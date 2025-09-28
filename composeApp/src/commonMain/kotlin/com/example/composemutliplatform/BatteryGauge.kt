package com.example.composemutliplatform

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun BatteryGaugePreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BatteryGauge(level = 90f, true, Modifier,250.dp)
    }
}

@Composable
fun BatteryGauge(
    level: Float,
    isCharging: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {

    val totalSweep = 220f   // arc size (can be 180, 240, 270, even 360)
    val startAngle = (180f - (totalSweep - 180f) / 2f) // center it

    val animatedLevel by animateFloatAsState(
        targetValue = level.coerceIn(0f, 100f),
        animationSpec = tween(durationMillis = 1000)
    )

    // --- Lightning Bolt Animation Setup ---
    val infiniteTransition = rememberInfiniteTransition(label = "BlinkTransition")

    // The alpha state is animated from 0.4 (subtle) to 1.0 (full visibility) and repeats
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCharging) 0.4f else 1f, // Only animate if charging
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        ),
        label = "BlinkAlpha"
    )

    // The final alpha used for the drawing. If not charging, it's 1.0.
    val finalLightningAlpha = if (isCharging) blinkAlpha else 1f

    val colorLow = Color(0xFFFF4500) // Orange Red
    val colorMedium = Color(0xFFFFA500) // Orange
    val colorHigh = Color(0xFF7CFC00) // Lawn Green
    val colorFull = Color(0xFF32CD32) // Lime Green

    val textColor = when {
        animatedLevel <= 20f -> colorLow
        animatedLevel <= 50f -> colorMedium
        animatedLevel <= 80f -> colorHigh
        else -> colorFull
    }

    val lightningColor = when {
        animatedLevel <= 20f -> colorLow
        animatedLevel <= 50f -> colorMedium
        animatedLevel <= 80f -> colorHigh
        else -> colorFull
    }

    Box(
        modifier = modifier.size(size).aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val totalSegments = 2
            val segmentSweepAngle = 180f / totalSegments
            val gaugeStrokeWidth = size.toPx() * 0.1f
            val radius = (size.toPx() - gaugeStrokeWidth) / 2
            val arcRect = Size(2 * radius, 2 * radius)
            val topLeft = Offset(
                (size.toPx() - arcRect.width) / 2,
                (size.toPx() - arcRect.height) / 2
            )

            // Draw the full gray/black background arc
            drawArc(
                color = Color.DarkGray.copy(alpha = 0.5f),
                startAngle = startAngle,
                sweepAngle = totalSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcRect,
                style = Stroke(
                    width = gaugeStrokeWidth,
                    cap = StrokeCap.Butt,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(segmentSweepAngle, segmentSweepAngle * 0.1f),
                        0f
                    )
                )
            )


            // Compute gradient stops based on battery arc
            val colorStops = arrayOf(
                0.0f to colorLow,
                0.3f to colorMedium,
                0.6f to colorHigh,
                1.0f to colorFull
            )

            val gradientSweepBrush = Brush.sweepGradient(
                // Define colors at key percentage points (0% to 100% fill)
                colorStops = arrayOf(
                    0.00f to colorLow,        // Start (0% power)
                    0.30f to colorMedium,     // 30% power
                    0.60f to colorHigh,       // 60% power
                    1.00f to colorFull        // End (100% power)
                ),
                center = center, // Center of the full circle
            )

            // Calculate the filled angle
            val currentSweepAngle = animatedLevel * (totalSweep / 100f)

            drawArc(
                color = lightningColor,
                startAngle = startAngle,
                sweepAngle = currentSweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcRect,
                style = Stroke(
                    width = gaugeStrokeWidth,
                    cap = StrokeCap.Butt,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(segmentSweepAngle, segmentSweepAngle * 0.1f),
                        0f
                    )
                )
            )

            // --- Draw the Lightning Bolt inside the arc ---
            // Adjust lightning bolt position to be slightly above the center of the semicircular area
            // and size it appropriately.
            val innerRadius = radius - gaugeStrokeWidth / 2
            val lightningYOffset = center.y - innerRadius * 0.5f // Slightly below center
            val lightningSize = size.toPx() * 0.15f // Smaller than before to fit

            drawLightningBolt(this, lightningColor, center.x, lightningYOffset, lightningSize, finalLightningAlpha)
        }

        // --- Percentage Text positioned below the lightning bolt and within the arc ---
        // Adjust padding to bring the text further up, inside the arc.
        Text(
            text = "${level.toInt()}%",
            color = textColor,
            style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                     fontSize = 20.sp),
            modifier = Modifier.padding(top = 0.dp) // Adjusted to move it up
        )
    }
}

/**
 * Draws a simplified, stylized lightning bolt icon in the center of the gauge.
 */
private fun drawLightningBolt(drawScope: DrawScope, color: Color, centerX: Float, centerY: Float, sizePx: Float,alpha: Float) {
    drawScope.apply {
        val path = Path().apply {
            // Define the shape of a simple lightning bolt centered at (centerX, centerY)
            moveTo(centerX - sizePx * 0.1f, centerY - sizePx * 0.5f)
            lineTo(centerX - sizePx * 0.3f, centerY + sizePx * 0.1f)
            lineTo(centerX + sizePx * 0.1f, centerY + sizePx * 0.1f)
            lineTo(centerX + sizePx * 0.3f, centerY + sizePx * 0.5f)
            lineTo(centerX + sizePx * 0.5f, centerY - sizePx * 0.1f)
            lineTo(centerX + sizePx * 0.1f, centerY - sizePx * 0.1f)
            close()
        }

        drawPath(
            path = path,
            color = color,
            alpha = alpha
        )
    }
}
