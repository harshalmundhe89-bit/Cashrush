package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.PurpleAccent
import kotlin.random.Random

private data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val speedX: Float,
    val speedY: Float,
    val color: Color,
    val size: Float
)

@Composable
fun CelebrationOverlay(
    isActive: Boolean,
    onFinished: () -> Unit = {}
) {
    if (!isActive) return

    val progress = remember { Animatable(0f) }
    val particles = remember {
        val colors = listOf(EmeraldLight, AmberGold, CyanAccent, PurpleAccent, Color.White)
        List(60) {
            ConfettiParticle(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat() * 0.3f,
                speedX = (Random.nextFloat() - 0.5f) * 400f,
                speedY = Random.nextFloat() * 1200f + 400f,
                color = colors[Random.nextInt(colors.size)],
                size = Random.nextFloat() * 14f + 6f
            )
        }
    }

    LaunchedEffect(isActive) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
        onFinished()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val t = progress.value

        particles.forEach { p ->
            val curX = (p.initialX * w + p.speedX * t).coerceIn(0f, w)
            val curY = p.initialY * h + p.speedY * t
            if (curY < h) {
                drawCircle(
                    color = p.color.copy(alpha = (1f - t * 0.8f).coerceIn(0f, 1f)),
                    radius = p.size * (1f - t * 0.4f),
                    center = Offset(curX, curY)
                )
            }
        }
    }
}
