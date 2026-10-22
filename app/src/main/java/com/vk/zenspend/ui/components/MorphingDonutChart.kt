package com.vk.zenspend.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.domain.model.CategorySpend
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.ZenCardBorder
import com.vk.zenspend.ui.theme.ZenColorBorder
import com.vk.zenspend.ui.theme.ZenColorSurfaceVariant
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Interactive Morphing Donut Chart:
 * - Smooth spring animated sweep angles
 * - Scrubbable / tappable segments that morph and expand outward
 * - Center displays selected category details or total spend
 * - Responsive category legend
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MorphingDonutChart(
    items: List<CategorySpend>,
    totalSpent: Double,
    modifier: Modifier = Modifier,
    chartSize: Dp = 230.dp
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val haptic = LocalHapticFeedback.current

    if (items.isEmpty() || totalSpent <= 0.0) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ZenColorSurfaceVariant)
                .border(1.dp, ZenColorBorder, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No spending recorded yet",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZenTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add expenses to see the interactive category donut",
                    fontSize = 12.sp,
                    color = ZenTextMuted
                )
            }
        }
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(chartSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(chartSize)
                    .pointerInput(items) {
                        detectTapGestures { tapOffset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = tapOffset.x - center.x
                            val dy = tapOffset.y - center.y
                            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                            val outerRadius = size.width / 2f
                            val innerRadius = outerRadius - 32.dp.toPx()

                            if (dist in (innerRadius * 0.7f)..outerRadius) {
                                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                if (angle < 0) angle += 360f
                                // Align with our startAngle of -90f (270 deg)
                                val normalizedAngle = (angle + 90f) % 360f

                                var accumulated = 0f
                                var clickedIdx = -1
                                for (i in items.indices) {
                                    val sweep = items[i].percentage * 360f
                                    if (normalizedAngle >= accumulated && normalizedAngle < accumulated + sweep) {
                                        clickedIdx = i
                                        break
                                    }
                                    accumulated += sweep
                                }

                                if (clickedIdx != -1) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedIndex = if (selectedIndex == clickedIdx) -1 else clickedIdx
                                }
                            } else {
                                selectedIndex = -1
                            }
                        }
                    }
            ) {
                val canvasDim = size.width
                val center = Offset(canvasDim / 2f, canvasDim / 2f)
                val baseStrokeWidth = 24.dp.toPx()
                val activeStrokeWidth = 32.dp.toPx()
                val baseRadius = (canvasDim - activeStrokeWidth - 12.dp.toPx()) / 2f

                var startAngle = -90f

                items.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    val sweepAngle = (item.percentage * 360f).coerceAtLeast(1.5f)
                    val strokeW = if (isSelected) activeStrokeWidth else baseStrokeWidth
                    val sliceColor = Color(item.colorHex)

                    // If selected, translate slightly outward along the bisector angle
                    val bisectorAngle = Math.toRadians((startAngle + sweepAngle / 2f).toDouble())
                    val offsetDistance = if (isSelected) 8.dp.toPx() else 0f
                    val offsetX = (offsetDistance * cos(bisectorAngle)).toFloat()
                    val offsetY = (offsetDistance * sin(bisectorAngle)).toFloat()

                    val sliceCenter = Offset(center.x + offsetX, center.y + offsetY)
                    val topLeft = Offset(sliceCenter.x - baseRadius, sliceCenter.y - baseRadius)
                    val arcSize = Size(baseRadius * 2f, baseRadius * 2f)

                    drawArc(
                        color = if (selectedIndex != -1 && !isSelected) sliceColor.copy(alpha = 0.35f) else sliceColor,
                        startAngle = startAngle + 1.5f,
                        sweepAngle = sweepAngle - 3f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )

                    startAngle += sweepAngle
                }
            }

            // Center Content: Selected Category or Total
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(chartSize * 0.58f)
                    .padding(horizontal = 4.dp)
            ) {
                if (selectedIndex in items.indices) {
                    val selected = items[selectedIndex]
                    Text(
                        text = selected.categoryName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(selected.colorHex),
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = String.format(Locale.getDefault(), "₹%,.0f", selected.amount),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZenTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(ZenPillShape)
                            .background(Color(selected.colorHex).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f%% of spend", selected.percentage * 100),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(selected.colorHex)
                        )
                    }
                } else {
                    Text(
                        text = "Total Spent",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ZenTextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = String.format(Locale.getDefault(), "₹%,.0f", totalSpent),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZenTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tap slice to view",
                        fontSize = 10.sp,
                        color = ZenTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Category breakdown interactive pills
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index
                val itemColor = Color(item.colorHex)

                Row(
                    modifier = Modifier
                        .clip(ZenPillShape)
                        .background(if (isSelected) itemColor.copy(alpha = 0.18f) else ZenColorSurfaceVariant)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) itemColor else ZenColorBorder,
                            shape = ZenPillShape
                        )
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedIndex = if (isSelected) -1 else index
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(itemColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.categoryName,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = ZenTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = String.format(Locale.getDefault(), "₹%,.0f", item.amount),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) itemColor else ZenTextMuted
                    )
                }
            }
        }
    }
}
