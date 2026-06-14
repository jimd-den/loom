package com.example.domain.usecase

import android.util.Log
import com.example.data.remote.OpenRouterClient
import com.example.domain.model.*
import com.example.domain.repository.LoomRepository
import kotlinx.coroutines.flow.Flow
import java.util.regex.Pattern

class GetGoalsUseCase(private val repository: LoomRepository) {
    operator fun invoke(): Flow<List<Goal>> = repository.getGoalsFlow()
}

class CreateGoalUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(title: String): Long {
        val newGoal = Goal(title = title, status = GoalStatus.INTERVIEWING)
        return repository.insertGoal(newGoal)
    }
}

class UpdateGoalStatusUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(id: Long, status: GoalStatus) {
        repository.updateGoalStatus(id, status)
    }
}

class DeleteGoalUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(id: Long) {
        repository.deleteGoal(id)
    }
}

class GetGoalProfileUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(goalId: Long): GoalProfile? = repository.getGoalProfile(goalId)
}

class SaveGoalProfileUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(profile: GoalProfile) = repository.saveGoalProfile(profile)
}

class GetIdeaCardsUseCase(private val repository: LoomRepository) {
    operator fun invoke(goalId: Long): Flow<List<IdeaCard>> = repository.getIdeaCardsFlow(goalId)
}

class AddIdeaCardUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(goalId: Long, content: String, category: IdeaCategory): Long {
        val card = IdeaCard(goalId = goalId, content = content, category = category, isFuzzy = true)
        return repository.insertIdeaCard(card)
    }
}

class DeleteIdeaCardUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteIdeaCard(id)
}

class GetSourcesUseCase(private val repository: LoomRepository) {
    operator fun invoke(goalId: Long): Flow<List<Source>> = repository.getSourcesFlow(goalId)
}

class UpdateSourceStatusUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(id: Long, status: SourceStatus) {
        repository.updateSourceStatus(id, status)
    }
}

class UpdateSourceRawTextUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(id: Long, text: String) {
        repository.updateSourceRawText(id, text)
    }
}

class GetNotesUseCase(private val repository: LoomRepository) {
    operator fun invoke(sourceId: Long): Flow<List<StudyNote>> = repository.getNotesFlow(sourceId)
}

class AddNoteUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(sourceId: Long, heading: String, content: String): Long {
        val note = StudyNote(sourceId = sourceId, heading = heading, content = content)
        return repository.insertNote(note)
    }
}

class GetQuestionsUseCase(private val repository: LoomRepository) {
    operator fun invoke(sourceId: Long): Flow<List<RecallQuestion>> = repository.getQuestionsFlow(sourceId)
}

class SaveQuestionUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(question: RecallQuestion): Long = repository.insertQuestion(question)
}

class GetRecallQueueUseCase(private val repository: LoomRepository) {
    operator fun invoke(): Flow<List<RecallQuestion>> = repository.getAllQuestionsFlow()
}

class SaveApiKeyUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(key: String) = repository.saveApiKey(key)
}

class GetApiKeyUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(): String? = repository.getApiKey()
}

class GetTaskModelUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(taskKey: String, defaultValue: String): String {
        return repository.getTaskModel(taskKey, defaultValue)
    }
}

class SaveTaskModelUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(taskKey: String, model: String) {
        repository.saveTaskModel(taskKey, model)
    }
}

class FetchModelsUseCase(private val repository: LoomRepository) {
    suspend operator fun invoke(): List<OpenRouterClientModel> {
        val currentKey = repository.getApiKey() ?: ""
        if (currentKey.isEmpty()) {
            return getDefaultModelList()
        }
        return try {
            val response = OpenRouterClient.service.getModels()
            val list = response.data?.map { item ->
                OpenRouterClientModel(
                    id = item.id,
                    name = item.name,
                    description = item.description ?: "Available OpenRouter LLM"
                )
            }
            if (!list.isNullOrEmpty()) list else getDefaultModelList()
        } catch (e: Exception) {
            Log.e("FetchModelsUseCase", "Failed to fetch remote models, loading default catalog", e)
            getDefaultModelList()
        }
    }

    private fun getDefaultModelList(): List<OpenRouterClientModel> {
        return listOf(
            OpenRouterClientModel("google/gemini-2.5-flash", "Gemini 2.5 Flash", "Google's fast, highly analytical multimodal model (Recommended Default)"),
            OpenRouterClientModel("google/gemini-2.5-pro", "Gemini 2.5 Pro", "Google's deep reasoning and premium intelligence agent"),
            OpenRouterClientModel("meta-llama/llama-3.3-70b-instruct", "Llama 3.3 70B Instruct", "Meta's state-of-the-art open large model"),
            OpenRouterClientModel("deepseek/deepseek-chat", "DeepSeek V3 Chat", "Highly efficient, state of the art Chinese reasoning model"),
            OpenRouterClientModel("qwen/qwen-2.5-72b-instruct", "Qwen 2.5 72B Instruct", "Alibaba's robust multilingual coder and reasoner"),
            OpenRouterClientModel("google/gemini-2.5-flash:free", "Gemini 2.5 Flash (Free)", "Completely free-tier Gemini API client through OpenRouter")
        )
    }
}

data class OpenRouterClientModel(
    val id: String,
    val name: String,
    val description: String
)

// Main AI features UseCase orchestration
class LoomAiEngine(private val repository: LoomRepository) {

    private suspend fun getApiKeyOrThrow(): String {
        val key = repository.getApiKey()
        if (key.isNullOrBlank()) {
            throw Exception("OpenRouter API key is missing. Please save your API key in the Setup screens or settings first.")
        }
        return key
    }

    private suspend fun runAi(taskKey: String, systemPrompt: String, userPrompt: String): String {
        val apiKey = getApiKeyOrThrow()
        
        // Fetch preferences
        val defaultModel = when (taskKey) {
            "onboarding" -> "google/gemini-2.5-flash"
            "canvas" -> "google/gemini-2.5-flash"
            "source" -> "google/gemini-2.5-flash"
            "tutor" -> "google/gemini-2.5-flash"
            "questions" -> "google/gemini-2.5-flash"
            "evaluation" -> "google/gemini-2.5-flash"
            else -> "google/gemini-2.5-flash"
        }
        val defaultFallback = "google/gemini-2.5-flash"
        val model = repository.getTaskModel(taskKey, defaultModel)
        
        return OpenRouterClient.generateChat(
            apiKey = apiKey,
            model = model,
            fallbackModel = defaultFallback,
            systemPrompt = systemPrompt,
            userPrompt = userPrompt
        )
    }

    // Task 1: Onboarding Interview Questions Generator
    suspend fun generateInterviewQuestions(goalTitle: String): List<String> {
        val systemPrompt = """
            You are Miyamoto, the zen game-designer of learning. Your mission is to help people turn any raw goal into a living active-reading study environment.
            The user wants to study: "$goalTitle"
            Generate 4 short, beautiful, highly supportive, clarifying questions.
            Focus on uncovering:
            1. Their explicit intent: why this deeply matters to them?
            2. Current context & skill: what can they already do or build?
            3. Specific desired outcome: what does "success" look like?
            4. Constraints: What is confusing or difficult right now, and do they want fast speed, broad mapping, or deep rigor?
            Format output STRICTLY as single-line questions, prefixed with Q1, Q2, Q3, Q4. Make them friendly, inviting, and highly motivating.
        """.trimIndent()

        val text = runAi("onboarding", systemPrompt, "Let's explore this path!")
        val questions = mutableListOf<String>()
        val pattern = Pattern.compile("Q\\d+[:\\x20-]*(.+)", Pattern.CASE_INSENSITIVE)
        text.lines().forEach { line ->
            val matcher = pattern.matcher(line)
            if (matcher.find()) {
                questions.add(matcher.group(1).trim())
            }
        }
        if (questions.size < 3) {
            // Safe fallbacks
            return listOf(
                "Why does mastering '$goalTitle' matter to you right now?",
                "What is your starting point: what has been your exposure or what can you build?",
                "What would success look like: are you aiming to design a project, clear an exam, or satisfy intense curiosity?",
                "What is the single most confusing or intimidating aspect of this topic to you?"
            )
        }
        return questions
    }

    // Task 2: Process interview answers and generate initial Idea Cards and Source Suggestions
    suspend fun processInterview(
        goalId: Long,
        goalTitle: String,
        questions: List<String>,
        answers: List<String>
    ): Pair<GoalProfile, List<IdeaCard>> {
        val userContext = questions.zip(answers).joinToString("\n") { (q, a) -> "Q: $q\nA: $a" }
        val systemPrompt = """
            You are Miyamoto. The learner has completed their goal onboarding.
            Goal: $goalTitle
            Context:
            $userContext
            
            Based on this, synthesize:
            1. An executive intent summary, their level, primary outcome, timehorizon/constraints, and study mode preferences.
            2. Create 6 foundational "Idea Cards" to populate their interactive study workspace.
            
            Format your entire response like this, so we can parse it reliably:
            [PROFILE]
            Intent: <brief summary of why it matters to them>
            Level: <Beginner, Intermediate, or Advanced>
            Outcome: <what success looks like>
            Constraints: <constraints and struggles>
            Density: <Rigor, Breadth, or Speed>
            [/PROFILE]
            
            [CARDS]
            - CATEGORY: CONCEPT | CONTENT: <Core prerequisite concept name 1>
            - CATEGORY: QUESTION | CONTENT: <An essential first query to search or resolve>
            - CATEGORY: VOCABULARY | CONTENT: <A crucial term they must master immediately>
            - CATEGORY: EXAMPLE | CONTENT: <An illustrative prototype case study or exercise>
            - CATEGORY: CONCEPT | CONTENT: <A foundational background skill or prerequisite>
            - CATEGORY: QUESTION | CONTENT: <An inspiring next question to answer>
            [/CARDS]
        """.trimIndent()

        val text = runAi("onboarding", systemPrompt, "We are ready. Build our learning map projection.")
        
        // Parse Profile
        var intent = "Deepen understanding of $goalTitle"
        var level = "Beginner"
        var outcome = "Mastery and building key elements"
        var constraints = "Time limits"
        var density = "Balanced"

        val profilePattern = Pattern.compile("\\[PROFILE\\](.*)\\[/PROFILE\\]", Pattern.DOTALL)
        val profileMatcher = profilePattern.matcher(text)
        if (profileMatcher.find()) {
            val block = profileMatcher.group(1)
            block.lines().forEach { line ->
                val parts = line.split(":", limit = 2)
                if (parts.size == 2) {
                    val k = parts[0].trim().lowercase()
                    val v = parts[1].trim()
                    when {
                        k.contains("intent") -> intent = v
                        k.contains("level") -> level = v
                        k.contains("outcome") -> outcome = v
                        k.contains("constraint") -> constraints = v
                        k.contains("density") -> density = v
                    }
                }
            }
        }

        val profile = GoalProfile(
            id = goalId,
            intent = intent,
            level = level,
            outcome = outcome,
            constraints = constraints,
            depthBreadthSpeed = density
        )

        // Parse Cards
        val cards = mutableListOf<IdeaCard>()
        val cardPattern = Pattern.compile("-\\s*CATEGORY:\\s*(\\w+)\\s*\\|\\s*CONTENT:\\s*(.+)", Pattern.CASE_INSENSITIVE)
        text.lines().forEach { line ->
            val matcher = cardPattern.matcher(line)
            if (matcher.find()) {
                val catStr = matcher.group(1).trim().uppercase()
                val content = matcher.group(2).trim()
                val category = try { IdeaCategory.valueOf(catStr) } catch(e: Exception) { IdeaCategory.CONCEPT }
                cards.add(IdeaCard(goalId = goalId, content = content, category = category, isFuzzy = true))
            }
        }

        if (cards.isEmpty()) {
            // Default safe starting deck
            cards.add(IdeaCard(goalId = goalId, content = "Identify core components of $goalTitle", category = IdeaCategory.CONCEPT))
            cards.add(IdeaCard(goalId = goalId, content = "What are the absolute prerequisites for $goalTitle?", category = IdeaCategory.QUESTION))
            cards.add(IdeaCard(goalId = goalId, content = "Key terminology for beginners", category = IdeaCategory.VOCABULARY))
        }

        return Pair(profile, cards)
    }

    // Task 3: Suggest tailored Sources for reading based on the goal and profile context
    suspend fun suggestSources(goalId: Long, goalTitle: String, profile: GoalProfile): List<Source> {
        val systemPrompt = """
            You are Miyamoto. Recommend exactly 4 high-quality reading sources (textbooks, reference documentation, comprehensive articles, or research guides) for:
            Goal: $goalTitle
            Profile Context: ${profile.intent}, Level: ${profile.level}, Desired Outcomes: ${profile.outcome}, Time Horizon: ${profile.depthBreadthSpeed}
            
            Format each suggested source exactly using these blocks, repeating 4 times:
            [SOURCE]
            TITLE: <Name of Article or Document>
            URL: <URL or name representing source, e.g. wikipedia.org/wiki/Subject or devdocs.com>
            WHY: <Why this specific source is urgent and critical for their context>
            UNLOCKS: <What specific concept or capability this source unlocks>
            DIFFICULTY: <BEGINNER or INTERMEDIATE or ADVANCED>
            EFFORT: <Estimated reading effort, e.g. '15 mins' or '45 mins'>
            NEXT: <Highly focused first action, e.g. 'Survey introduction section and highlight terminology'>
            [/SOURCE]
        """.trimIndent()

        val text = runAi("source", systemPrompt, "Generate sources list.")
        val sources = mutableListOf<Source>()

        val sourcePattern = Pattern.compile("\\[SOURCE\\](.*?)\\[/SOURCE\\]", Pattern.DOTALL)
        val matcher = sourcePattern.matcher(text)
        while (matcher.find()) {
            val block = matcher.group(1)
            var title = ""
            var url: String? = null
            var why = ""
            var unlocks = ""
            var difficulty = Difficulty.INTERMEDIATE
            var effort = "15 mins"
            var next = ""

            block.lines().forEach { line ->
                val parts = line.split(":", limit = 2)
                if (parts.size == 2) {
                    val k = parts[0].trim().uppercase()
                    val v = parts[1].trim()
                    when (k) {
                        "TITLE" -> title = v
                        "URL" -> url = v
                        "WHY" -> why = v
                        "UNLOCKS" -> unlocks = v
                        "DIFFICULTY" -> difficulty = try { Difficulty.valueOf(v.uppercase()) } catch(e:Exception){ Difficulty.INTERMEDIATE }
                        "EFFORT" -> effort = v
                        "NEXT" -> next = v
                    }
                }
            }
            if (title.isNotEmpty()) {
                sources.add(
                    Source(
                        goalId = goalId,
                        title = title,
                        url = url,
                        whyItMatters = why,
                        whatItUnlocks = unlocks,
                        difficulty = difficulty,
                        readingEffort = effort,
                        nextAction = next,
                        status = SourceStatus.SUGGESTED
                    )
                )
            }
        }

        if (sources.isEmpty()) {
            // Return excellent default curated resources
            sources.add(
                Source(
                    goalId = goalId,
                    title = "Wikipedia: $goalTitle",
                    url = "https://en.wikipedia.org/wiki/${goalTitle.replace(" ", "_")}",
                    whyItMatters = "Surveys the complete conceptual landscape, standard terminology, and historical context.",
                    whatItUnlocks = "A complete, unbiased table of contents and structural bird's-eye view.",
                    difficulty = Difficulty.BEGINNER,
                    readingEffort = "20 mins",
                    nextAction = "Skim the lead paragraph and structure of sections.",
                    status = SourceStatus.SUGGESTED
                )
            )
            sources.add(
                Source(
                    goalId = goalId,
                    title = "Getting Started with $goalTitle: Essential Core Guide",
                    url = "https://devdocs.io/${goalTitle.lowercase().replace(" ", "-")}",
                    whyItMatters = "Interactive core manual outlining modern standard implementation standards.",
                    whatItUnlocks = "Pragmatic, syntactical competence and configuration layouts.",
                    difficulty = Difficulty.INTERMEDIATE,
                    readingEffort = "30 mins",
                    nextAction = "Extract first code/implementation schema template.",
                    status = SourceStatus.SUGGESTED
                )
            )
        }

        return sources
    }

    // Task 4: Suggest more ideas for the Canvas, based on current items
    suspend fun generateCanvasSuggestions(goalTitle: String, currentCards: List<IdeaCard>): List<IdeaCard> {
        val itemsStr = currentCards.joinToString { "- [${it.category.name}] ${it.content}" }
        val systemPrompt = """
            You are Miyamoto. Based on the user's study goal: "$goalTitle"
            And their current canvas cards list:
            $itemsStr
            
            Generate 5 smart, highly custom, related idea cards to reveal next, like dependencies, missing vocabulary, or key doubts.
            Format each card as:
            - CATEGORY: <CONCEPT | QUESTION | VOCABULARY | EXAMPLE> | CONTENT: <Card topic content>
        """.trimIndent()

        val text = runAi("canvas", systemPrompt, "Suggest next learning fragments.")
        val suggestions = mutableListOf<IdeaCard>()
        val cardPattern = Pattern.compile("-\\s*CATEGORY:\\s*(\\w+)\\s*\\|\\s*CONTENT:\\s*(.+)", Pattern.CASE_INSENSITIVE)
        
        text.lines().forEach { line ->
            val matcher = cardPattern.matcher(line)
            if (matcher.find()) {
                val catStr = matcher.group(1).trim().uppercase()
                val content = matcher.group(2).trim()
                val category = try { IdeaCategory.valueOf(catStr) } catch(e: Exception) { IdeaCategory.CONCEPT }
                suggestions.add(IdeaCard(goalId = 0, content = content, category = category, isFuzzy = true))
            }
        }
        return suggestions
    }

    // Task 5: Mock or fetch some simulated content text for any Source if the user starts reading it.
    // In a real database/production app, we fetch the parsed web content, but we can write an intelligent generator 
    // to build a 4-section SQ5R study guide for the selected Source using OpenRouter, complete with headings, paragraphs, and active questions!
    suspend fun generateSourceDummyText(source: Source): String {
        val systemPrompt = """
            You are Miyamoto. The user has accepted raw source: "${source.title}" representing: ${source.whatItUnlocks}
            Generate an incredible, comprehensive, highly educational markdown-based study chapter for this source.
            It must contain exactly 4 standard structured sections, representing the SQ5R 'Survey' layout.
            Structure it strictly with markdown headers (using ## and ###).
            Keep it deeply realistic, informative, and detailed, containing real expert insights and knowledge, not generic mock text.
        """.trimIndent()

        return runAi("tutor", systemPrompt, "Draft full 4-section study chapter.")
    }

    // Task 6: SQ5R Assistance
    // 6a: Heading to active recall question conversion
    suspend fun generateHeadingQuestions(heading: String, textBlock: String): List<String> {
        val systemPrompt = """
            In SQ5R, converting headers into active inquiry questions prior to reading dramatically boosts comprehension.
            Heading: "$heading"
            Passage Context:
            $textBlock
            
            Create 3 sharp, analytical questions that this passage directly answers. 
            Format them strictly as numbered list item lines:
            1. <Question 1>
            2. <Question 2>
            3. <Question 3>
        """.trimIndent()

        val text = runAi("questions", systemPrompt, "Generate 3 questions.")
        val questions = mutableListOf<String>()
        val regex = Pattern.compile("\\d+\\.\\s*(.+)")
        text.lines().forEach { line ->
            val m = regex.matcher(line)
            if (m.find()) {
                questions.add(m.group(1).trim())
            }
        }
        if (questions.isEmpty()) {
            return listOf(
                "What is the foundational thesis of $heading?",
                "What is a critical real-world illustration of $heading?",
                "How does $heading compare to other standard concepts?"
            )
        }
        return questions
    }

    // 6b: Passage simplification / analogies
    suspend fun explainPassage(actionType: String, passageText: String): String {
        val modePrompt = when (actionType) {
            "SIMPLIFY" -> "Rewrite this passage in extremely clear, high-comprehension sentences suitable for rapid uptake. Lay out the arguments in neat bullet points."
            "ANALOGY" -> "Provide an inspiring, creative, and memorable real-world game-design or physical analogy to make this abstract concept stick immediately."
            "CHALLENGE" -> "Formulate an intellectually challenging puzzle, mental model validation, or application scenario question based on this text."
            else -> "Explain this deeply, highlighting key items."
        }

        val systemPrompt = """
            You are Miyamoto. Simplify learning and boost retention.
            Action requested: $modePrompt
            Passage text:
            $passageText
        """.trimIndent()

        return runAi("tutor", systemPrompt, "Help me process this.")
    }

    // Task 7: Evaluate active recall answers
    suspend fun evaluateRecallAnswer(
        questionText: String,
        passageText: String,
        userAnswer: String
    ): Pair<RecallStatus, String> {
        val systemPrompt = """
            You are Miyamoto, the learning coach. You evaluate active retrieval recall.
            The user attempted to answer an active recall question based on the textbook passage.
            
            Question: "$questionText"
            Source Reference Passage:
            $passageText
            
            User's Typed Answer:
            "$userAnswer"
            
            Assess if the answer is conceptually correct and catches the core items of the passage context.
            Respond in this exact format:
            [SCORE] <MASTERED or RETRY>
            [FEEDBACK]
            <Provide a supportive, game-designer style explanation of what they nailed, what details they missed, and the precise truth from the source context. Keep it highly inspiring!>
        """.trimIndent()

        val response = runAi("evaluation", systemPrompt, "Analyze user recall attempt.")
        
        var status = RecallStatus.RETRY
        var feedback = "Unable to process assessment. Keep exploring!"

        if (response.contains("MASTERED")) {
            status = RecallStatus.MASTERED
        }

        val feedbackPattern = Pattern.compile("\\[FEEDBACK\\](.*)", Pattern.DOTALL)
        val matcher = feedbackPattern.matcher(response)
        if (matcher.find()) {
            feedback = matcher.group(1).trim()
        } else {
            // Fallback parsing
            feedback = response.replace("[SCORE] MASTERED", "").replace("[SCORE] RETRY", "").replace("[FEEDBACK]", "").trim()
        }

        return Pair(status, feedback)
    }
}
