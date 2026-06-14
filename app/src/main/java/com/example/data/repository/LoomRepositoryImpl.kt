package com.example.data.repository

import com.example.data.local.*
import com.example.domain.model.*
import com.example.domain.repository.LoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoomRepositoryImpl(private val db: AppDatabase) : LoomRepository {

    override suspend fun getApiKey(): String? {
        return db.settingsDao().getSetting("openrouter_api_key")?.value
    }

    override suspend fun saveApiKey(key: String) {
        db.settingsDao().insertSetting(SettingEntity("openrouter_api_key", key))
    }

    override suspend fun getTaskModel(taskKey: String, defaultModel: String): String {
        return db.settingsDao().getSetting("model_$taskKey")?.value ?: defaultModel
    }

    override suspend fun saveTaskModel(taskKey: String, model: String) {
        db.settingsDao().insertSetting(SettingEntity("model_$taskKey", model))
    }

    override fun getGoalsFlow(): Flow<List<Goal>> {
        return db.goalDao().getAllGoalsFlow().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getGoalById(id: Long): Goal? {
        return db.goalDao().getGoalById(id)?.toDomain()
    }

    override suspend fun insertGoal(goal: Goal): Long {
        return db.goalDao().insertGoal(GoalEntity.fromDomain(goal))
    }

    override suspend fun updateGoalStatus(id: Long, status: GoalStatus) {
        db.goalDao().updateGoalStatus(id, status.name)
    }

    override suspend fun deleteGoal(id: Long) {
        db.goalDao().deleteGoal(id)
    }

    override suspend fun getGoalProfile(goalId: Long): GoalProfile? {
        return db.goalProfileDao().getGoalProfile(goalId)?.toDomain()
    }

    override suspend fun saveGoalProfile(profile: GoalProfile) {
        db.goalProfileDao().insertGoalProfile(GoalProfileEntity.fromDomain(profile))
    }

    override fun getIdeaCardsFlow(goalId: Long): Flow<List<IdeaCard>> {
        return db.ideaCardDao().getIdeaCardsFlow(goalId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertIdeaCard(card: IdeaCard): Long {
        return db.ideaCardDao().insertIdeaCard(IdeaCardEntity.fromDomain(card))
    }

    override suspend fun deleteIdeaCard(id: Long) {
        db.ideaCardDao().deleteIdeaCard(id)
    }

    override suspend fun updateIdeaCardFuzzy(id: Long, isFuzzy: Boolean) {
        db.ideaCardDao().updateIdeaCardFuzzy(id, isFuzzy)
    }

    override fun getSourcesFlow(goalId: Long): Flow<List<Source>> {
        return db.sourceDao().getSourcesFlow(goalId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getSourceById(id: Long): Source? {
        return db.sourceDao().getSourceById(id)?.toDomain()
    }

    override suspend fun insertSource(source: Source): Long {
        return db.sourceDao().insertSource(SourceEntity.fromDomain(source))
    }

    override suspend fun updateSourceStatus(id: Long, status: SourceStatus) {
        db.sourceDao().updateStatus(id, status.name)
    }

    override suspend fun updateSourceRawText(id: Long, text: String) {
        db.sourceDao().updateRawText(id, text)
    }

    override fun getNotesFlow(sourceId: Long): Flow<List<StudyNote>> {
        return db.noteDao().getNotesFlow(sourceId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertNote(note: StudyNote): Long {
        return db.noteDao().insertNote(NoteEntity.fromDomain(note))
    }

    override suspend fun deleteNote(id: Long) {
        db.noteDao().deleteNote(id)
    }

    override fun getQuestionsFlow(sourceId: Long): Flow<List<RecallQuestion>> {
        return db.questionDao().getQuestionsFlow(sourceId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllQuestionsFlow(): Flow<List<RecallQuestion>> {
        return db.questionDao().getAllQuestionsFlow().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertQuestion(question: RecallQuestion): Long {
        return db.questionDao().insertQuestion(QuestionEntity.fromDomain(question))
    }

    override suspend fun updateQuestionRecall(
        id: Long,
        status: RecallStatus,
        lastRecallText: String?,
        lastRecallAssessment: String?,
        nextReviewTime: Long
    ) {
        db.questionDao().updateQuestionRecall(
            id = id,
            status = status.name,
            lastText = lastRecallText,
            lastAssessment = lastRecallAssessment,
            nextReview = nextReviewTime
        )
    }
}
