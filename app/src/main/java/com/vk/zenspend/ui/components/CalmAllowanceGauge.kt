package com.vk.zenspend.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.ui.theme.ApricotCaution
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.TerracottaAlert
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary

/**
 * Liquid Gauge / Morphing Ring:
 * Displays monthly budget progress with a smooth, organically pulsating circle.
 * Automatically adapts color palette:
 * - Sage Green for healthy pace (< 75%)
 * - Apricot for cautionary thresholds (75% - 100%)
 * - Terracotta for exceeded limits (> 100%)
 */
@Composable
fun CalmAllowanceGauge(
    dailyCalmAllowance: Double,
    spentSoFar: Double,
    monthlyBudget: Double,
    remainingDays: Int,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp
) {
    val progressFraction = if (monthlyBudget > 0) {
        (spentSoFar / monthlyBudget).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    val animatedSweep by animateFloatAsState(
        targetValue = progressFraction * 360f,
        animationSpec = ZenMotion.DefaultSpring,
        label = "gaugeSweep"
    )

    // Gentle organic breathing / pulsating aura
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val activeColor = when {
        spentSoFar > monthlyBudget -> TerracottaAlert
        progressFraction > 0.75f -> ApricotCaution
        else -> SageAccent
    }

    val activeGlow = when {
        spentSoFar > monthlyBudget -> Color(0xFFFEECEB)
        progressFraction > 0.75f -> Color(0xFFFFF4E5)
        else -> SagePastel
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing background & gauge ring
        Canvas(modifier = Modifier.size(size * pulseScale)) {
            val strokeW = 14.dp.toPx()
            val canvasSize = this.size.width
            val radius = (canvasSize - strokeW) / 2f
            val topLeft = Offset(strokeW / 2f, strokeW / 2f)
            val arcSize = Size(radius * 2f, radius * 2f)

            // 1. Ambient soft background ring
            drawArc(
                color = activeGlow,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )

            // 2. Liquid progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        activeColor.copy(alpha = 0.8f),
                        activeColor,
                        activeColor
                    )
                ),
                startAngle = -90f,
                sweepAngle = animatedSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
        }

        // Central Calm Metric
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Safe daily pace",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = ZenTextMuted,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            RollingTickerText(
                amount = dailyCalmAllowance,
                textStyle = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "for next $remainingDays days",
                fontSize = 12.sp,
                color = ZenTextSecondary
            )
        }
    }
}
