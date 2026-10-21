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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class Particle(
    val initialX: Float,
    val initialY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotationSpeed: Float,
    val color: Color,
    val size: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiCelebration(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 70
) {
    if (!isActive) return

    val progress = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2800, easing = LinearEasing)
        )
    }

    val particles = remember {
        val colors = listOf(
            Color(0xFF7C3AED), // Purple
            Color(0xFFEC4899), // Pink
            Color(0xFFF59E0B), // Gold
            Color(0xFF10B981), // Emerald
            Color(0xFF3B82F6), // Blue
            Color(0xFFFBBF24)  // Amber
        )
        List(particleCount) {
            Particle(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat() * 0.3f, // start near top
                velocityX = (Random.nextFloat() - 0.5f) * 220f,
                velocityY = Random.nextFloat() * 650f + 250f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                color = colors.random(),
                size = Random.nextFloat() * 12f + 8f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    val currentProgress = progress.value
    val alpha = (1f - (currentProgress - 0.7f).coerceAtLeast(0f) / 0.3f).coerceIn(0f, 1f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            val x = (p.initialX * width + p.velocityX * currentProgress).mod(width)
            val y = p.initialY * height + p.velocityY * currentProgress
            val rotation = p.rotationSpeed * currentProgress

            if (y < height) {
                rotate(degrees = rotation, pivot = Offset(x, y)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color.copy(alpha = alpha),
                            radius = p.size / 2f,
                            center = Offset(x, y)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(x - p.size / 2f, y - p.size / 3f),
                            size = Size(p.size, p.size * 0.65f)
                        )
                    }
                }
            }
        }
    }
}
