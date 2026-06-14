package com.example.domain.repository

import com.example.domain.model.*
import kotlinx.coroutines.flow.Flow

interface LoomRepository {
    // Settings
    suspend fun getApiKey(): String?
    suspend fun saveApiKey(key: String)
    suspend fun getTaskModel(taskKey: String, defaultModel: String): String
    suspend fun saveTaskModel(taskKey: String, model: String)

    // Goals
    fun getGoalsFlow(): Flow<List<Goal>>
    suspend fun getGoalById(id: Long): Goal?
    suspend fun insertGoal(goal: Goal): Long
    suspend fun updateGoalStatus(id: Long, status: GoalStatus)
    suspend fun deleteGoal(id: Long)

    // Goal Profile
    suspend fun getGoalProfile(goalId: Long): GoalProfile?
    suspend fun saveGoalProfile(profile: GoalProfile)

    // Idea Cards
    fun getIdeaCardsFlow(goalId: Long): Flow<List<IdeaCard>>
    suspend fun insertIdeaCard(card: IdeaCard): Long
    suspend fun deleteIdeaCard(id: Long)
    suspend fun updateIdeaCardFuzzy(id: Long, isFuzzy: Boolean)

    // Sources
    fun getSourcesFlow(goalId: Long): Flow<List<Source>>
    suspend fun getSourceById(id: Long): Source?
    suspend fun insertSource(source: Source): Long
    suspend fun updateSourceStatus(id: Long, status: SourceStatus)
    suspend fun updateSourceRawText(id: Long, text: String)

    // Notes
    fun getNotesFlow(sourceId: Long): Flow<List<StudyNote>>
    suspend fun insertNote(note: StudyNote): Long
    suspend fun deleteNote(id: Long)

    // Recall Questions
    fun getQuestionsFlow(sourceId: Long): Flow<List<RecallQuestion>>
    fun getAllQuestionsFlow(): Flow<List<RecallQuestion>>
    suspend fun insertQuestion(question: RecallQuestion): Long
    suspend fun updateQuestionRecall(
        id: Long, 
        status: RecallStatus, 
        lastRecallText: String?, 
        lastRecallAssessment: String?, 
        nextReviewTime: Long
    )
}
