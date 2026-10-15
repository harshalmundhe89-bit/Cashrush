package com.example.ui.games

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameType
import com.example.ui.components.CelebrationOverlay
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberLight
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class WheelSlice(val label: String, val points: Int, val color: Color)

private val SLICES = listOf(
    WheelSlice("100 PTS", 100, Color(0xFF1E293B)),
    WheelSlice("250 PTS", 250, EmeraldDark),
    WheelSlice("50 PTS", 50, Color(0xFF334155)),
    WheelSlice("500 PTS", 500, Color(0xFFB45309)),
    WheelSlice("JACKPOT\n1000", 1000, AmberGold),
    WheelSlice("150 PTS", 150, Color(0xFF1E293B)),
    WheelSlice("300 PTS", 300, EmeraldPrimary),
    WheelSlice("750 PTS", 750, PurpleAccent)
)

@Composable
fun WheelSpinGame(
    tickets: Int,
    onBack: () -> Unit,
    onRewardEarned: (points: Int, score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }
    val scope = rememberCoroutineScope()

    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var wonReward by remember { mutableStateOf<WheelSlice?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    fun triggerVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(40)
        }
    }

    fun startSpin() {
        if (isSpinning) return
        isSpinning = true
        scope.launch {
            // Select winning slice
            val targetSliceIndex = Random.nextInt(SLICES.size)
            val sliceAngle = 360f / SLICES.size
            // Pointer is at the top (270 degrees in canvas coords, or 0 if needle is top)
            // Extra 5 full rotations (1800 deg)
            val extraRounds = 5 * 360f
            // Target angle to land with pointer on targetSliceIndex
            // Needle points at top (angle 270)
            val targetAngle = extraRounds + (SLICES.size - targetSliceIndex) * sliceAngle - (sliceAngle / 2f)

            rotation.animateTo(
                targetValue = targetAngle,
                animationSpec = tween(
                    durationMillis = 4200,
                    easing = CubicBezierEasing(0.12f, 0.8f, 0.2f, 1.0f)
                )
            )

            triggerVibrate()
            val slice = SLICES[targetSliceIndex]
            wonReward = slice
            showConfetti = true
            isSpinning = false
            onRewardEarned(slice.points, slice.points)
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
                    text = "LUCKY SPIN & WIN",
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
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$tickets Spins",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Spin daily to earn points towards your $100 cashout!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Wheel Container with Needle
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                // Wheel Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation.value)
                ) {
                    val diameter = size.minDimension
                    val radius = diameter / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val sweepAngle = 360f / SLICES.size

                    // Outer glowing border
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(AmberGold, Color(0xFF78350F)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Draw Slices
                    SLICES.forEachIndexed { index, slice ->
                        val startAngle = index * sweepAngle
                        drawArc(
                            color = slice.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            size = Size(diameter - 24.dp.toPx(), diameter - 24.dp.toPx()),
                            topLeft = Offset(center.x - radius + 12.dp.toPx(), center.y - radius + 12.dp.toPx())
                        )
                    }

                    // Slice lines
                    SLICES.forEachIndexed { index, _ ->
                        val angle = Math.toRadians((index * sweepAngle).toDouble())
                        val endX = center.x + (radius - 12.dp.toPx()) * cos(angle).toFloat()
                        val endY = center.y + (radius - 12.dp.toPx()) * sin(angle).toFloat()
                        drawLine(
                            color = SlateBorder,
                            start = center,
                            end = Offset(endX, endY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Center Hub
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White, AmberGold),
                            center = center,
                            radius = 28.dp.toPx()
                        ),
                        radius = 28.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = SlateDarkBg,
                        radius = 16.dp.toPx(),
                        center = center
                    )
                }

                // Needle at the top
                Canvas(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.TopCenter)
                ) {
                    val path = Path().apply {
                        moveTo(size.width / 2f, size.height)
                        lineTo(0f, 0f)
                        lineTo(size.width, 0f)
                        close()
                    }
                    drawPath(path, color = AmberLight)
                    drawPath(path, color = Color.White, style = Stroke(width = 2.dp.toPx()))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Spin Button
            Button(
                onClick = { startSpin() },
                enabled = !isSpinning,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp)
                    .testTag("spin_wheel_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberGold,
                    contentColor = SlateDarkBg,
                    disabledContainerColor = SlateCardElevated,
                    disabledContentColor = TextSecondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSpinning) "SPINNING..." else "SPIN THE WHEEL",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Rewards showcase
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateCardSurface)
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔥 Max Prize", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(text = "1,000 PTS", style = MaterialTheme.typography.titleSmall, color = AmberGold, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "💰 Cash Value", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(text = "Up to $1.00", style = MaterialTheme.typography.titleSmall, color = EmeraldLight, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "✨ Odds", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(text = "100% Win", style = MaterialTheme.typography.titleSmall, color = CyanAccent, fontWeight = FontWeight.Bold)
                }
            }
        }

        CelebrationOverlay(isActive = showConfetti) {
            showConfetti = false
        }

        // Win Dialog
        wonReward?.let { reward ->
            AlertDialog(
                onDismissRequest = { wonReward = null },
                confirmButton = {
                    Button(
                        onClick = { wonReward = null },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(text = "Collect Points", fontWeight = FontWeight.Bold, color = SlateDarkBg)
                    }
                },
                title = {
                    Text(
                        text = "🎉 YOU WON!",
                        style = MaterialTheme.typography.titleLarge,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "+${reward.points} POINTS",
                            style = MaterialTheme.typography.displayMedium,
                            color = EmeraldLight,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Worth $%.2f towards your $100 cashout goal!".format(reward.points / 1000.0),
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
