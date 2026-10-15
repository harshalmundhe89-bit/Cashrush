package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberLight
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun CashProgressCard(
    points: Int,
    onNavigateCashout: () -> Unit,
    onQuickBoost: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dollars = points / 1000.0
    val threshold = 100.0
    val rawProgress = (dollars / threshold).toFloat().coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 800),
        label = "cashProgress"
    )

    val remainingDollars = (threshold - dollars).coerceAtLeast(0.0)
    val isThresholdMet = dollars >= threshold

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SlateCardSurface,
                        Color(0xFF0F1B2A)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    colors = if (isThresholdMet) {
                        listOf(EmeraldPrimary, AmberGold, EmeraldLight)
                    } else {
                        listOf(EmeraldPrimary.copy(alpha = 0.5f), SlateBorder)
                    }
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(18.dp)
    ) {
        Column {
            // Header Row: Badge & Goal Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isThresholdMet) EmeraldPrimary else AmberGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isThresholdMet) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Vault status",
                            tint = if (isThresholdMet) SlateDarkBg else AmberGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CASHOUT VAULT",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isThresholdMet) EmeraldLight else AmberLight,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Target: $100.00 Limit",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Points Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "%,d PTS".format(Locale.US, points),
                        style = MaterialTheme.typography.labelMedium,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Balance Highlight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Current Value",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "$%.2f".format(Locale.US, dollars),
                        style = MaterialTheme.typography.displayMedium,
                        color = EmeraldLight,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (isThresholdMet) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "THRESHOLD MET!",
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Ready to Redeem",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    } else {
                        Text(
                            text = "$%.2f Left".format(Locale.US, remainingDollars),
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "to unlock $100 payout",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateCardElevated)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(14.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    EmeraldPrimary,
                                    EmeraldLight,
                                    AmberGold
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Milestone Step Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MilestoneItem(label = "$25", reached = dollars >= 25.0)
                MilestoneItem(label = "$50", reached = dollars >= 50.0)
                MilestoneItem(label = "$75", reached = dollars >= 75.0)
                MilestoneItem(label = "$100 (Payout)", reached = dollars >= 100.0)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateCashout,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("cashout_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isThresholdMet) EmeraldPrimary else SlateCardElevated,
                        contentColor = if (isThresholdMet) SlateDarkBg else TextPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isThresholdMet) "CLAIM $100 CASHOUT" else "Cashout Vault",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Quick Play/Test Boost button (allows reviewer/user to simulate or test reaching the threshold easily)
                Button(
                    onClick = onQuickBoost,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("demo_boost_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGold.copy(alpha = 0.15f),
                        contentColor = AmberGold
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+$25 Boost",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MilestoneItem(label: String, reached: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (reached) EmeraldPrimary else SlateBorder)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (reached) EmeraldLight else TextMuted,
            fontSize = 10.sp
        )
    }
}
