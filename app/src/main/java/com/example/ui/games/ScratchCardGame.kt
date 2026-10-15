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
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CelebrationOverlay
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberLight
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.random.Random

private data class ScratchItem(
    val id: Int,
    val name: String,
    val points: Int,
    val icon: ImageVector,
    val color: Color,
    var isRevealed: Boolean = false
)

@Composable
fun ScratchCardGame(
    tickets: Int,
    onBack: () -> Unit,
    onRewardEarned: (points: Int, score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    fun triggerVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(30)
        }
    }

    fun generateCards(): List<ScratchItem> {
        val candidates = listOf(
            Triple("Diamond Vault", 1200, Pair(Icons.Default.Diamond, CyanAccent)),
            Triple("Gold Stacks", 600, Pair(Icons.Default.MonetizationOn, AmberGold)),
            Triple("Lucky Star", 350, Pair(Icons.Default.Stars, AmberLight)),
            Triple("Bonus Cash", 200, Pair(Icons.Default.CardGiftcard, EmeraldLight))
        )
        // Ensure at least 2 or 3 match for a rewarding experience
        val winner = candidates.random()
        val items = mutableListOf<ScratchItem>()
        // 3 of winner
        repeat(3) {
            items.add(ScratchItem(items.size, winner.first, winner.second, winner.third.first, winner.third.second))
        }
        // 3 others
        repeat(3) {
            val other = candidates.random()
            items.add(ScratchItem(items.size, other.first, other.second, other.third.first, other.third.second))
        }
        return items.shuffled()
    }

    val cards = remember { mutableStateListOf<ScratchItem>().apply { addAll(generateCards()) } }
    var totalWon by remember { mutableStateOf<Int?>(null) }
    var showCelebration by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    fun checkCompletion() {
        if (cards.all { it.isRevealed } && !isCompleted) {
            isCompleted = true
            // Find most frequent item
            val groups = cards.groupBy { it.name }
            val matched3 = groups.entries.find { it.value.size >= 3 }
            val winPoints = if (matched3 != null) {
                matched3.value.first().points
            } else {
                cards.maxOf { it.points } / 2
            }
            totalWon = winPoints
            showCelebration = true
            triggerVibrate()
            onRewardEarned(winPoints, winPoints)
        }
    }

    fun revealCard(index: Int) {
        if (!cards[index].isRevealed) {
            cards[index] = cards[index].copy(isRevealed = true)
            triggerVibrate()
            checkCompletion()
        }
    }

    fun scratchAll() {
        for (i in cards.indices) {
            cards[i] = cards[i].copy(isRevealed = true)
        }
        triggerVibrate()
        checkCompletion()
    }

    fun resetCard() {
        cards.clear()
        cards.addAll(generateCards())
        totalWon = null
        isCompleted = false
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
                    text = "GOLDEN SCRATCHERS",
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
                        text = "Match 3 to Win!",
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Tap or scratch the tiles to reveal symbols. Match 3 for Jackpot!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Metallic Scratcher Card Board
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .border(
                        2.dp,
                        Brush.linearGradient(listOf(AmberLight, AmberDark)),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIAMOND RUSH $1.20 TICKET",
                            style = MaterialTheme.typography.labelMedium,
                            color = AmberLight,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Top: 1,200 PTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3x2 Scratch Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(cards) { index, item ->
                            ScratchTile(
                                item = item,
                                onClick = { revealCard(index) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { scratchAll() },
                    enabled = !isCompleted,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("scratch_all_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGold,
                        contentColor = SlateDarkBg
                    )
                ) {
                    Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Reveal All", fontWeight = FontWeight.Bold)
                }

                if (isCompleted) {
                    Button(
                        onClick = { resetCard() },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("new_card_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = SlateDarkBg
                        )
                    ) {
                        Text(text = "New Card", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        CelebrationOverlay(isActive = showCelebration) {
            showCelebration = false
        }

        totalWon?.let { won ->
            AlertDialog(
                onDismissRequest = { totalWon = null },
                confirmButton = {
                    Button(
                        onClick = { totalWon = null },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(text = "Collect", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(
                        text = "🎊 MATCHED!",
                        style = MaterialTheme.typography.titleLarge,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "+$won POINTS",
                            style = MaterialTheme.typography.displayMedium,
                            color = EmeraldLight,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Added directly to your cashout vault towards $100!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
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
private fun ScratchTile(
    item: ScratchItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectDragGestures { _, _ ->
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (item.isRevealed) {
            // Revealed content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SlateCardSurface)
                    .border(1.5.dp, item.color.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.name,
                        tint = item.color,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${item.points} pts",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            // Metallic Scratch Foil
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF64748B),
                                Color(0xFF475569),
                                Color(0xFF94A3B8),
                                Color(0xFF475569)
                            )
                        )
                    )
                    .border(1.dp, Color(0xFF94A3B8), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$",
                        style = MaterialTheme.typography.titleLarge,
                        color = AmberLight,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "SCRATCH",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
