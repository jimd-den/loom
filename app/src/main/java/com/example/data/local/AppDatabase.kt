package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SettingEntity::class,
        GoalEntity::class,
        GoalProfileEntity::class,
        IdeaCardEntity::class,
        SourceEntity::class,
        NoteEntity::class,
        QuestionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun settingsDao(): SettingsDao
    abstract fun goalDao(): GoalDao
    abstract fun goalProfileDao(): GoalProfileDao
    abstract fun ideaCardDao(): IdeaCardDao
    abstract fun sourceDao(): SourceDao
    abstract fun noteDao(): NoteDao
    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "loom_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
