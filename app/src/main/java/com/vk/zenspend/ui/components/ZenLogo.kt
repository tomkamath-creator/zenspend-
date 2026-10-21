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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary

/**
 * Zen Logo • Crafted by VK
 * Renders a soothing, balanced Zen cairn (stacked stones)
 * crowned with a golden prosperity bud and subtle VK monogram.
 */
@Composable
fun ZenLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = SageAccent.copy(alpha = 0.2f))
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        SagePastel.copy(alpha = 0.7f),
                        PorcelainBg
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.75f)) {
            val w = this.size.width
            val h = this.size.height

            // 1. Golden Accent Halo
            drawCircle(
                color = Color(0xFFFFB703).copy(alpha = 0.35f),
                radius = w * 0.46f,
                center = Offset(w / 2f, h / 2f),
                style = Stroke(width = w * 0.02f)
            )

            // 2. Base Large Zen Stone (Forest Emerald)
            val baseStone = RoundRect(
                left = w * 0.18f,
                top = h * 0.64f,
                right = w * 0.82f,
                bottom = h * 0.80f,
                cornerRadius = CornerRadius(w * 0.08f, h * 0.08f)
            )
            val basePath = Path().apply { addRoundRect(baseStone) }
            drawPath(
                path = basePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF1B4332), SagePrimary)
                )
            )

            // 3. Middle-Low Stone (Deep Sage)
            val midLowStone = RoundRect(
                left = w * 0.26f,
                top = h * 0.49f,
                right = w * 0.74f,
                bottom = h * 0.63f,
                cornerRadius = CornerRadius(w * 0.07f, h * 0.07f)
            )
            val midLowPath = Path().apply { addRoundRect(midLowStone) }
            drawPath(
                path = midLowPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(SagePrimary, Color(0xFF40916C))
                )
            )

            // 4. Middle-Top Stone (Fresh Sage)
            val midTopStone = RoundRect(
                left = w * 0.33f,
                top = h * 0.36f,
                right = w * 0.67f,
                bottom = h * 0.48f,
                cornerRadius = CornerRadius(w * 0.06f, h * 0.06f)
            )
            val midTopPath = Path().apply { addRoundRect(midTopStone) }
            drawPath(
                path = midTopPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF40916C), SageAccent)
                )
            )

            // 5. Crown Stone (Soft Mint)
            val capStone = RoundRect(
                left = w * 0.40f,
                top = h * 0.26f,
                right = w * 0.60f,
                bottom = h * 0.35f,
                cornerRadius = CornerRadius(w * 0.05f, h * 0.05f)
            )
            val capPath = Path().apply { addRoundRect(capStone) }
            drawPath(
                path = capPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF52B788), Color(0xFF74C69D))
                )
            )

            // 6. Golden Prosperity Sprout / Bud
            val budPath = Path().apply {
                moveTo(w * 0.50f, h * 0.12f)
                cubicTo(w * 0.54f, h * 0.16f, w * 0.57f, h * 0.22f, w * 0.50f, h * 0.25f)
                cubicTo(w * 0.43f, h * 0.22f, w * 0.46f, h * 0.16f, w * 0.50f, h * 0.12f)
                close()
            }
            drawPath(
                path = budPath,
                color = Color(0xFFFFB703)
            )

            // 7. Minimalist VK Monogram in the Base Stone
            val strokeWidth = w * 0.024f
            // 'V'
            drawLine(
                color = Color.White.copy(alpha = 0.95f),
                start = Offset(w * 0.42f, h * 0.69f),
                end = Offset(w * 0.46f, h * 0.76f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = 0.95f),
                start = Offset(w * 0.46f, h * 0.76f),
                end = Offset(w * 0.50f, h * 0.69f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // 'K'
            drawLine(
                color = Color.White.copy(alpha = 0.95f),
                start = Offset(w * 0.53f, h * 0.69f),
                end = Offset(w * 0.53f, h * 0.76f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = 0.95f),
                start = Offset(w * 0.58f, h * 0.69f),
                end = Offset(w * 0.53f, h * 0.725f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = 0.95f),
                start = Offset(w * 0.53f, h * 0.725f),
                end = Offset(w * 0.58f, h * 0.76f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
