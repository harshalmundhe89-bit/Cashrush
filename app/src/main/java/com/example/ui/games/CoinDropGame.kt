package com.example.ui.games

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CelebrationOverlay
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberLight
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.random.Random

private data class FallingCoin(
    val id: Long,
    var xPercent: Float,
    var yPercent: Float,
    val speed: Float,
    val isBomb: Boolean,
    val radiusPx: Float,
    var isCollected: Boolean = false
)

@Composable
fun CoinDropGame(
    tickets: Int,
    onBack: () -> Unit,
    onRewardEarned: (points: Int, score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var isGameRunning by remember { mutableStateOf(false) }
    var timeLeftSeconds by remember { mutableIntStateOf(25) }
    var collectedCoinsCount by remember { mutableIntStateOf(0) }
    var currentScore by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val coins = remember { mutableStateListOf<FallingCoin>() }

    fun triggerVibrate(isGood: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ms = if (isGood) 25L else 150L
            vibrator?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(if (isGood) 25 else 150)
        }
    }

    // Main game loop
    LaunchedEffect(isGameRunning) {
        if (isGameRunning) {
            timeLeftSeconds = 25
            currentScore = 0
            collectedCoinsCount = 0
            coins.clear()

            // Spawn and update loop (approx 60fps)
            var lastSpawnTime = System.currentTimeMillis()
            var lastTimerTick = System.currentTimeMillis()

            while (timeLeftSeconds > 0 && isGameRunning) {
                val now = System.currentTimeMillis()
                // Timer tick
                if (now - lastTimerTick >= 1000) {
                    timeLeftSeconds--
                    lastTimerTick = now
                }

                // Spawn items
                if (now - lastSpawnTime >= 350) {
                    lastSpawnTime = now
                    val isBomb = Random.nextFloat() < 0.22f
                    coins.add(
                        FallingCoin(
                            id = Random.nextLong(),
                            xPercent = Random.nextFloat() * 0.84f + 0.08f,
                            yPercent = -0.05f,
                            speed = Random.nextFloat() * 0.008f + 0.012f,
                            isBomb = isBomb,
                            radiusPx = if (isBomb) 42f else 36f
                        )
                    )
                }

                // Move items
                val iterator = coins.listIterator()
                while (iterator.hasNext()) {
                    val coin = iterator.next()
                    coin.yPercent += coin.speed
                    if (coin.yPercent > 1.05f || coin.isCollected) {
                        iterator.remove()
                    }
                }

                delay(16)
            }

            if (isGameRunning) {
                isGameRunning = false
                isGameOver = true
                val finalPoints = (currentScore * 1.8).toInt().coerceAtLeast(150)
                if (currentScore > 0) {
                    showConfetti = true
                    onRewardEarned(finalPoints, collectedCoinsCount)
                }
            }
        }
    }

    fun handleTap(tapX: Float, tapY: Float, canvasWidth: Float, canvasHeight: Float) {
        if (!isGameRunning) return
        var hitAny = false
        for (coin in coins) {
            if (coin.isCollected) continue
            val coinPixelX = coin.xPercent * canvasWidth
            val coinPixelY = coin.yPercent * canvasHeight
            val dist = hypot(tapX - coinPixelX, tapY - coinPixelY)
            if (dist < coin.radiusPx * 2.2f) {
                coin.isCollected = true
                hitAny = true
                if (coin.isBomb) {
                    triggerVibrate(false)
                    currentScore = (currentScore - 40).coerceAtLeast(0)
                } else {
                    triggerVibrate(true)
                    currentScore += 30
                    collectedCoinsCount++
                }
                break
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
                    text = "COIN DROP REFLEX",
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (timeLeftSeconds <= 5) CrimsonAlert else CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${timeLeftSeconds}s",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Score Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Collected", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(text = "$collectedCoinsCount Coins", style = MaterialTheme.typography.titleMedium, color = AmberGold, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Points Score", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(text = "$currentScore", style = MaterialTheme.typography.titleMedium, color = EmeraldLight, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Drop Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SlateCardSurface)
            ) {
                if (!isGameRunning && !isGameOver) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🪙 Catch the Falling Cash!",
                            style = MaterialTheme.typography.titleLarge,
                            color = AmberGold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tap gold coins and cash bags as they fall.\nDodge the red bombs to protect your score!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { isGameRunning = true },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(52.dp)
                                .testTag("start_coin_drop_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = "START CATCHING (25s)", color = SlateDarkBg, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                } else {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    handleTap(offset.x, offset.y, size.width.toFloat(), size.height.toFloat())
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height

                        coins.forEach { coin ->
                            val cx = coin.xPercent * w
                            val cy = coin.yPercent * h
                            if (coin.isBomb) {
                                // Draw red hazard bomb
                                drawCircle(
                                    color = CrimsonAlert,
                                    radius = coin.radiusPx,
                                    center = Offset(cx, cy)
                                )
                                drawCircle(
                                    color = Color.Black,
                                    radius = coin.radiusPx * 0.4f,
                                    center = Offset(cx, cy)
                                )
                            } else {
                                // Draw shiny gold coin
                                drawCircle(
                                    color = AmberGold,
                                    radius = coin.radiusPx,
                                    center = Offset(cx, cy)
                                )
                                drawCircle(
                                    color = AmberLight,
                                    radius = coin.radiusPx * 0.7f,
                                    center = Offset(cx, cy)
                                )
                            }
                        }
                    }
                }
            }
        }

        CelebrationOverlay(isActive = showConfetti) {
            showConfetti = false
        }

        if (isGameOver) {
            val finalPoints = (currentScore * 1.8).toInt().coerceAtLeast(150)
            AlertDialog(
                onDismissRequest = { isGameOver = false },
                confirmButton = {
                    Button(
                        onClick = { isGameOver = false },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(text = "Collect Points", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(
                        text = "💰 ROUND COMPLETE!",
                        style = MaterialTheme.typography.titleLarge,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Coins Caught: $collectedCoinsCount",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+$finalPoints POINTS",
                            style = MaterialTheme.typography.displayMedium,
                            color = EmeraldLight,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Added to your $100 cashout balance!",
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
