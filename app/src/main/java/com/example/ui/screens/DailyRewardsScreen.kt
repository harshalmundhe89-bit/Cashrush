package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.QuestEntity
import com.example.ui.components.CelebrationOverlay
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberLight
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private val STREAK_VALUES = listOf(
    Pair("Day 1", 250),
    Pair("Day 2", 400),
    Pair("Day 3", 600),
    Pair("Day 4", 850),
    Pair("Day 5", 1200),
    Pair("Day 6", 1800),
    Pair("Day 7", 3500)
)

@Composable
fun DailyRewardsScreen(
    streakDays: Int,
    tickets: Int,
    quests: List<QuestEntity>,
    onClaimStreak: () -> Unit,
    onClaimQuest: (QuestEntity) -> Unit,
    onRefillTickets: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfetti by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DAILY ENGAGEMENT",
                            style = MaterialTheme.typography.labelMedium,
                            color = AmberGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Streaks & Quests",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SlateCardElevated)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "$tickets Tickets", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                        }
                    }
                }
            }

            // 7-Day Streak Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    SlateCardSurface,
                                    Color(0xFF131D2E)
                                )
                            )
                        )
                        .border(1.5.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AmberGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = AmberGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "$streakDays DAY STREAK",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Check in daily for mega point boosts",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    onClaimStreak()
                                    showConfetti = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                modifier = Modifier.testTag("claim_daily_button")
                            ) {
                                Text(text = "Check In", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Streak horizontal timeline
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            STREAK_VALUES.forEachIndexed { index, (dayLabel, points) ->
                                val dayNum = index + 1
                                val isChecked = dayNum <= streakDays
                                val isToday = dayNum == (streakDays % 7) + 1
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isChecked -> EmeraldPrimary
                                                    isToday -> AmberGold.copy(alpha = 0.3f)
                                                    else -> SlateCardElevated
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                if (isToday) AmberGold else SlateBorder,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isChecked) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SlateDarkBg, modifier = Modifier.size(18.dp))
                                        } else {
                                            Text(
                                                text = "+$points",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isToday) AmberLight else TextMuted,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = dayLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isChecked || isToday) TextPrimary else TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Daily Quests Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Quests",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = onRefillTickets,
                        colors = ButtonDefaults.buttonColors(containerColor = SlateCardElevated),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "+5 Free Tickets", color = CyanAccent, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // Quests List
            items(quests) { quest ->
                QuestItemCard(
                    quest = quest,
                    onClaim = {
                        onClaimQuest(quest)
                        showConfetti = true
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        CelebrationOverlay(isActive = showConfetti) {
            showConfetti = false
        }
    }
}

@Composable
private fun QuestItemCard(
    quest: QuestEntity,
    onClaim: () -> Unit
) {
    val canClaim = quest.currentProgress >= quest.targetProgress && !quest.isClaimed
    val progressRatio = (quest.currentProgress.toFloat() / quest.targetProgress.coerceAtLeast(1)).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SlateCardSurface)
            .border(1.dp, if (canClaim) EmeraldPrimary else SlateBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = quest.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(SlateCardElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressRatio)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (canClaim) EmeraldPrimary else AmberGold)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${quest.currentProgress}/${quest.targetProgress}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Action
            if (quest.isClaimed) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateCardElevated)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "Claimed", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            } else {
                Button(
                    onClick = onClaim,
                    enabled = canClaim,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = SlateDarkBg,
                        disabledContainerColor = SlateCardElevated,
                        disabledContentColor = TextMuted
                    ),
                    modifier = Modifier.testTag("claim_quest_${quest.id}")
                ) {
                    Text(
                        text = "+${quest.rewardPoints} pts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
