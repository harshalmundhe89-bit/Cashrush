package com.example.ui.games

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class MathQuestion(
    val prompt: String,
    val correctAnswer: Int,
    val options: List<Int>
)

@Composable
fun MathBlitzGame(
    tickets: Int,
    onBack: () -> Unit,
    onRewardEarned: (points: Int, score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var timeLeftSeconds by remember { mutableIntStateOf(30) }
    var isGameRunning by remember { mutableStateOf(false) }
    var currentScore by remember { mutableIntStateOf(0) }
    var streakCombo by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun triggerVibrate(isGood: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val duration = if (isGood) 30L else 120L
            vibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(if (isGood) 30 else 120)
        }
    }

    fun generateQuestion(): MathQuestion {
        val op = Random.nextInt(3) // 0: +, 1: -, 2: *
        val a: Int
        val b: Int
        val ans: Int
        val text: String
        when (op) {
            0 -> {
                a = Random.nextInt(8, 45)
                b = Random.nextInt(8, 45)
                ans = a + b
                text = "$a + $b"
            }
            1 -> {
                a = Random.nextInt(20, 60)
                b = Random.nextInt(5, a - 2)
                ans = a - b
                text = "$a - $b"
            }
            else -> {
                a = Random.nextInt(3, 12)
                b = Random.nextInt(3, 12)
                ans = a * b
                text = "$a × $b"
            }
        }
        val optionsSet = mutableSetOf(ans)
        while (optionsSet.size < 4) {
            val delta = Random.nextInt(-8, 9)
            if (delta != 0 && ans + delta > 0) {
                optionsSet.add(ans + delta)
            }
        }
        return MathQuestion(text, ans, optionsSet.toList().shuffled())
    }

    var question by remember { mutableStateOf(generateQuestion()) }

    // Timer loop
    LaunchedEffect(isGameRunning) {
        if (isGameRunning) {
            timeLeftSeconds = 30
            currentScore = 0
            streakCombo = 0
            question = generateQuestion()
            while (timeLeftSeconds > 0 && isGameRunning) {
                delay(1000)
                timeLeftSeconds--
            }
            if (isGameRunning) {
                isGameRunning = false
                isGameOver = true
                val finalPoints = (currentScore * 1.5).toInt().coerceAtLeast(100)
                if (currentScore > 0) {
                    showConfetti = true
                    onRewardEarned(finalPoints, currentScore)
                }
            }
        }
    }

    fun answer(selected: Int) {
        if (!isGameRunning) return
        if (selected == question.correctAnswer) {
            triggerVibrate(true)
            streakCombo++
            val multiplier = when {
                streakCombo >= 6 -> 3.0
                streakCombo >= 3 -> 2.0
                else -> 1.0
            }
            currentScore += (50 * multiplier).toInt()
        } else {
            triggerVibrate(false)
            streakCombo = 0
            currentScore = (currentScore - 20).coerceAtLeast(0)
        }
        question = generateQuestion()
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
            // Header
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
                    text = "MATH BLITZ RUSH",
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
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = AmberLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${streakCombo}x STREAK",
                            style = MaterialTheme.typography.labelMedium,
                            color = AmberLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Score and Timer Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Score", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        text = "$currentScore",
                        style = MaterialTheme.typography.headlineMedium,
                        color = EmeraldLight,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Time Remaining", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (timeLeftSeconds <= 5) CrimsonAlert else CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${timeLeftSeconds}s",
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (timeLeftSeconds <= 5) CrimsonAlert else TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SlateCardElevated)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(timeLeftSeconds / 30f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (timeLeftSeconds <= 5) CrimsonAlert else EmeraldPrimary)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (!isGameRunning && !isGameOver) {
                // Start screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SlateCardSurface)
                        .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🧠 Brain Reflex Challenge",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Solve as many math equations as you can in 30 seconds!\n\nStreak combos multiply your points. Up to 1,200 points per round!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { isGameRunning = true },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(52.dp)
                                .testTag("start_math_blitz_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = "START RUSH (30s)", color = SlateDarkBg, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            } else {
                // Question Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SlateCardSurface)
                        .border(2.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(vertical = 36.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${question.prompt} = ?",
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2x2 Options Grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OptionButton(
                            value = question.options[0],
                            modifier = Modifier.weight(1f),
                            onClick = { answer(question.options[0]) }
                        )
                        OptionButton(
                            value = question.options[1],
                            modifier = Modifier.weight(1f),
                            onClick = { answer(question.options[1]) }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OptionButton(
                            value = question.options[2],
                            modifier = Modifier.weight(1f),
                            onClick = { answer(question.options[2]) }
                        )
                        OptionButton(
                            value = question.options[3],
                            modifier = Modifier.weight(1f),
                            onClick = { answer(question.options[3]) }
                        )
                    }
                }
            }
        }

        CelebrationOverlay(isActive = showConfetti) {
            showConfetti = false
        }

        if (isGameOver) {
            val finalPoints = (currentScore * 1.5).toInt().coerceAtLeast(100)
            AlertDialog(
                onDismissRequest = { isGameOver = false },
                confirmButton = {
                    Button(
                        onClick = { isGameOver = false },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(text = "Claim Points", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(
                        text = "⚡ TIME'S UP!",
                        style = MaterialTheme.typography.titleLarge,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Score: $currentScore",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "+$finalPoints POINTS",
                            style = MaterialTheme.typography.displayMedium,
                            color = EmeraldLight,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Credited to your $100 Cashout Vault balance!",
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
private fun OptionButton(
    value: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SlateCardElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}
