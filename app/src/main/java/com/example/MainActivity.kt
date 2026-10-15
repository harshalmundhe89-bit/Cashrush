package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.entity.UserProfileEntity
import com.example.model.GameType
import com.example.ui.games.CoinDropGame
import com.example.ui.games.MathBlitzGame
import com.example.ui.games.MemoryVaultGame
import com.example.ui.games.ScratchCardGame
import com.example.ui.games.WheelSpinGame
import com.example.ui.screens.CashoutScreen
import com.example.ui.screens.DailyRewardsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.SettingsAndPolicyScreen
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.RewardViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: RewardViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeGame by viewModel.activeGame.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val withdrawals by viewModel.withdrawalRequests.collectAsStateWithLifecycle()
    val dailyQuests by viewModel.dailyQuests.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val profile = userProfile ?: UserProfileEntity()

    // Handle back button press when playing games, in settings, or in secondary tabs
    if (activeGame != null) {
        BackHandler {
            viewModel.closeGame()
        }
    } else if (isSettingsOpen) {
        BackHandler {
            viewModel.closeSettings()
        }
    } else if (currentTab != MainTab.HOME) {
        BackHandler {
            viewModel.selectTab(MainTab.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SlateDarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (activeGame == null && !isSettingsOpen) {
                NavigationBar(
                    containerColor = SlateCardSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = currentTab == MainTab.HOME,
                        onClick = { viewModel.selectTab(MainTab.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Games", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SlateDarkBg,
                            selectedTextColor = EmeraldLight,
                            indicatorColor = EmeraldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.DAILY,
                        onClick = { viewModel.selectTab(MainTab.DAILY) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.DAILY) Icons.Filled.LocalFireDepartment else Icons.Outlined.LocalFireDepartment,
                                contentDescription = "Daily Rewards"
                            )
                        },
                        label = { Text("Daily", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SlateDarkBg,
                            selectedTextColor = AmberGold,
                            indicatorColor = AmberGold,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_daily")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.CASHOUT,
                        onClick = { viewModel.selectTab(MainTab.CASHOUT) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.CASHOUT) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                                contentDescription = "$100 Cashout"
                            )
                        },
                        label = { Text("$100 Vault", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SlateDarkBg,
                            selectedTextColor = EmeraldLight,
                            indicatorColor = EmeraldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_cashout")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.LEADERBOARD,
                        onClick = { viewModel.selectTab(MainTab.LEADERBOARD) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.LEADERBOARD) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                                contentDescription = "Leaderboard"
                            )
                        },
                        label = { Text("Ranks", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SlateDarkBg,
                            selectedTextColor = AmberGold,
                            indicatorColor = AmberGold,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_leaderboard")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeGame != null) {
                // Game View
                when (activeGame!!) {
                    GameType.SPIN_WHEEL -> WheelSpinGame(
                        tickets = profile.tickets,
                        onBack = { viewModel.closeGame() },
                        onRewardEarned = { points, score ->
                            viewModel.onGameRewardWon(GameType.SPIN_WHEEL, points, score)
                        }
                    )
                    GameType.SCRATCH_CARD -> ScratchCardGame(
                        tickets = profile.tickets,
                        onBack = { viewModel.closeGame() },
                        onRewardEarned = { points, score ->
                            viewModel.onGameRewardWon(GameType.SCRATCH_CARD, points, score)
                        }
                    )
                    GameType.MATH_BLITZ -> MathBlitzGame(
                        tickets = profile.tickets,
                        onBack = { viewModel.closeGame() },
                        onRewardEarned = { points, score ->
                            viewModel.onGameRewardWon(GameType.MATH_BLITZ, points, score)
                        }
                    )
                    GameType.COIN_DROP -> CoinDropGame(
                        tickets = profile.tickets,
                        onBack = { viewModel.closeGame() },
                        onRewardEarned = { points, score ->
                            viewModel.onGameRewardWon(GameType.COIN_DROP, points, score)
                        }
                    )
                    GameType.MEMORY_VAULT -> MemoryVaultGame(
                        tickets = profile.tickets,
                        onBack = { viewModel.closeGame() },
                        onRewardEarned = { points, score ->
                            viewModel.onGameRewardWon(GameType.MEMORY_VAULT, points, score)
                        }
                    )
                }
            } else if (isSettingsOpen) {
                // Play Store & Legal Policy Screen
                SettingsAndPolicyScreen(
                    onBack = { viewModel.closeSettings() }
                )
            } else {
                // Tab Views
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tabTransition"
                ) { tab ->
                    when (tab) {
                        MainTab.HOME -> HomeScreen(
                            profile = profile,
                            onNavigateGame = { gameType -> viewModel.launchGame(gameType) },
                            onNavigateCashout = { viewModel.selectTab(MainTab.CASHOUT) },
                            onQuickBoost = { viewModel.addQuickBoost(25000) },
                            onOpenSettings = { viewModel.openSettings() }
                        )
                        MainTab.DAILY -> DailyRewardsScreen(
                            streakDays = profile.streakDays,
                            tickets = profile.tickets,
                            quests = dailyQuests,
                            onClaimStreak = { viewModel.claimDailyStreak() },
                            onClaimQuest = { quest -> viewModel.claimQuest(quest) },
                            onRefillTickets = { viewModel.refillTickets() }
                        )
                        MainTab.CASHOUT -> CashoutScreen(
                            points = profile.points,
                            withdrawals = withdrawals,
                            onSubmitWithdrawal = { amount, method, account ->
                                viewModel.submitWithdrawal(amount, method, account)
                            },
                            onQuickBoost = { viewModel.addQuickBoost(25000) },
                            onOpenSettings = { viewModel.openSettings() }
                        )
                        MainTab.LEADERBOARD -> LeaderboardScreen(
                            userPoints = profile.points
                        )
                    }
                }
            }
        }
    }
}
