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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LeaderboardItem
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
import java.util.Locale

private val LEADERBOARD_DATA = listOf(
    LeaderboardItem(1, "Marcus 'Apex' Vance", "👑", 285400, 200.0),
    LeaderboardItem(2, "Elena Rostova", "⚡", 198250, 100.0),
    LeaderboardItem(3, "Kai Nakamura", "🔥", 164800, 100.0),
    LeaderboardItem(4, "Sarah Jenkins", "🎯", 121500, 100.0),
    LeaderboardItem(5, "David Chen", "💎", 98400, 0.0),
    LeaderboardItem(6, "Amara Okafor", "🚀", 89100, 0.0),
    LeaderboardItem(7, "Lucas Meyer", "🎲", 74200, 0.0),
    LeaderboardItem(8, "Sophia Martinez", "🌟", 62000, 0.0)
)

@Composable
fun LeaderboardScreen(
    userPoints: Int,
    modifier: Modifier = Modifier
) {
    val userDollars = userPoints / 1000.0
    val userRank = when {
        userPoints >= 200000 -> 2
        userPoints >= 150000 -> 3
        userPoints >= 100000 -> 5
        userPoints >= 50000 -> 9
        else -> 14
    }

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
                Column {
                    Text(
                        text = "GLOBAL RACERS",
                        style = MaterialTheme.typography.labelMedium,
                        color = AmberGold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Cashout Leaderboard",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Top 3 Podium
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    SlateCardSurface,
                                    Color(0xFF172033)
                                )
                            )
                        )
                        .border(1.5.dp, AmberGold.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Top $100 Cashout Earners", style = MaterialTheme.typography.titleSmall, color = AmberGold, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 3 Podium Columns: 2nd, 1st, 3rd
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            PodiumColumn(item = LEADERBOARD_DATA[1], rank = 2, heightDp = 100, badgeColor = Color(0xFFC0C0C0))
                            PodiumColumn(item = LEADERBOARD_DATA[0], rank = 1, heightDp = 130, badgeColor = AmberGold)
                            PodiumColumn(item = LEADERBOARD_DATA[2], rank = 3, heightDp = 85, badgeColor = Color(0xFFCD7F32))
                        }
                    }
                }
            }

            // User's Rank Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.12f))
                        .border(1.5.dp, EmeraldPrimary, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "#$userRank", color = SlateDarkBg, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "You (Current Player)", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text(text = "$%.2f / $100.00 Goal".format(Locale.US, userDollars), style = MaterialTheme.typography.bodySmall, color = EmeraldLight)
                            }
                        }

                        Text(
                            text = "%,d pts".format(Locale.US, userPoints),
                            style = MaterialTheme.typography.titleMedium,
                            color = AmberGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Rankings",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            items(LEADERBOARD_DATA) { item ->
                LeaderboardRow(item = item)
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun PodiumColumn(
    item: LeaderboardItem,
    rank: Int,
    heightDp: Int,
    badgeColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(text = item.avatarEmoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.username.split(" ").first(),
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = "$%.0f Cash".format(item.cashedOutDollars),
            style = MaterialTheme.typography.labelSmall,
            color = EmeraldLight,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .width(76.dp)
                .height(heightDp.dp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(SlateCardElevated)
                .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "#$rank", color = badgeColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(text = "%,dk".format(item.totalPoints / 1000), color = TextMuted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun LeaderboardRow(item: LeaderboardItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCardSurface)
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "#${item.rank}",
                style = MaterialTheme.typography.labelMedium,
                color = if (item.rank <= 3) AmberGold else TextMuted,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(32.dp)
            )
            Text(text = item.avatarEmoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = item.username, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                if (item.cashedOutDollars > 0) {
                    Text(text = "Cashed Out $%.0f".format(item.cashedOutDollars), style = MaterialTheme.typography.labelSmall, color = EmeraldLight)
                } else {
                    Text(text = "Racing to $100", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
        }

        Text(
            text = "%,d pts".format(Locale.US, item.totalPoints),
            style = MaterialTheme.typography.titleSmall,
            color = AmberGold,
            fontWeight = FontWeight.Bold
        )
    }
}
