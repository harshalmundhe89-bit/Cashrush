package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val points: Int = 18500, // Starts with $18.50 progress towards $100
    val streakDays: Int = 1,
    val lastCheckinTimestamp: Long = 0L,
    val tickets: Int = 8,
    val lifetimePoints: Int = 18500,
    val gamesPlayedCount: Int = 0,
    val welcomeBonusClaimed: Boolean = true
)

@Entity(tableName = "withdrawal_requests")
data class WithdrawalRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val amountDollars: Double,
    val pointsDeducted: Int,
    val paymentMethod: String,
    val destinationAccount: String,
    val requestTimestamp: Long,
    val status: String, // "PENDING", "VERIFYING", "COMPLETED"
    val referenceCode: String
)

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val gameType: String,
    val pointsEarned: Int,
    val score: Int,
    val timestamp: Long
)

@Entity(tableName = "daily_quests")
data class QuestEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val rewardPoints: Int,
    val currentProgress: Int,
    val targetProgress: Int,
    val isClaimed: Boolean
)
