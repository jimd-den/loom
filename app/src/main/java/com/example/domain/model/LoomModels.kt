package com.example.domain.model

import java.io.Serializable

data class Goal(
    val id: Long = 0,
    val title: String,
    val status: GoalStatus = GoalStatus.INTERVIEWING,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

enum class GoalStatus {
    INTERVIEWING,
    ACTIVE,
    COMPLETED,
    ARCHIVED
}

data class GoalProfile(
    val id: Long, // Matches goalId
    val intent: String = "",
    val level: String = "",
    val outcome: String = "",
    val timeHorizon: String = "",
    val constraints: String = "",
    val depthBreadthSpeed: String = "Balanced"
) : Serializable

enum class IdeaCategory {
    CONCEPT,
    QUESTION,
    VOCABULARY,
    EXAMPLE,
    LINK
}

data class IdeaCard(
    val id: Long = 0,
    val goalId: Long,
    val content: String,
    val category: IdeaCategory = IdeaCategory.CONCEPT,
    val isFuzzy: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

enum class SourceStatus {
    SUGGESTED,
    ACCEPTED,
    COMPLETED
}

data class Source(
    val id: Long = 0,
    val goalId: Long,
    val title: String,
    val url: String? = null,
    val whyItMatters: String = "",
    val whatItUnlocks: String = "",
    val difficulty: Difficulty = Difficulty.INTERMEDIATE,
    val readingEffort: String = "15 mins",
    val nextAction: String = "",
    val status: SourceStatus = SourceStatus.SUGGESTED,
    val rawText: String? = null
) : Serializable

data class StudyNote(
    val id: Long = 0,
    val sourceId: Long,
    val heading: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

enum class RecallStatus {
    UNTESTED,
    MASTERED,
    RETRY
}

data class RecallQuestion(
    val id: Long = 0,
    val sourceId: Long,
    val questionText: String,
    val passageId: Long? = null,
    val recallStatus: RecallStatus = RecallStatus.UNTESTED,
    val lastRecallText: String? = null,
    val lastRecallAssessment: String? = null,
    val nextReviewTime: Long = System.currentTimeMillis(),
    val isUserGenerated: Boolean = false
) : Serializable

data class ModelConfig(
    val key: String,
    val displayName: String,
    val description: String,
    val selectedModel: String,
    val fallbackModel: String
) : Serializable
