package com.example.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.*
import com.example.domain.repository.LoomRepository
import com.example.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoomViewModel(private val repository: LoomRepository) : ViewModel() {

    private val getGoalsUseCase = GetGoalsUseCase(repository)
    private val createGoalUseCase = CreateGoalUseCase(repository)
    private val updateGoalStatusUseCase = UpdateGoalStatusUseCase(repository)
    private val deleteGoalUseCase = DeleteGoalUseCase(repository)
    private val getGoalProfileUseCase = GetGoalProfileUseCase(repository)
    private val saveGoalProfileUseCase = SaveGoalProfileUseCase(repository)
    private val getIdeaCardsUseCase = GetIdeaCardsUseCase(repository)
    private val addIdeaCardUseCase = AddIdeaCardUseCase(repository)
    private val deleteIdeaCardUseCase = DeleteIdeaCardUseCase(repository)
    private val getSourcesUseCase = GetSourcesUseCase(repository)
    private val updateSourceStatusUseCase = UpdateSourceStatusUseCase(repository)
    private val updateSourceRawTextUseCase = UpdateSourceRawTextUseCase(repository)
    private val getNotesUseCase = GetNotesUseCase(repository)
    private val addNoteUseCase = AddNoteUseCase(repository)
    private val getQuestionsUseCase = GetQuestionsUseCase(repository)
    private val saveQuestionUseCase = SaveQuestionUseCase(repository)
    private val getRecallQueueUseCase = GetRecallQueueUseCase(repository)
    private val fetchModelsUseCase = FetchModelsUseCase(repository)
    
    private val aiEngine = LoomAiEngine(repository)

    // Reactive StateFlows
    val goals: StateFlow<List<Goal>> = getGoalsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recallQueue: StateFlow<List<RecallQuestion>> = getRecallQueueUseCase()
        .map { list -> list.filter { it.recallStatus != RecallStatus.MASTERED } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // API Configurations
    private val _apiKey = MutableStateFlow<String>("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _availableModels = MutableStateFlow<List<OpenRouterClientModel>>(emptyList())
    val availableModels: StateFlow<List<OpenRouterClientModel>> = _availableModels.asStateFlow()

    // Screen-specific state bindings
    private val _currentGoal = MutableStateFlow<Goal?>(null)
    val currentGoal: StateFlow<Goal?> = _currentGoal.asStateFlow()

    private val _currentProfile = MutableStateFlow<GoalProfile?>(null)
    val currentProfile: StateFlow<GoalProfile?> = _currentProfile.asStateFlow()

    // Flow for active goal canvas elements
    val currentIdeaCards: StateFlow<List<IdeaCard>> = _currentGoal
        .flatMapLatest { goal ->
            if (goal != null) getIdeaCardsUseCase(goal.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow for active goal sources elements
    val currentSources: StateFlow<List<Source>> = _currentGoal
        .flatMapLatest { goal ->
            if (goal != null) getSourcesUseCase(goal.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reading / Study view states
    private val _activeSource = MutableStateFlow<Source?>(null)
    val activeSource: StateFlow<Source?> = _activeSource.asStateFlow()

    private val _sourceText = MutableStateFlow<String>("")
    val sourceText: StateFlow<String> = _sourceText.asStateFlow()

    val activeSourceNotes: StateFlow<List<StudyNote>> = _activeSource
        .flatMapLatest { src ->
            if (src != null) getNotesUseCase(src.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSourceQuestions: StateFlow<List<RecallQuestion>> = _activeSource
        .flatMapLatest { src ->
            if (src != null) getQuestionsUseCase(src.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Onboarding process states
    private val _onboardingQuestions = MutableStateFlow<List<String>>(emptyList())
    val onboardingQuestions: StateFlow<List<String>> = _onboardingQuestions.asStateFlow()

    private val _interviewAnswers = MutableStateFlow<List<String>>(emptyList())
    val interviewAnswers: StateFlow<List<String>> = _interviewAnswers.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    // Shared UI Loaders
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiStateMessage = MutableStateFlow("")
    val aiStateMessage: StateFlow<String> = _aiStateMessage.asStateFlow()

    private val _tutorFeedback = MutableStateFlow("")
    val tutorFeedback: StateFlow<String> = _tutorFeedback.asStateFlow()

    // Active Recall state holders
    private val _recallFeedback = MutableStateFlow<Pair<RecallStatus, String>?>(null)
    val recallFeedback: StateFlow<Pair<RecallStatus, String>?> = _recallFeedback.asStateFlow()

    // Model Preferences Per Task Key (e.g. "onboarding", "canvas", "source")
    val modelOnboarding = repository.observeModelPreference("onboarding", "google/gemini-2.5-flash")
    val modelCanvas = repository.observeModelPreference("canvas", "google/gemini-2.5-flash")
    val modelSource = repository.observeModelPreference("source", "google/gemini-2.5-flash")
    val modelTutor = repository.observeModelPreference("tutor", "google/gemini-2.5-flash")
    val modelQuestions = repository.observeModelPreference("questions", "google/gemini-2.5-flash")
    val modelEvaluation = repository.observeModelPreference("evaluation", "google/gemini-2.5-flash")

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _apiKey.value = repository.getApiKey() ?: ""
            refreshModels()
        }
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            repository.saveApiKey(key)
            _apiKey.value = key
            refreshModels()
        }
    }

    fun saveTaskModel(taskKey: String, model: String) {
        viewModelScope.launch {
            repository.saveTaskModel(taskKey, model)
        }
    }

    fun refreshModels() {
        viewModelScope.launch {
            _availableModels.value = fetchModelsUseCase()
        }
    }

    // Action: Plant a new Study Goal
    fun plantGoal(title: String, onSuccess: (Long) -> Unit) {
        if (title.isBlank()) return
        _isAiLoading.value = true
        _aiStateMessage.value = "Miyamoto is structuring your path layout..."
        viewModelScope.launch {
            try {
                val goalId = createGoalUseCase(title)
                val goal = repository.getGoalById(goalId)
                _currentGoal.value = goal
                
                // Generate Interview Questions
                _aiStateMessage.value = "Formulating clarifying questions..."
                val questions = aiEngine.generateInterviewQuestions(title)
                _onboardingQuestions.value = questions
                _interviewAnswers.value = List(questions.size) { "" }
                _currentQuestionIndex.value = 0
                _isAiLoading.value = false
                
                onSuccess(goalId)
            } catch (e: Exception) {
                Log.e("LoomViewModel", "Failed to plant goal: ${e.message}", e)
                _isAiLoading.value = false
            }
        }
    }

    // Action: Answer an Interview question
    fun submitAnswer(answer: String, onCompleted: () -> Unit) {
        val currIndex = _currentQuestionIndex.value
        val questions = _onboardingQuestions.value
        if (currIndex >= questions.size) return

        val answersCopy = _interviewAnswers.value.toMutableList()
        answersCopy[currIndex] = answer
        _interviewAnswers.value = answersCopy

        if (currIndex + 1 < questions.size) {
            _currentQuestionIndex.value = currIndex + 1
        } else {
            // Completed! Process onboarding answers
            processInterviewAnswers(onCompleted)
        }
    }

    private fun processInterviewAnswers(onCompleted: () -> Unit) {
        val goal = _currentGoal.value ?: return
        _isAiLoading.value = true
        _aiStateMessage.value = "Miyamoto is compiling your mental profile projection..."
        viewModelScope.launch {
            try {
                val (profile, cards) = aiEngine.processInterview(
                    goalId = goal.id,
                    goalTitle = goal.title,
                    questions = _onboardingQuestions.value,
                    answers = _interviewAnswers.value
                )
                // Persist Profile
                saveGoalProfileUseCase(profile)
                _currentProfile.value = profile

                // Save Starting Cards
                cards.forEach { card ->
                    repository.insertIdeaCard(card)
                }

                // Suggest Core Sources
                _aiStateMessage.value = "Scouting the web for pristine guides..."
                val sources = aiEngine.suggestSources(goal.id, goal.title, profile)
                sources.forEach { source ->
                    repository.insertSource(source)
                }

                // Promote goal to ACTIVE status
                updateGoalStatusUseCase(goal.id, GoalStatus.ACTIVE)
                _currentGoal.value = goal.copy(status = GoalStatus.ACTIVE)

                _isAiLoading.value = false
                onCompleted()
            } catch (e: Exception) {
                Log.e("LoomViewModel", "Failed to synthesize interview results: ${e.message}", e)
                _isAiLoading.value = false
            }
        }
    }

    // Action: Launch existing goal
    fun selectGoal(goal: Goal) {
        _currentGoal.value = goal
        viewModelScope.launch {
            _currentProfile.value = getGoalProfileUseCase(goal.id)
        }
    }

    // Action: Delete a goal
    fun removeGoal(id: Long) {
        viewModelScope.launch {
            deleteGoalUseCase(id)
            if (_currentGoal.value?.id == id) {
                _currentGoal.value = null
                _currentProfile.value = null
            }
        }
    }

    // Action: Add manual card on canvas
    fun addCanvasCard(content: String, category: IdeaCategory) {
        val goal = _currentGoal.value ?: return
        viewModelScope.launch {
            addIdeaCardUseCase(goalId = goal.id, content = content, category = category)
        }
    }

    // Action: Delete card from canvas
    fun removeCanvasCard(id: Long) {
        viewModelScope.launch {
            deleteIdeaCardUseCase(id)
        }
    }

    // Action: Canvas card marked clear/fuzzy toggle
    fun toggleCardFuzzy(id: Long, isFuzzy: Boolean) {
        viewModelScope.launch {
            repository.updateIdeaCardFuzzy(id, isFuzzy)
        }
    }

    // Action: Trigger AI canvas additions (suggest new cards based on current mesh)
    fun chimInCanvas() {
        val goal = _currentGoal.value ?: return
        _isAiLoading.value = true
        _aiStateMessage.value = "Analyzing current knowledge fragments..."
        viewModelScope.launch {
            try {
                val suggestions = aiEngine.generateCanvasSuggestions(goal.title, currentIdeaCards.value)
                suggestions.forEach { card ->
                    repository.insertIdeaCard(card.copy(goalId = goal.id))
                }
                _isAiLoading.value = false
            } catch (e: Exception) {
                Log.e("LoomViewModel", "Canvas chiming in failed: ${e.message}", e)
                _isAiLoading.value = false
            }
        }
    }

    // Action: Accept Source card on Discovery lanes
    fun acceptSource(sourceId: Long) {
        viewModelScope.launch {
            updateSourceStatusUseCase(sourceId, SourceStatus.ACCEPTED)
        }
    }

    // Action: Reject/Archive Source card
    fun archiveSource(sourceId: Long) {
        viewModelScope.launch {
            updateSourceStatusUseCase(sourceId, SourceStatus.SUGGESTED) // toggle back or can delete
        }
    }

    // Action: Read / Open a source
    fun openSource(source: Source) {
        _activeSource.value = source
        _sourceText.value = source.rawText ?: ""
        _tutorFeedback.value = ""
        _recallFeedback.value = null

        if (source.rawText.isNullOrBlank()) {
            _isAiLoading.value = true
            _aiStateMessage.value = "Compiling 4-section SQ5R study guide..."
            viewModelScope.launch {
                try {
                    val fullText = aiEngine.generateSourceDummyText(source)
                    updateSourceRawTextUseCase(source.id, fullText)
                    _sourceText.value = fullText
                    _activeSource.value = source.copy(rawText = fullText)
                    
                    // Generate first retrieval questions automatically
                    _aiStateMessage.value = "Mapping critical retrieval prompts..."
                    val questions = aiEngine.generateHeadingQuestions(source.title, fullText.take(1500))
                    questions.forEach { text ->
                        saveQuestionUseCase(RecallQuestion(sourceId = source.id, questionText = text))
                    }
                    _isAiLoading.value = false
                } catch (e: Exception) {
                    Log.e("LoomViewModel", "Failed loading source chapter: ${e.message}", e)
                    _isAiLoading.value = false
                }
            }
        }
    }

    // Action: Custom Passage tutor (SIMPLIFY, ANALOGY, CHALLENGE)
    fun requestTutorAid(mode: String, passage: String) {
        if (passage.isBlank()) return
        _isAiLoading.value = true
        _aiStateMessage.value = "Miyamoto is refracting this concept..."
        viewModelScope.launch {
            try {
                val result = aiEngine.explainPassage(mode, passage)
                _tutorFeedback.value = result
                _isAiLoading.value = false
            } catch (e: Exception) {
                Log.e("LoomViewModel", "Tutor request aborted: ${e.message}", e)
                _isAiLoading.value = false
            }
        }
    }

    // Action: Add custom notes highlighted
    fun saveHeadingNote(heading: String, textNote: String) {
        val src = _activeSource.value ?: return
        if (textNote.isBlank()) return
        viewModelScope.launch {
            addNoteUseCase(sourceId = src.id, heading = heading, content = textNote)
        }
    }

    // Action: Add custom retrieval question
    fun createCustomRecallQuestion(questionText: String) {
        val src = _activeSource.value ?: return
        if (questionText.isBlank()) return
        viewModelScope.launch {
            saveQuestionUseCase(RecallQuestion(sourceId = src.id, questionText = questionText, isUserGenerated = true))
        }
    }

    // Action: Verify retrieval answer (Recite & Review)
    fun submitRecallAnswer(question: RecallQuestion, typedAnswer: String) {
        if (typedAnswer.isBlank()) return
        _isAiLoading.value = true
        _aiStateMessage.value = "Judging conceptual fidelity..."
        
        // Find reference passage for this question (either source guide or custom)
        val textRef = _sourceText.value.take(2000)

        viewModelScope.launch {
            try {
                val (scoreStatus, assessment) = aiEngine.evaluateRecallAnswer(
                    questionText = question.questionText,
                    passageText = textRef,
                    userAnswer = typedAnswer
                )

                _recallFeedback.value = Pair(scoreStatus, assessment)
                
                // Save recall trial parameters & review interval (e.g. standard spaced recall interval: +1 day if mastered, +1 hour if retry)
                val spacingInterval = if (scoreStatus == RecallStatus.MASTERED) 24*60*60*1000L else 30*60*1000L
                repository.updateQuestionRecall(
                    id = question.id,
                    status = scoreStatus,
                    lastRecallText = typedAnswer,
                    lastRecallAssessment = assessment,
                    nextReviewTime = System.currentTimeMillis() + spacingInterval
                )
                
                _isAiLoading.value = false
            } catch (e: Exception) {
                Log.e("LoomViewModel", "Assessment scoring failed: ${e.message}", e)
                _isAiLoading.value = false
            }
        }
    }

    fun clearRecallFeedback() {
        _recallFeedback.value = null
    }

    // Settings helpers for task specific mapping Flow observation
    private fun LoomRepository.observeModelPreference(taskKey: String, defaultValue: String): Flow<String> = flow {
        while (true) {
            emit(getTaskModel(taskKey, defaultValue))
            kotlinx.coroutines.delay(2000) // Poll-sync setup for simplified presentation
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), defaultValue)
}

@Suppress("UNCHECKED_CAST")
class LoomViewModelFactory(private val repository: LoomRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoomViewModel::class.java)) {
            return LoomViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
