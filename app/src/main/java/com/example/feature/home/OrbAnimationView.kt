package com.example.feature.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.core.emotion.MyraEmotion
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun OrbAnimationView(
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    emotion: MyraEmotion = MyraEmotion.CARING,
    amplitude: Float = 0f,
    isListening: Boolean = false,
    isSpeaking: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val rippleWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple"
    )

    val primaryColor = emotion.orbColor
    val secondaryColor = when (emotion) {
        MyraEmotion.LOVING, MyraEmotion.ROMANTIC -> Color(0xFFFF2A85)
        MyraEmotion.CUTE -> Color(0xFFFF80BF)
        MyraEmotion.HAPPY -> Color(0xFFF59E0B)
        MyraEmotion.CARING -> Color(0xFFFB7185)
        MyraEmotion.SLEEPY -> Color(0xFF38BDF8)
        MyraEmotion.MOTIVATIONAL -> Color(0xFFEF4444)
        else -> Color(0xFF7928CA)
    }

    Box(
        modifier = modifier
            .size(size)
            .testTag("myra_orb_view"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = (this.size.minDimension / 2f) * 0.55f

            // Dynamic amplitude expansion
            val activeBoost = if (isListening || isSpeaking) (amplitude * 35f) else 0f
            val currentRadius = (baseRadius * pulseScale) + activeBoost

            // 1. Outer Holographic Glow Rings
            val ringCount = 3
            for (i in 1..ringCount) {
                val ringRadius = currentRadius + (i * 18f * rippleWave)
                val alpha = (0.35f - (i * 0.10f) - (rippleWave * 0.15f)).coerceIn(0.05f, 0.4f)
                drawCircle(
                    color = primaryColor.copy(alpha = alpha),
                    radius = ringRadius,
                    center = center,
                    style = Stroke(width = 2.5f)
                )
            }

            // 2. Rotating Energy Orbits
            val orbitCount = 6
            for (j in 0 until orbitCount) {
                val angleRad = Math.toRadians((rotationAngle + (j * 60.0)).toDouble())
                val orbitDist = currentRadius * 0.85f
                val orbitPos = Offset(
                    x = (center.x + orbitDist * cos(angleRad)).toFloat(),
                    y = (center.y + orbitDist * sin(angleRad)).toFloat()
                )
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.55f),
                    radius = 4f + (amplitude * 5f),
                    center = orbitPos
                )
            }

            // 3. Main Orb Core with Liquid Gradient
            val gradientBrush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.95f),
                    primaryColor.copy(alpha = 0.85f),
                    secondaryColor.copy(alpha = 0.70f),
                    primaryColor.copy(alpha = 0.15f)
                ),
                center = Offset(center.x - 12f, center.y - 12f),
                radius = currentRadius
            )

            drawCircle(
                brush = gradientBrush,
                radius = currentRadius,
                center = center
            )

            // 4. Subtle Inner Highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.45f),
                radius = currentRadius * 0.35f,
                center = Offset(center.x - 15f, center.y - 15f)
            )
        }
    }
}
