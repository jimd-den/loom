package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.*

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val status: String,
    val timestamp: Long
) {
    fun toDomain() = Goal(
        id = id,
        title = title,
        status = try { GoalStatus.valueOf(status) } catch (e: Exception) { GoalStatus.INTERVIEWING },
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(goal: Goal) = GoalEntity(
            id = goal.id,
            title = goal.title,
            status = goal.status.name,
            timestamp = goal.timestamp
        )
    }
}

@Entity(tableName = "goal_profiles")
data class GoalProfileEntity(
    @PrimaryKey val id: Long, // Matches goalId
    val intent: String,
    val level: String,
    val outcome: String,
    val timeHorizon: String,
    val constraints: String,
    val depthBreadthSpeed: String
) {
    fun toDomain() = GoalProfile(
        id = id,
        intent = intent,
        level = level,
        outcome = outcome,
        timeHorizon = timeHorizon,
        constraints = constraints,
        depthBreadthSpeed = depthBreadthSpeed
    )

    companion object {
        fun fromDomain(profile: GoalProfile) = GoalProfileEntity(
            id = profile.id,
            intent = profile.intent,
            level = profile.level,
            outcome = profile.outcome,
            timeHorizon = profile.timeHorizon,
            constraints = profile.constraints,
            depthBreadthSpeed = profile.depthBreadthSpeed
        )
    }
}

@Entity(tableName = "idea_cards")
data class IdeaCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val content: String,
    val category: String,
    val isFuzzy: Boolean,
    val timestamp: Long
) {
    fun toDomain() = IdeaCard(
        id = id,
        goalId = goalId,
        content = content,
        category = try { IdeaCategory.valueOf(category) } catch (e: Exception) { IdeaCategory.CONCEPT },
        isFuzzy = isFuzzy,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(card: IdeaCard) = IdeaCardEntity(
            id = card.id,
            goalId = card.goalId,
            content = card.content,
            category = card.category.name,
            isFuzzy = card.isFuzzy,
            timestamp = card.timestamp
        )
    }
}

@Entity(tableName = "sources")
data class SourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val title: String,
    val url: String?,
    val whyItMatters: String,
    val whatItUnlocks: String,
    val difficulty: String,
    val readingEffort: String,
    val nextAction: String,
    val status: String,
    val rawText: String?
) {
    fun toDomain() = Source(
        id = id,
        goalId = goalId,
        title = title,
        url = url,
        whyItMatters = whyItMatters,
        whatItUnlocks = whatItUnlocks,
        difficulty = try { Difficulty.valueOf(difficulty) } catch (e: Exception) { Difficulty.INTERMEDIATE },
        readingEffort = readingEffort,
        nextAction = nextAction,
        status = try { SourceStatus.valueOf(status) } catch (e: Exception) { SourceStatus.SUGGESTED },
        rawText = rawText
    )

    companion object {
        fun fromDomain(source: Source) = SourceEntity(
            id = source.id,
            goalId = source.goalId,
            title = source.title,
            url = source.url,
            whyItMatters = source.whyItMatters,
            whatItUnlocks = source.whatItUnlocks,
            difficulty = source.difficulty.name,
            readingEffort = source.readingEffort,
            nextAction = source.nextAction,
            status = source.status.name,
            rawText = source.rawText
        )
    }
}

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: Long,
    val heading: String,
    val content: String,
    val timestamp: Long
) {
    fun toDomain() = StudyNote(
        id = id,
        sourceId = sourceId,
        heading = heading,
        content = content,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(note: StudyNote) = NoteEntity(
            id = note.id,
            sourceId = note.sourceId,
            heading = note.heading,
            content = note.content,
            timestamp = note.timestamp
        )
    }
}

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: Long,
    val questionText: String,
    val passageId: Long?,
    val recallStatus: String,
    val lastRecallText: String?,
    val lastRecallAssessment: String?,
    val nextReviewTime: Long,
    val isUserGenerated: Boolean
) {
    fun toDomain() = RecallQuestion(
        id = id,
        sourceId = sourceId,
        questionText = questionText,
        passageId = passageId,
        recallStatus = try { RecallStatus.valueOf(recallStatus) } catch (e: Exception) { RecallStatus.UNTESTED },
        lastRecallText = lastRecallText,
        lastRecallAssessment = lastRecallAssessment,
        nextReviewTime = nextReviewTime,
        isUserGenerated = isUserGenerated
    )

    companion object {
        fun fromDomain(question: RecallQuestion) = QuestionEntity(
            id = question.id,
            sourceId = question.sourceId,
            questionText = question.questionText,
            passageId = question.passageId,
            recallStatus = question.recallStatus.name,
            lastRecallText = question.lastRecallText,
            lastRecallAssessment = question.lastRecallAssessment,
            nextReviewTime = question.nextReviewTime,
            isUserGenerated = question.isUserGenerated
        )
    }
}
