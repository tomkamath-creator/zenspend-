package com.vk.zenspend.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary

/**
 * Zen Expense Calculator Logo
 * Features a modern rounded calculator card, golden Indian Rupee '₹' display,
 * keypad dots, and a gentle water-drop circular halo.
 */
@Composable
fun ZenLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = SageAccent.copy(alpha = 0.25f))
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        SagePastel.copy(alpha = 0.8f),
                        PorcelainBg
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.72f)) {
            val w = this.size.width
            val h = this.size.height

            // 1. Water Ripple Halo
            drawCircle(
                color = SageAccent.copy(alpha = 0.25f),
                radius = w * 0.48f,
                center = Offset(w / 2f, h / 2f),
                style = Stroke(width = w * 0.02f)
            )

            // 2. Calculator Body (Forest Sage Gradient)
            val calcBody = RoundRect(
                left = w * 0.20f,
                top = h * 0.16f,
                right = w * 0.80f,
                bottom = h * 0.88f,
                cornerRadius = CornerRadius(w * 0.10f, h * 0.10f)
            )
            val calcPath = Path().apply { addRoundRect(calcBody) }
            drawPath(
                path = calcPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1B4332), SagePrimary)
                )
            )

            // 3. Digital Screen Bar
            val screenRect = RoundRect(
                left = w * 0.26f,
                top = h * 0.22f,
                right = w * 0.74f,
                bottom = h * 0.40f,
                cornerRadius = CornerRadius(w * 0.05f, h * 0.05f)
            )
            drawPath(
                path = Path().apply { addRoundRect(screenRect) },
                color = Color(0xFFE8F5E9)
            )

            // 4. Golden Rupee '₹' Glyph on Screen
            val rupeeTopY = h * 0.27f
            val rupeeMidY = h * 0.32f
            val rupeeBotY = h * 0.38f
            val strokeW = w * 0.024f
            val gold = Color(0xFFFFB703)

            // Top Bar
            drawLine(
                color = gold,
                start = Offset(w * 0.32f, rupeeTopY),
                end = Offset(w * 0.44f, rupeeTopY),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
            // Second Bar
            drawLine(
                color = gold,
                start = Offset(w * 0.32f, rupeeTopY + (rupeeMidY - rupeeTopY) * 0.5f),
                end = Offset(w * 0.42f, rupeeTopY + (rupeeMidY - rupeeTopY) * 0.5f),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
            // Arc / Stem
            val rupeeArc = Path().apply {
                moveTo(w * 0.36f, rupeeTopY)
                cubicTo(w * 0.44f, rupeeTopY, w * 0.44f, rupeeMidY, w * 0.36f, rupeeMidY)
                lineTo(w * 0.35f, rupeeMidY)
                lineTo(w * 0.43f, rupeeBotY)
            }
            drawPath(
                path = rupeeArc,
                color = gold,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )

            // Digital balance line
            drawLine(
                color = SagePrimary,
                start = Offset(w * 0.48f, h * 0.32f),
                end = Offset(w * 0.68f, h * 0.32f),
                strokeWidth = strokeW * 1.2f,
                cap = StrokeCap.Round
            )

            // 5. Calculator Keypad Grid
            val keyColor = Color(0xFF52B788)
            val accentKey = Color(0xFFFFB703)
            val row1Y = h * 0.49f
            val row2Y = h * 0.62f
            val colXs = listOf(w * 0.33f, w * 0.45f, w * 0.57f, w * 0.69f)

            // Row 1
            drawCircle(keyColor, radius = w * 0.045f, center = Offset(colXs[0], row1Y))
            drawCircle(keyColor, radius = w * 0.045f, center = Offset(colXs[1], row1Y))
            drawCircle(keyColor, radius = w * 0.045f, center = Offset(colXs[2], row1Y))
            drawCircle(accentKey, radius = w * 0.045f, center = Offset(colXs[3], row1Y))

            // Row 2
            drawCircle(keyColor, radius = w * 0.045f, center = Offset(colXs[0], row2Y))
            drawCircle(keyColor, radius = w * 0.045f, center = Offset(colXs[1], row2Y))
            drawCircle(keyColor, radius = w * 0.045f, center = Offset(colXs[2], row2Y))
            drawCircle(accentKey, radius = w * 0.045f, center = Offset(colXs[3], row2Y))

            // Equal Bar at bottom
            val equalBar = RoundRect(
                left = w * 0.28f,
                top = h * 0.72f,
                right = w * 0.72f,
                bottom = h * 0.81f,
                cornerRadius = CornerRadius(w * 0.04f, h * 0.04f)
            )
            drawPath(
                path = Path().apply { addRoundRect(equalBar) },
                color = Color(0xFF74C69D)
            )
            drawLine(
                color = Color.White,
                start = Offset(w * 0.44f, h * 0.755f),
                end = Offset(w * 0.56f, h * 0.755f),
                strokeWidth = strokeW * 0.9f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = Offset(w * 0.44f, h * 0.785f),
                end = Offset(w * 0.56f, h * 0.785f),
                strokeWidth = strokeW * 0.9f,
                cap = StrokeCap.Round
            )
        }
    }
}
