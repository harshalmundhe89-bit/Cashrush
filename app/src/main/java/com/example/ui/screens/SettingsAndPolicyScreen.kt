package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun SettingsAndPolicyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    var expandedPrivacy by remember { mutableStateOf(false) }
    var expandedTerms by remember { mutableStateOf(false) }
    var expandedPlayListing by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "LEGAL & PLAY STORE READY",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Settings & Policy Center",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Google Play Store Compliance Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    SlateCardSurface,
                                    Color(0xFF0F2027)
                                )
                            )
                        )
                        .border(1.5.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Google Play Policy Compliant",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ready for Google Play Console Submission",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "• Target SDK 36 (Android 14+ Compliant)\n• Zero Dangerous Permissions Requested\n• Free-to-Play Loyalty Model (No Gambling / Staking)\n• Clear $100.00 Redemption Threshold Disclosures\n• Built-in Privacy Policy & Terms of Service",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Mandatory Google LLC Disclaimer Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SlateCardElevated)
                        .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Official Google LLC Disclaimer",
                                style = MaterialTheme.typography.labelMedium,
                                color = AmberGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Google LLC is not a sponsor, nor is it involved in any manner with CashRush, its mini-games, rewards, contests, or cash redemptions. All rewards are sponsored and disbursed independently by CashRush in accordance with its Terms of Service.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Section: Google Play Console Listing Guide (Copy-Paste Toolkit)
            item {
                ExpandableCard(
                    title = "Play Store Listing Kit",
                    subtitle = "Title, descriptions, and category ready for Play Console",
                    icon = Icons.Default.Store,
                    isExpanded = expandedPlayListing,
                    onToggle = { expandedPlayListing = !expandedPlayListing }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CopyableField(
                            label = "App Title (<= 30 chars)",
                            value = "CashRush",
                            onCopy = { copyToClipboard("App Title", "CashRush") }
                        )

                        CopyableField(
                            label = "Short Description (<= 80 chars)",
                            value = "Play fun mini-games, earn points, and cash out rewards at the $100 limit!",
                            onCopy = {
                                copyToClipboard(
                                    "Short Description",
                                    "Play fun mini-games, earn points, and cash out rewards at the $100 limit!"
                                )
                            }
                        )

                        CopyableField(
                            label = "Category & Tags",
                            value = "Category: Games > Casual / Trivia\nTags: Mini-games, Rewards, Puzzle, Casual, Offline",
                            onCopy = { copyToClipboard("Category", "Games > Casual") }
                        )

                        CopyableField(
                            label = "Full Description (Play Console formatted)",
                            value = """Welcome to CashRush – the casual rewards gaming hub where your time and reflexes turn into real rewards!

🎮 5 ADDICTIVE MINI-GAMES:
• Lucky Spin Wheel: Spin daily for up to 1,000 points and multiplier boosts.
• Golden Scratchers: Scratch metallic tiles to reveal matching cash symbols and win jackpots.
• Math Blitz Rush: 30-second speed calculation rush to chain combo streaks.
• Coin Drop Reflex: Fast reflexes! Catch falling gold coins while dodging bombs.
• Memory Cash Vault: Flip and match security cards before time runs out.

💰 THE $100 CASHOUT VAULT:
Every game you play earns reward points (1,000 points = $1.00 USD). Build your balance toward the $100.00 withdrawal threshold. Once reached, redeem your rewards via PayPal, Venmo, Cash App, Direct Bank Wire, or Gift Cards!

🔥 DAILY ENGAGEMENT:
• 7-Day Escalating Streak Bonuses
• Daily Quests and Missions
• Global Leaderboard Competition

100% FREE TO PLAY: No purchases, in-app deposits, or payments of any kind are necessary to participate.

Disclaimer: Google LLC is not a sponsor of, or affiliated in any way with, this application or its promotions.""",
                            onCopy = {
                                copyToClipboard(
                                    "Full Description",
                                    """Welcome to CashRush – the casual rewards gaming hub where your time and reflexes turn into real rewards!

🎮 5 ADDICTIVE MINI-GAMES:
• Lucky Spin Wheel: Spin daily for up to 1,000 points and multiplier boosts.
• Golden Scratchers: Scratch metallic tiles to reveal matching cash symbols and win jackpots.
• Math Blitz Rush: 30-second speed calculation rush to chain combo streaks.
• Coin Drop Reflex: Fast reflexes! Catch falling gold coins while dodging bombs.
• Memory Cash Vault: Flip and match security cards before time runs out.

💰 THE $100 CASHOUT VAULT:
Every game you play earns reward points (1,000 points = $1.00 USD). Build your balance toward the $100.00 withdrawal threshold. Once reached, redeem your rewards via PayPal, Venmo, Cash App, Direct Bank Wire, or Gift Cards!

🔥 DAILY ENGAGEMENT:
• 7-Day Escalating Streak Bonuses
• Daily Quests and Missions
• Global Leaderboard Competition

100% FREE TO PLAY: No purchases, in-app deposits, or payments of any kind are necessary to participate.

Disclaimer: Google LLC is not a sponsor of, or affiliated in any way with, this application or its promotions."""
                                )
                            }
                        )
                    }
                }
            }

            // Section: Official Privacy Policy
            item {
                ExpandableCard(
                    title = "Privacy Policy",
                    subtitle = "Required by Google Play for all user-facing apps",
                    icon = Icons.Default.PrivacyTip,
                    isExpanded = expandedPrivacy,
                    onToggle = { expandedPrivacy = !expandedPrivacy }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Last updated: October 2026\n\n" +
                                    "1. Information We Collect:\n" +
                                    "CashRush respects your privacy. We collect minimal information strictly required to operate the app:\n" +
                                    "• Payout Information: If you request a cashout (such as a PayPal address or Venmo handle), we collect this solely to disburse your funds.\n" +
                                    "• Game Progress & Points: Stored on-device to track points, tickets, quests, and streak records.\n" +
                                    "• Device Information: We do not access contacts, photos, microphone, or camera.\n\n" +
                                    "2. How We Use Information:\n" +
                                    "• To calculate points and award mini-game wins.\n" +
                                    "• To verify and fulfill $100 withdrawal requests.\n" +
                                    "• To prevent automated bots and duplicate account exploitation.\n\n" +
                                    "3. Data Sharing & Third Parties:\n" +
                                    "We do NOT sell, rent, or trade your personal data. Payout information is transmitted securely only to designated payment gateways to complete your requested disbursement.\n\n" +
                                    "4. User Rights:\n" +
                                    "You may reset or delete all local data at any time from your device settings.\n\n" +
                                    "5. Children's Privacy:\n" +
                                    "CashRush is intended for users 18 years of age or older. We do not knowingly collect personal information from children under 13.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = {
                                copyToClipboard(
                                    "Privacy Policy URL/Text",
                                    "https://cashrush-rewards.app/privacy-policy (CashRush Official Privacy Policy)"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateCardElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Privacy Policy Text", color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Section: Official Rewards Terms of Service
            item {
                ExpandableCard(
                    title = "Terms of Service & Rules",
                    subtitle = "Rules of play, $100 threshold, and fraud prevention",
                    icon = Icons.Default.Gavel,
                    isExpanded = expandedTerms,
                    onToggle = { expandedTerms = !expandedTerms }
                ) {
                    Text(
                        text = "1. Free Participation:\n" +
                                "CashRush is a free-to-play loyalty and rewards application. No monetary deposit, fee, or purchase is required to participate in any mini-game.\n\n" +
                                "2. Points and Cashout Threshold:\n" +
                                "Points are virtual loyalty credits earned exclusively through gameplay and daily engagement. 1,000 points correspond to $1.00 USD in redemption value. The minimum redemption threshold is strictly $100.00 (100,000 points). Withdrawals cannot be processed below this limit.\n\n" +
                                "3. Processing & Verification:\n" +
                                "Withdrawal requests undergo an automated verification review (24 to 48 business hours) to protect the integrity of the reward pool.\n\n" +
                                "4. Fair Play & Prohibited Conduct:\n" +
                                "Using auto-clickers, emulators for botting, reverse-engineering, or creating multiple accounts to exploit bonus systems is strictly prohibited and will result in forfeiture of points.\n\n" +
                                "5. Jurisdiction & Taxes:\n" +
                                "Users are responsible for any applicable local tax obligations associated with prize redemptions in their jurisdiction.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            // Technical App Info Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SlateCardSurface)
                        .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Technical Build Info",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        InfoRow(label = "Application ID", value = "com.aistudio.cashrush.rxmpwq")
                        InfoRow(label = "Version", value = "1.0.0 (Build 1)")
                        InfoRow(label = "Target SDK", value = "API 36 (Android 14+)")
                        InfoRow(label = "Min SDK", value = "API 24 (Android 7.0+)")
                        InfoRow(label = "Release Format", value = "AAB (Android App Bundle)")
                        InfoRow(label = "Permissions", value = "VIBRATE (Normal, no runtime prompt)")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun ExpandableCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SlateCardSurface)
            .border(1.dp, if (isExpanded) AmberGold.copy(alpha = 0.5f) else SlateBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = title, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun CopyableField(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCardElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = AmberGold, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = EmeraldLight, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
    }
}
