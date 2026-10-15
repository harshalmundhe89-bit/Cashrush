package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.GameHistoryEntity
import com.example.data.entity.QuestEntity
import com.example.data.entity.UserProfileEntity
import com.example.data.entity.WithdrawalRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM withdrawal_requests ORDER BY requestTimestamp DESC")
    fun getWithdrawalRequestsFlow(): Flow<List<WithdrawalRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawalRequest(request: WithdrawalRequestEntity): Long

    @Query("SELECT * FROM game_history ORDER BY timestamp DESC LIMIT 20")
    fun getGameHistoryFlow(): Flow<List<GameHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameHistory(history: GameHistoryEntity): Long

    @Query("SELECT * FROM daily_quests ORDER BY id ASC")
    fun getQuestsFlow(): Flow<List<QuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuests(quests: List<QuestEntity>)

    @Update
    suspend fun updateQuest(quest: QuestEntity)

    @Query("UPDATE user_profile SET points = points + :points, lifetimePoints = lifetimePoints + :points, gamesPlayedCount = gamesPlayedCount + 1 WHERE id = 1")
    suspend fun incrementPointsAndGames(points: Int)

    @Query("UPDATE user_profile SET tickets = tickets - 1 WHERE id = 1 AND tickets > 0")
    suspend fun consumeTicket()

    @Query("UPDATE user_profile SET tickets = tickets + :amount WHERE id = 1")
    suspend fun addTickets(amount: Int)
}
