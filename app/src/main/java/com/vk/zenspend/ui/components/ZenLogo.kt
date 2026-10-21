package com.vk.zenspend.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern Cool Fintech Expense Logo:
 * Features a dynamic obsidian squircle card, illuminated neon emerald & cyan perimeter glow,
 * bold geometric Indian Rupee '₹' glyph, modern ascending pulse line, and micro keypad matrix.
 */
@Composable
fun ZenLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val cornerRadius = size * 0.28f

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color(0xFF00E676).copy(alpha = 0.35f),
                spotColor = Color(0xFF00E5FF).copy(alpha = 0.45f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0A0F1D),
                        Color(0xFF0D2818),
                        Color(0xFF051B14)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF00E676),
                        Color(0xFF00E5FF),
                        Color(0xFF1DE9B6),
                        Color(0xFFFFD166),
                        Color(0xFF00E676)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.78f)) {
            val w = this.size.width
            val h = this.size.height

            // 1. Sleek Digital Backdrop Glass Panel
            val panelRect = RoundRect(
                left = w * 0.10f,
                top = h * 0.10f,
                right = w * 0.90f,
                bottom = h * 0.90f,
                cornerRadius = CornerRadius(w * 0.20f, h * 0.20f)
            )
            drawPath(
                path = Path().apply { addRoundRect(panelRect) },
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF).copy(alpha = 0.08f),
                        Color(0xFF00E676).copy(alpha = 0.04f),
                        Color(0x00000000)
                    )
                )
            )

            // 2. Rising Financial Growth Wave in background
            val wavePath = Path().apply {
                moveTo(w * 0.15f, h * 0.68f)
                cubicTo(w * 0.35f, h * 0.72f, w * 0.50f, h * 0.48f, w * 0.85f, h * 0.32f)
            }
            drawPath(
                path = wavePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF00E676).copy(alpha = 0.25f),
                        Color(0xFF00E5FF).copy(alpha = 0.75f)
                    )
                ),
                style = Stroke(width = w * 0.035f, cap = StrokeCap.Round)
            )

            // Neon Arrowhead at the end of the growth line
            drawCircle(
                color = Color(0xFF00E5FF),
                radius = w * 0.045f,
                center = Offset(w * 0.85f, h * 0.32f)
            )
            drawCircle(
                color = Color.White,
                radius = w * 0.022f,
                center = Offset(w * 0.85f, h * 0.32f)
            )

            // 3. Bold Geometric Indian Rupee '₹' Glyph
            val rupeeTopY = h * 0.24f
            val rupeeMidY = h * 0.44f
            val rupeeBotY = h * 0.72f
            val strokeW = w * 0.065f

            val rupeeBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFF00E676),
                    Color(0xFF1DE9B6)
                )
            )

            // Top Horizontal Bar
            drawLine(
                brush = rupeeBrush,
                start = Offset(w * 0.26f, rupeeTopY),
                end = Offset(w * 0.62f, rupeeTopY),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )

            // Second Parallel Bar
            val midBarY = rupeeTopY + (rupeeMidY - rupeeTopY) * 0.46f
            drawLine(
                brush = rupeeBrush,
                start = Offset(w * 0.26f, midBarY),
                end = Offset(w * 0.56f, midBarY),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )

            // Rupee Upper Loop & Diagonal Leg
            val rupeeBody = Path().apply {
                moveTo(w * 0.34f, rupeeTopY)
                cubicTo(
                    w * 0.64f, rupeeTopY,
                    w * 0.64f, rupeeMidY,
                    w * 0.34f, rupeeMidY
                )
                lineTo(w * 0.30f, rupeeMidY)
                lineTo(w * 0.60f, rupeeBotY)
            }
            drawPath(
                path = rupeeBody,
                brush = rupeeBrush,
                style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // 4. Modern Calculator Node Matrix (Bottom Right)
            val matrixY = h * 0.76f
            val nodeSpacing = w * 0.11f
            val startX = w * 0.32f

            listOf(
                Color(0xFF00E676),
                Color(0xFF00E5FF),
                Color(0xFFFFD166)
            ).forEachIndexed { index, color ->
                val cx = startX + index * nodeSpacing
                drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = w * 0.045f,
                    center = Offset(cx, matrixY)
                )
                drawCircle(
                    color = color,
                    radius = w * 0.025f,
                    center = Offset(cx, matrixY)
                )
            }
        }
    }
}
