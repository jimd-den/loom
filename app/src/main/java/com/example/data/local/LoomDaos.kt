package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): SettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingEntity)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY timestamp DESC")
    fun getAllGoalsFlow(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
    suspend fun getGoalById(id: Long): GoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Query("UPDATE goals SET status = :status WHERE id = :id")
    suspend fun updateGoalStatus(id: Long, status: String)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoal(id: Long)
}

@Dao
interface GoalProfileDao {
    @Query("SELECT * FROM goal_profiles WHERE id = :goalId LIMIT 1")
    suspend fun getGoalProfile(goalId: Long): GoalProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalProfile(profile: GoalProfileEntity)
}

@Dao
interface IdeaCardDao {
    @Query("SELECT * FROM idea_cards WHERE goalId = :goalId ORDER BY timestamp DESC")
    fun getIdeaCardsFlow(goalId: Long): Flow<List<IdeaCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdeaCard(card: IdeaCardEntity): Long

    @Query("DELETE FROM idea_cards WHERE id = :id")
    suspend fun deleteIdeaCard(id: Long)

    @Query("UPDATE idea_cards SET isFuzzy = :isFuzzy WHERE id = :id")
    suspend fun updateIdeaCardFuzzy(id: Long, isFuzzy: Boolean)
}

@Dao
interface SourceDao {
    @Query("SELECT * FROM sources WHERE goalId = :goalId")
    fun getSourcesFlow(goalId: Long): Flow<List<SourceEntity>>

    @Query("SELECT * FROM sources WHERE id = :id LIMIT 1")
    suspend fun getSourceById(id: Long): SourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: SourceEntity): Long

    @Query("UPDATE sources SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE sources SET rawText = :text WHERE id = :id")
    suspend fun updateRawText(id: Long, text: String)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE sourceId = :sourceId ORDER BY timestamp DESC")
    fun getNotesFlow(sourceId: Long): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE sourceId = :sourceId ORDER BY isUserGenerated DESC")
    fun getQuestionsFlow(sourceId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions ORDER BY nextReviewTime ASC")
    fun getAllQuestionsFlow(): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Query("UPDATE questions SET recallStatus = :status, lastRecallText = :lastText, lastRecallAssessment = :lastAssessment, nextReviewTime = :nextReview WHERE id = :id")
    suspend fun updateQuestionRecall(
        id: Long,
        status: String,
        lastText: String?,
        lastAssessment: String?,
        nextReview: Long
    )
}
