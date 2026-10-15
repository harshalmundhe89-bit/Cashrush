package com.example.ui.games

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CelebrationOverlay
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private data class VaultCard(
    val id: Int,
    val pairKey: String,
    val icon: ImageVector,
    val color: Color,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun MemoryVaultGame(
    tickets: Int,
    onBack: () -> Unit,
    onRewardEarned: (points: Int, score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    fun triggerVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(35)
        }
    }

    fun makeDeck(): List<VaultCard> {
        val pairs = listOf(
            Triple("DIAMOND", Icons.Default.Diamond, CyanAccent),
            Triple("GOLD", Icons.Default.MonetizationOn, AmberGold),
            Triple("CASH", Icons.Default.AttachMoney, EmeraldLight),
            Triple("CROWN", Icons.Default.WorkspacePremium, PurpleAccent)
        )
        val list = mutableListOf<VaultCard>()
        var idCounter = 0
        pairs.forEach { (key, icon, color) ->
            list.add(VaultCard(idCounter++, key, icon, color))
            list.add(VaultCard(idCounter++, key, icon, color))
        }
        return list.shuffled()
    }

    val cards = remember { mutableStateListOf<VaultCard>().apply { addAll(makeDeck()) } }
    var selectedIndex1 by remember { mutableStateOf<Int?>(null) }
    var selectedIndex2 by remember { mutableStateOf<Int?>(null) }
    var isBusy by remember { mutableStateOf(false) }
    var turnsCount by remember { mutableIntStateOf(0) }
    var isGameFinished by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun onCardClicked(index: Int) {
        if (isBusy || cards[index].isFlipped || cards[index].isMatched) return

        triggerVibrate()
        cards[index] = cards[index].copy(isFlipped = true)

        if (selectedIndex1 == null) {
            selectedIndex1 = index
        } else if (selectedIndex2 == null) {
            selectedIndex2 = index
            turnsCount++
            isBusy = true
        }
    }

    // Matching logic
    LaunchedEffect(selectedIndex1, selectedIndex2) {
        val i1 = selectedIndex1
        val i2 = selectedIndex2
        if (i1 != null && i2 != null) {
            delay(650)
            if (cards[i1].pairKey == cards[i2].pairKey) {
                cards[i1] = cards[i1].copy(isMatched = true)
                cards[i2] = cards[i2].copy(isMatched = true)
                triggerVibrate()
            } else {
                cards[i1] = cards[i1].copy(isFlipped = false)
                cards[i2] = cards[i2].copy(isFlipped = false)
            }
            selectedIndex1 = null
            selectedIndex2 = null
            isBusy = false

            // Check win
            if (cards.all { it.isMatched } && !isGameFinished) {
                isGameFinished = true
                showConfetti = true
                val points = when {
                    turnsCount <= 6 -> 800
                    turnsCount <= 10 -> 600
                    else -> 450
                }
                onRewardEarned(points, turnsCount)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "MEMORY CASH VAULT",
                    style = MaterialTheme.typography.titleLarge,
                    color = AmberGold,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCardElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$turnsCount Moves",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Flip matching cash vault symbols to crack the vault!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4x2 Grid of cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(cards) { index, card ->
                    MemoryCardView(card = card, onClick = { onCardClicked(index) })
                }
            }
        }

        CelebrationOverlay(isActive = showConfetti) {
            showConfetti = false
        }

        if (isGameFinished) {
            val wonPoints = when {
                turnsCount <= 6 -> 800
                turnsCount <= 10 -> 600
                else -> 450
            }
            AlertDialog(
                onDismissRequest = { isGameFinished = false },
                confirmButton = {
                    Button(
                        onClick = { isGameFinished = false },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(text = "Collect Points", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(
                        text = "🔓 VAULT UNLOCKED!",
                        style = MaterialTheme.typography.titleLarge,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Cracked in $turnsCount turns!",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+$wonPoints POINTS",
                            style = MaterialTheme.typography.displayMedium,
                            color = EmeraldLight,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Added directly to your cashout vault towards $100!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                },
                containerColor = SlateCardSurface,
                shape = RoundedCornerShape(18.dp)
            )
        }
    }
}

@Composable
private fun MemoryCardView(
    card: VaultCard,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(
                if (card.isFlipped || card.isMatched) SlateCardSurface else SlateCardElevated
            )
            .border(
                1.5.dp,
                if (card.isMatched) EmeraldPrimary else if (card.isFlipped) card.color else SlateBorder,
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (card.isFlipped || card.isMatched) {
            Icon(
                imageVector = card.icon,
                contentDescription = card.pairKey,
                tint = card.color,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Hidden",
                tint = SlateBorder,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
