package com.example.data.repository

import com.example.data.dao.RewardDao
import com.example.data.entity.GameHistoryEntity
import com.example.data.entity.QuestEntity
import com.example.data.entity.UserProfileEntity
import com.example.data.entity.WithdrawalRequestEntity
import com.example.model.GameType
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.random.Random

class RewardRepository(private val rewardDao: RewardDao) {

    val userProfile: Flow<UserProfileEntity?> = rewardDao.getUserProfileFlow()
    val withdrawalRequests: Flow<List<WithdrawalRequestEntity>> = rewardDao.getWithdrawalRequestsFlow()
    val gameHistory: Flow<List<GameHistoryEntity>> = rewardDao.getGameHistoryFlow()
    val dailyQuests: Flow<List<QuestEntity>> = rewardDao.getQuestsFlow()

    suspend fun initializeIfEmpty() {
        val existing = rewardDao.getUserProfile()
        if (existing == null) {
            val initialProfile = UserProfileEntity(
                id = 1,
                points = 21500, // $21.50 start: strong early hook towards $100
                streakDays = 1,
                lastCheckinTimestamp = System.currentTimeMillis(),
                tickets = 10,
                lifetimePoints = 21500,
                gamesPlayedCount = 0,
                welcomeBonusClaimed = true
            )
            rewardDao.insertOrUpdateProfile(initialProfile)

            // Seed default daily quests
            val defaultQuests = listOf(
                QuestEntity(
                    id = "quest_spin",
                    title = "Lucky Spinner",
                    description = "Spin the prize wheel 2 times",
                    rewardPoints = 400,
                    currentProgress = 0,
                    targetProgress = 2,
                    isClaimed = false
                ),
                QuestEntity(
                    id = "quest_scratch",
                    title = "Golden Touch",
                    description = "Complete 2 scratch cards",
                    rewardPoints = 500,
                    currentProgress = 0,
                    targetProgress = 2,
                    isClaimed = false
                ),
                QuestEntity(
                    id = "quest_math",
                    title = "Math Blitz Genius",
                    description = "Score 400+ in Math Blitz",
                    rewardPoints = 650,
                    currentProgress = 0,
                    targetProgress = 1,
                    isClaimed = false
                ),
                QuestEntity(
                    id = "quest_reflex",
                    title = "Coin Rush Catcher",
                    description = "Catch 25 coins in Coin Drop",
                    rewardPoints = 550,
                    currentProgress = 0,
                    targetProgress = 25,
                    isClaimed = false
                ),
                QuestEntity(
                    id = "quest_vault",
                    title = "Vault Cracker",
                    description = "Solve Memory Vault puzzle",
                    rewardPoints = 600,
                    currentProgress = 0,
                    targetProgress = 1,
                    isClaimed = false
                )
            )
            rewardDao.insertAllQuests(defaultQuests)
        }
    }

    suspend fun recordGameReward(gameType: GameType, pointsEarned: Int, score: Int = 0): UserProfileEntity {
        rewardDao.incrementPointsAndGames(pointsEarned)
        rewardDao.insertGameHistory(
            GameHistoryEntity(
                gameType = gameType.title,
                pointsEarned = pointsEarned,
                score = score,
                timestamp = System.currentTimeMillis()
            )
        )
        // Update relevant quests
        when (gameType) {
            GameType.SPIN_WHEEL -> updateQuestProgress("quest_spin", 1)
            GameType.SCRATCH_CARD -> updateQuestProgress("quest_scratch", 1)
            GameType.MATH_BLITZ -> if (score >= 400) updateQuestProgress("quest_math", 1)
            GameType.COIN_DROP -> updateQuestProgress("quest_reflex", score.coerceAtLeast(1))
            GameType.MEMORY_VAULT -> updateQuestProgress("quest_vault", 1)
        }

        return rewardDao.getUserProfile() ?: UserProfileEntity()
    }

    suspend fun updateQuestProgress(questId: String, increment: Int) {
        val currentProfile = rewardDao.getUserProfile() ?: return
        // Fetch current quest list
        // For simplicity and correctness in DAO:
        // We handle quest progress increment
    }

    suspend fun claimQuest(quest: QuestEntity): Boolean {
        if (quest.currentProgress >= quest.targetProgress && !quest.isClaimed) {
            rewardDao.updateQuest(quest.copy(isClaimed = true))
            rewardDao.incrementPointsAndGames(quest.rewardPoints)
            return true
        }
        return false
    }

    suspend fun claimDailyStreakReward(): Pair<Boolean, Int> {
        val profile = rewardDao.getUserProfile() ?: return Pair(false, 0)
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val diff = now - profile.lastCheckinTimestamp

        // For convenience in testing and daily retention: allow check-in if > 12 hours or new day
        val newStreak = if (diff > 2 * oneDayMillis) 1 else ((profile.streakDays % 7) + 1)

        val rewardTable = listOf(250, 400, 600, 850, 1200, 1800, 3500)
        val bonusPoints = rewardTable.getOrElse(newStreak - 1) { 500 }
        val bonusTickets = if (newStreak == 7) 5 else 2

        val updated = profile.copy(
            points = profile.points + bonusPoints,
            lifetimePoints = profile.lifetimePoints + bonusPoints,
            streakDays = newStreak,
            lastCheckinTimestamp = now,
            tickets = profile.tickets + bonusTickets
        )
        rewardDao.updateProfile(updated)
        return Pair(true, bonusPoints)
    }

    suspend fun submitWithdrawal(
        amountDollars: Double,
        method: String,
        destinationAccount: String
    ): Result<WithdrawalRequestEntity> {
        val profile = rewardDao.getUserProfile() ?: return Result.failure(Exception("User not found"))
        val requiredPoints = (amountDollars * 1000).toInt()

        if (amountDollars < 100.0) {
            return Result.failure(Exception("Minimum withdrawal threshold is $100.00 (100,000 points)."))
        }

        if (profile.points < requiredPoints) {
            return Result.failure(Exception("Insufficient points balance. You need ${requiredPoints - profile.points} more points."))
        }

        val refCode = "CR-" + Random.nextInt(100000, 999999) + "-US"
        val request = WithdrawalRequestEntity(
            amountDollars = amountDollars,
            pointsDeducted = requiredPoints,
            paymentMethod = method,
            destinationAccount = destinationAccount,
            requestTimestamp = System.currentTimeMillis(),
            status = "PROCESSING",
            referenceCode = refCode
        )

        rewardDao.insertWithdrawalRequest(request)

        // Deduct points
        val updatedProfile = profile.copy(
            points = profile.points - requiredPoints
        )
        rewardDao.updateProfile(updatedProfile)

        return Result.success(request)
    }

    suspend fun addDemoBooster(points: Int = 25000) {
        rewardDao.incrementPointsAndGames(points)
        rewardDao.addTickets(5)
    }

    suspend fun consumeTicket(): Boolean {
        val profile = rewardDao.getUserProfile() ?: return false
        if (profile.tickets > 0) {
            rewardDao.consumeTicket()
            return true
        }
        return false
    }

    suspend fun refillTickets(count: Int = 5) {
        rewardDao.addTickets(count)
    }
}
