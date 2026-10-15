package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.WithdrawalRequestEntity
import com.example.model.PayoutMethod
import com.example.ui.components.CelebrationOverlay
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CashoutScreen(
    points: Int,
    withdrawals: List<WithdrawalRequestEntity>,
    onSubmitWithdrawal: (amountDollars: Double, method: String, accountInfo: String) -> Unit,
    onQuickBoost: () -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dollars = points / 1000.0
    val threshold = 100.0
    val isThresholdMet = dollars >= threshold
    val remainingDollars = (threshold - dollars).coerceAtLeast(0.0)

    var showWithdrawModal by remember { mutableStateOf(false) }
    var selectedMethod by remember { mutableStateOf(PayoutMethod.PAYPAL) }
    var accountInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    var selectedReceipt by remember { mutableStateOf<WithdrawalRequestEntity?>(null) }
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
                            text = "CASHOUT VAULT",
                            style = MaterialTheme.typography.labelMedium,
                            color = AmberGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Redeem Real Cash",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isThresholdMet) EmeraldPrimary else SlateCardElevated)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isThresholdMet) "UNLOCKED" else "LOCKED",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isThresholdMet) SlateDarkBg else AmberGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // High-Impact Vault Status Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    SlateCardSurface,
                                    Color(0xFF0D1E2F)
                                )
                            )
                        )
                        .border(
                            2.dp,
                            if (isThresholdMet) Brush.horizontalGradient(listOf(EmeraldPrimary, AmberGold)) else Brush.linearGradient(listOf(SlateBorder, SlateBorder)),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "AVAILABLE BALANCE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "$%.2f".format(Locale.US, dollars),
                                    style = MaterialTheme.typography.displayMedium,
                                    color = if (isThresholdMet) EmeraldLight else TextPrimary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "%,d Points (1,000 pts = $1.00)".format(Locale.US, points),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AmberGold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(if (isThresholdMet) EmeraldPrimary else SlateCardElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isThresholdMet) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isThresholdMet) SlateDarkBg else TextSecondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress Tracker
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Withdrawal Threshold Limit: $100.00",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "%.1f%%".format(Locale.US, (dollars / threshold * 100).coerceAtMost(100.0)),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isThresholdMet) EmeraldLight else AmberLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SlateCardElevated)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth((dollars / threshold).toFloat().coerceIn(0f, 1f))
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(EmeraldPrimary, AmberGold)
                                            )
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (!isThresholdMet) {
                                Text(
                                    text = "⚡ Earn $%.2f more to unlock instant cash withdrawal".format(Locale.US, remainingDollars),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            } else {
                                Text(
                                    text = "✅ Threshold reached! You can withdraw $100.00 right now.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showWithdrawModal = true },
                                enabled = isThresholdMet,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("request_withdrawal_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldPrimary,
                                    contentColor = SlateDarkBg,
                                    disabledContainerColor = SlateCardElevated,
                                    disabledContentColor = TextMuted
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payment,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isThresholdMet) "WITHDRAW $100 CASH" else "$100 LIMIT LOCKED",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            // Reviewer & Test Quick Boost
                            Button(
                                onClick = onQuickBoost,
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("cashout_boost_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberGold.copy(alpha = 0.15f),
                                    contentColor = AmberGold
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "+$25 Boost", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Payout Methods Supported
            item {
                Text(
                    text = "Supported Payout Options",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PayoutMethodCard(name = "PayPal Direct", icon = Icons.Default.Payment, speed = "Instant to 24h", tag = "POPULAR")
                    PayoutMethodCard(name = "Venmo", icon = Icons.Default.Payment, speed = "Instant to 24h", tag = "FAST")
                    PayoutMethodCard(name = "Cash App", icon = Icons.Default.Payment, speed = "Instant to 24h", tag = "MOBILE")
                    PayoutMethodCard(name = "Direct Bank Wire (ACH)", icon = Icons.Default.AccountBalance, speed = "1-2 Business Days", tag = "DIRECT")
                    PayoutMethodCard(name = "Amazon Gift Card", icon = Icons.Default.CardGiftcard, speed = "Instant Delivery", tag = "ZERO FEE")
                    PayoutMethodCard(name = "USDC Crypto (Base / Polygon)", icon = Icons.Default.CurrencyBitcoin, speed = "Instant Blockchain", tag = "WEB3")
                }
            }

            // Trust & Security Notice
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardElevated)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Guaranteed Payout Security",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "All redemptions above the $100 limit are verified and disbursed with 256-bit encryption.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Legal & Play Store Policy Link
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardSurface)
                        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                        .clickable { onOpenSettings() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Play Store Compliance & Legal Terms", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            Text(text = "Privacy Policy, Rules of Play & Disclaimers", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }
                    Text(text = "View", color = AmberGold, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            // Past Redemptions History
            item {
                Text(
                    text = "Redemption History",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (withdrawals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateCardSurface)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No withdrawals yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = "Reach $100.00 (100,000 pts) to submit your first redemption!",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(withdrawals) { withdrawal ->
                    WithdrawalHistoryItem(
                        item = withdrawal,
                        onClick = { selectedReceipt = withdrawal }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        CelebrationOverlay(isActive = showConfetti) {
            showConfetti = false
        }

        // Withdrawal Request Modal Dialog
        if (showWithdrawModal) {
            AlertDialog(
                onDismissRequest = { showWithdrawModal = false },
                containerColor = SlateCardSurface,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text(
                        text = "Claim $100.00 Cashout",
                        style = MaterialTheme.typography.titleLarge,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Select your preferred payout method and enter your receiving details:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Method selection buttons
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            PayoutMethod.entries.forEach { method ->
                                val isSelected = method == selectedMethod
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else SlateCardElevated)
                                        .border(
                                            1.dp,
                                            if (isSelected) EmeraldPrimary else SlateBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedMethod = method }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Payment,
                                        contentDescription = null,
                                        tint = if (isSelected) EmeraldLight else TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = method.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = selectedMethod.fieldLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = accountInput,
                            onValueChange = {
                                accountInput = it
                                inputError = null
                            },
                            placeholder = { Text(selectedMethod.placeholder, color = TextMuted) },
                            singleLine = true,
                            isError = inputError != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = SlateBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("account_input_field")
                        )

                        inputError?.let { err ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = err, color = Color(0xFFEF4444), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (accountInput.trim().length < 3) {
                                inputError = "Please enter valid payout recipient details"
                            } else {
                                onSubmitWithdrawal(100.0, selectedMethod.displayName, accountInput.trim())
                                showWithdrawModal = false
                                showConfetti = true
                                accountInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("confirm_withdraw_button")
                    ) {
                        Text(text = "Submit ($100.00)", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showWithdrawModal = false },
                        colors = ButtonDefaults.buttonColors(containerColor = SlateCardElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Cancel", color = TextPrimary)
                    }
                }
            )
        }

        // Receipt Details Dialog
        selectedReceipt?.let { r ->
            val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.US)
            AlertDialog(
                onDismissRequest = { selectedReceipt = null },
                containerColor = SlateCardSurface,
                shape = RoundedCornerShape(18.dp),
                title = {
                    Text(
                        text = "🧾 Withdrawal Receipt",
                        style = MaterialTheme.typography.titleLarge,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReceiptRow(label = "Amount", value = "$%.2f USD".format(Locale.US, r.amountDollars), isHighlight = true)
                        ReceiptRow(label = "Reference ID", value = r.referenceCode)
                        ReceiptRow(label = "Payout Method", value = r.paymentMethod)
                        ReceiptRow(label = "Destination", value = r.destinationAccount)
                        ReceiptRow(label = "Date Submitted", value = dateFormat.format(Date(r.requestTimestamp)))
                        ReceiptRow(label = "Status", value = r.status)

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SlateCardElevated)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Estimated delivery: Within 24-48 business hours to your selected account.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedReceipt = null },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(text = "Close", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
private fun PayoutMethodCard(
    name: String,
    icon: ImageVector,
    speed: String,
    tag: String
) {
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
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SlateCardElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = name, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text(text = speed, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(EmeraldPrimary.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = tag, style = MaterialTheme.typography.labelSmall, color = EmeraldLight, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
    }
}

@Composable
private fun WithdrawalHistoryItem(
    item: WithdrawalRequestEntity,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SlateCardSurface)
            .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "$%.2f USD".format(Locale.US, item.amountDollars), style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(text = "${item.paymentMethod} • ${dateFormat.format(Date(item.requestTimestamp))}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AmberGold.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = item.status, style = MaterialTheme.typography.labelSmall, color = AmberGold, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = if (isHighlight) EmeraldLight else TextPrimary,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}
