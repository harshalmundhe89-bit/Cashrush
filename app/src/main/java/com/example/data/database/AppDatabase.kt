package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.RewardDao
import com.example.data.entity.GameHistoryEntity
import com.example.data.entity.QuestEntity
import com.example.data.entity.UserProfileEntity
import com.example.data.entity.WithdrawalRequestEntity

@Database(
    entities = [
        UserProfileEntity::class,
        WithdrawalRequestEntity::class,
        GameHistoryEntity::class,
        QuestEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rewardDao(): RewardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cashrush_rewards.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
