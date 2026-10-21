package com.vk.zenspend.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.clip
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.ZenPillShape
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.ui.theme.ApricotCaution
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.TerracottaAlert
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary

/**
 * Liquid Gauge with Organic Water Drop Ripple Effect:
 * Visualizes safe daily allowance in ₹ with pulsating ripples
 * like serene water droplets on a peaceful pond.
 */
@Composable
fun CalmAllowanceGauge(
    dailyCalmAllowance: Double,
    spentSoFar: Double,
    monthlyBudget: Double,
    remainingDays: Int,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    onClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Bouncy spring reaction on tap
    val bounceScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = ZenMotion.DefaultSpring,
        label = "gaugeBounce"
    )

    val progressFraction = if (monthlyBudget > 0) {
        (spentSoFar / monthlyBudget).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    val animatedSweep by animateFloatAsState(
        targetValue = progressFraction * 360f,
        animationSpec = ZenMotion.DefaultSpring,
        label = "gaugeSweep"
    )

    // Water Drop Ripple 1 (Expanding concentric ring)
    val infiniteTransition = rememberInfiniteTransition(label = "waterRipple")
    val ripple1Radius by infiniteTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple1Radius"
    )
    val ripple1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple1Alpha"
    )

    // Water Drop Ripple 2 (Staggered offset wave)
    val ripple2Radius by infiniteTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, delayMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple2Radius"
    )
    val ripple2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, delayMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple2Alpha"
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
        modifier = modifier
            .size(size)
            .scale(bounceScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size.width
            val center = Offset(canvasSize / 2f, canvasSize / 2f)
            val baseRadius = canvasSize * 0.40f

            // 1. Water Drop Ripple Wave 1
            drawCircle(
                color = activeColor.copy(alpha = ripple1Alpha),
                radius = baseRadius * ripple1Radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // 2. Water Drop Ripple Wave 2
            drawCircle(
                color = activeColor.copy(alpha = ripple2Alpha),
                radius = baseRadius * ripple2Radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            val strokeW = 14.dp.toPx()
            val radius = (canvasSize - strokeW - 16.dp.toPx()) / 2f
            val topLeft = Offset((canvasSize - radius * 2f) / 2f, (canvasSize - radius * 2f) / 2f)
            val arcSize = Size(radius * 2f, radius * 2f)

            // 3. Ambient soft track ring
            drawArc(
                color = activeGlow,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )

            // 4. Liquid progress arc with gradient
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        activeColor.copy(alpha = 0.75f),
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

        // Center daily safe allowance in Rupee
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (monthlyBudget <= 0.0) {
                Text(
                    text = "Monthly Budget",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ZenTextMuted,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "₹0.00",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .clip(ZenPillShape)
                        .background(SagePastel)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Tap to set budget",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SagePrimary
                    )
                }
            } else {
                Text(
                    text = "Daily Budget",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ZenTextMuted,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                RollingTickerText(
                    amount = dailyCalmAllowance,
                    currencySymbol = "₹",
                    textStyle = TextStyle(
                        fontSize = 25.sp,
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
}
