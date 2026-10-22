package com.vk.zenspend.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.domain.model.TrendPoint
import com.vk.zenspend.ui.theme.DarkCardBorder
import com.vk.zenspend.ui.theme.DarkSurface
import com.vk.zenspend.ui.theme.LocalIsDarkTheme
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.ZenCardBorder
import com.vk.zenspend.ui.theme.ZenColorSurface
import com.vk.zenspend.ui.theme.ZenColorSurfaceVariant
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary
import java.util.Locale
import kotlin.math.max

/**
 * Smooth Bézier Curve Trend Chart:
 * - Natural cubic spline interpolation for fluid trajectories
 * - Calming translucent gradient glow beneath the curve
 * - Touch & scrub to reveal exact day/month values and amounts
 */
@Composable
fun BezierTrendChart(
    points: List<TrendPoint>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 190.dp,
    lineColor: Color = SageAccent,
    fillColor: Color = SagePastel.copy(alpha = 0.55f)
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val haptic = LocalHapticFeedback.current
    val isDark = LocalIsDarkTheme.current

    if (points.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(chartHeight)
                .clip(RoundedCornerShape(20.dp))
                .background(ZenColorSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No trend data available",
                fontSize = 13.sp,
                color = ZenTextMuted
            )
        }
        return
    }

    val maxValue = max(1.0, points.maxOfOrNull { it.value } ?: 1.0)

    Column(modifier = modifier.fillMaxWidth()) {
        // Floating Scrubber Callout Banner
        if (selectedIndex in points.indices) {
            val pt = points[selectedIndex]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(ZenPillShape)
                        .background(ZenColorSurface)
                        .border(1.dp, lineColor.copy(alpha = 0.4f), ZenPillShape)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(lineColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${pt.label}: ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZenTextSecondary
                        )
                        Text(
                            text = String.format(Locale.getDefault(), "₹%,.0f", pt.value),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZenTextPrimary
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Touch and drag along the line to inspect spending",
                    fontSize = 11.sp,
                    color = ZenTextMuted
                )
            }
        }

        // Canvas Bézier drawing area
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
                .pointerInput(points) {
                    detectTapGestures(
                        onPress = { offset ->
                            val slotWidth = size.width / (points.size - 1).coerceAtLeast(1)
                            val idx = ((offset.x + slotWidth / 2f) / slotWidth).toInt().coerceIn(0, points.size - 1)
                            selectedIndex = idx
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    )
                }
                .pointerInput(points) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val slotWidth = size.width / (points.size - 1).coerceAtLeast(1)
                            val idx = ((offset.x + slotWidth / 2f) / slotWidth).toInt().coerceIn(0, points.size - 1)
                            selectedIndex = idx
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val slotWidth = size.width / (points.size - 1).coerceAtLeast(1)
                            val idx = ((change.position.x + slotWidth / 2f) / slotWidth).toInt().coerceIn(0, points.size - 1)
                            if (idx != selectedIndex) {
                                selectedIndex = idx
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        },
                        onDragEnd = {
                            // keep last selected point or leave visible
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val padBottom = 20.dp.toPx()
            val padTop = 14.dp.toPx()
            val usableHeight = height - padBottom - padTop

            val count = points.size
            if (count < 2) return@Canvas

            val stepX = width / (count - 1).toFloat()

            val coords = points.mapIndexed { i, pt ->
                val x = i * stepX
                val normY = (pt.value / maxValue).toFloat().coerceIn(0f, 1f)
                val y = height - padBottom - (normY * usableHeight)
                Offset(x, y)
            }

            // Draw smooth cubic Bézier stroke path
            val strokePath = Path().apply {
                moveTo(coords[0].x, coords[0].y)
                for (i in 0 until coords.size - 1) {
                    val p0 = coords[i]
                    val p1 = coords[i + 1]
                    val controlX1 = p0.x + (p1.x - p0.x) / 2f
                    val controlY1 = p0.y
                    val controlX2 = p0.x + (p1.x - p0.x) / 2f
                    val controlY2 = p1.y
                    cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                }
            }

            // Fill path under the curve
            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(coords.last().x, height - padBottom)
                lineTo(coords.first().x, height - padBottom)
                close()
            }

            // 1. Draw gradient fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        fillColor,
                        fillColor.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    startY = padTop,
                    endY = height - padBottom
                )
            )

            // 2. Draw smooth stroke line
            drawPath(
                path = strokePath,
                color = lineColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // 3. Draw baseline
            drawLine(
                color = if (isDark) DarkCardBorder else ZenCardBorder,
                start = Offset(0f, height - padBottom),
                end = Offset(width, height - padBottom),
                strokeWidth = 1.dp.toPx()
            )

            // 4. Highlight active scrub point
            if (selectedIndex in coords.indices) {
                val activeCoord = coords[selectedIndex]

                // Vertical dashed / soft indicator line
                drawLine(
                    color = lineColor.copy(alpha = 0.45f),
                    start = Offset(activeCoord.x, padTop),
                    end = Offset(activeCoord.x, height - padBottom),
                    strokeWidth = 1.5.dp.toPx()
                )

                // Outer halo
                drawCircle(
                    color = lineColor.copy(alpha = 0.25f),
                    radius = 9.dp.toPx(),
                    center = activeCoord
                )
                // Solid center dot
                drawCircle(
                    color = if (isDark) DarkSurface else Color.White,
                    radius = 5.dp.toPx(),
                    center = activeCoord
                )
                drawCircle(
                    color = lineColor,
                    radius = 3.5.dp.toPx(),
                    center = activeCoord
                )
            }
        }

        // X-axis guide labels (Sparse selection e.g. 4-6 labels)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val labelIndices = if (points.size <= 6) {
                points.indices.toList()
            } else {
                val step = max(1, points.size / 5)
                val list = mutableListOf<Int>()
                var idx = 0
                while (idx < points.size) {
                    list.add(idx)
                    idx += step
                }
                if (!list.contains(points.size - 1)) list.add(points.size - 1)
                list
            }

            labelIndices.forEach { i ->
                Text(
                    text = points[i].label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedIndex == i) lineColor else ZenTextMuted
                )
            }
        }
    }
}
