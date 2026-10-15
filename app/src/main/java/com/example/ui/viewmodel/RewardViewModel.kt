package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.QuestEntity
import com.example.data.entity.UserProfileEntity
import com.example.data.entity.WithdrawalRequestEntity
import com.example.data.repository.RewardRepository
import com.example.model.GameType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    DAILY,
    CASHOUT,
    LEADERBOARD
}

class RewardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RewardRepository

    val userProfile: StateFlow<UserProfileEntity?>
    val withdrawalRequests: StateFlow<List<WithdrawalRequestEntity>>
    val dailyQuests: StateFlow<List<QuestEntity>>

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _activeGame = MutableStateFlow<GameType?>(null)
    val activeGame: StateFlow<GameType?> = _activeGame.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    init {
        val db = AppDatabase.getInstance(application)
        repository = RewardRepository(db.rewardDao())

        userProfile = repository.userProfile.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        withdrawalRequests = repository.withdrawalRequests.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        dailyQuests = repository.dailyQuests.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun launchGame(gameType: GameType) {
        viewModelScope.launch {
            val hasTicket = repository.consumeTicket()
            if (!hasTicket) {
                // Refill tickets automatically so user is never blocked from playing!
                repository.refillTickets(3)
            }
            _activeGame.value = gameType
        }
    }

    fun closeGame() {
        _activeGame.value = null
    }

    fun onGameRewardWon(gameType: GameType, points: Int, score: Int = 0) {
        viewModelScope.launch {
            repository.recordGameReward(gameType, points, score)
            _snackbarEvent.emit("+$points Points earned in ${gameType.title}!")
        }
    }

    fun claimDailyStreak() {
        viewModelScope.launch {
            val (success, points) = repository.claimDailyStreakReward()
            if (success) {
                _snackbarEvent.emit("🔥 Day Streak Claimed: +$points points credited!")
            }
        }
    }

    fun claimQuest(quest: QuestEntity) {
        viewModelScope.launch {
            val success = repository.claimQuest(quest)
            if (success) {
                _snackbarEvent.emit("🏆 Quest Complete: +${quest.rewardPoints} points!")
            }
        }
    }

    fun submitWithdrawal(amountDollars: Double, method: String, accountInfo: String) {
        viewModelScope.launch {
            val result = repository.submitWithdrawal(amountDollars, method, accountInfo)
            result.fold(
                onSuccess = { req ->
                    _snackbarEvent.emit("✅ $100.00 withdrawal submitted! Ref: ${req.referenceCode}")
                },
                onFailure = { err ->
                    _snackbarEvent.emit("⚠️ Withdrawal failed: ${err.message}")
                }
            )
        }
    }

    fun addQuickBoost(points: Int = 25000) {
        viewModelScope.launch {
            repository.addDemoBooster(points)
            _snackbarEvent.emit("⚡ +$points Points Boosted (+$25.00 Value)!")
        }
    }

    fun refillTickets() {
        viewModelScope.launch {
            repository.refillTickets(5)
            _snackbarEvent.emit("🎟️ +5 Game Tickets added!")
        }
    }
}
