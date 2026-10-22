package com.vk.zenspend.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.domain.model.SpendPeriod
import com.vk.zenspend.ui.components.BezierTrendChart
import com.vk.zenspend.ui.components.MorphingDonutChart
import com.vk.zenspend.ui.components.ZenCard
import com.vk.zenspend.ui.theme.ApricotCaution
import com.vk.zenspend.ui.theme.DarkSagePastel
import com.vk.zenspend.ui.theme.DarkSagePrimary
import com.vk.zenspend.ui.theme.DarkTerracottaPastel
import com.vk.zenspend.ui.theme.LocalIsDarkTheme
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.ZenColorBackground
import com.vk.zenspend.ui.theme.ZenColorBorder
import com.vk.zenspend.ui.theme.ZenColorSagePastel
import com.vk.zenspend.ui.theme.ZenColorSagePrimary
import com.vk.zenspend.ui.theme.ZenColorSurface
import com.vk.zenspend.ui.theme.ZenColorTextMuted
import com.vk.zenspend.ui.theme.ZenColorTextPrimary
import com.vk.zenspend.ui.theme.ZenColorTextSecondary
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.TerracottaAlert
import com.vk.zenspend.ui.theme.TerracottaPastel
import com.vk.zenspend.ui.theme.ZenCardBorder
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: ZenSpendViewModel
) {
    val selectedPeriod by viewModel.selectedSpendPeriod.collectAsState()
    val monthlyComparison by viewModel.monthlySpendComparison.collectAsState()
    val yearlyComparison by viewModel.yearlySpendComparison.collectAsState()
    val monthlyTrendPoints by viewModel.monthlyTrendPoints.collectAsState()
    val yearlyTrendPoints by viewModel.yearlyTrendPoints.collectAsState()
    val categoryBreakdown by viewModel.categorySpendBreakdown.collectAsState()

    val comparison = if (selectedPeriod == SpendPeriod.THIS_MONTH) monthlyComparison else yearlyComparison
    val trendPoints = if (selectedPeriod == SpendPeriod.THIS_MONTH) monthlyTrendPoints else yearlyTrendPoints

    val haptic = LocalHapticFeedback.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZenColorBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Spending Analytics",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Monthly & Yearly Trends and Category Breakdown",
                    fontSize = 13.sp,
                    color = ZenTextSecondary
                )
            }
        }

        // Period Toggle (Monthly vs Yearly)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ZenPillShape)
                    .background(ZenColorSurface)
                    .border(1.dp, ZenColorBorder, ZenPillShape)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PeriodTabPill(
                    title = "This Month",
                    icon = Icons.Default.CalendarMonth,
                    isSelected = selectedPeriod == SpendPeriod.THIS_MONTH,
                    modifier = Modifier.weight(1f)
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.setSpendPeriod(SpendPeriod.THIS_MONTH)
                }

                PeriodTabPill(
                    title = "This Year",
                    icon = Icons.Default.AutoGraph,
                    isSelected = selectedPeriod == SpendPeriod.THIS_YEAR,
                    modifier = Modifier.weight(1f)
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.setSpendPeriod(SpendPeriod.THIS_YEAR)
                }
            }
        }

        // Spend Comparison Card (More Spent vs Less Spent)
        item {
            ZenCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedPeriod == SpendPeriod.THIS_MONTH) "Monthly Spend Pace" else "Yearly Spend Pace",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZenTextMuted
                        )

                        // More Spent / Less Spent Status Pill
                        val isDark = LocalIsDarkTheme.current
                        val isMore = comparison.isMoreSpent
                        val badgeBg = if (isMore) {
                            if (isDark) DarkTerracottaPastel else TerracottaPastel
                        } else {
                            if (isDark) DarkSagePastel else SagePastel
                        }
                        val badgeColor = if (isMore) {
                            TerracottaAlert
                        } else {
                            if (isDark) DarkSagePrimary else SagePrimary
                        }
                        val badgeIcon = if (isMore) Icons.Default.TrendingUp else Icons.Default.TrendingDown

                        Box(
                            modifier = Modifier
                                .clip(ZenPillShape)
                                .background(badgeBg)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = badgeIcon,
                                    contentDescription = null,
                                    tint = badgeColor,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isMore) "Above Budget" else "Under Budget (Saved)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = String.format(Locale.getDefault(), "₹%,.0f", comparison.currentPeriodAmount),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZenTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val summaryColor = if (comparison.isMoreSpent) {
                        TerracottaAlert
                    } else {
                        if (isDark) DarkSagePrimary else SagePrimary
                    }

                    Text(
                        text = comparison.summaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = summaryColor
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (selectedPeriod == SpendPeriod.THIS_MONTH) {
                            String.format(Locale.getDefault(), "Previous Month: ₹%,.0f", comparison.previousPeriodAmount)
                        } else {
                            String.format(Locale.getDefault(), "Previous Year: ₹%,.0f", comparison.previousPeriodAmount)
                        },
                        fontSize = 11.sp,
                        color = ZenTextMuted
                    )
                }
            }
        }

        // Section 1: Interactive Morphing Donut Chart (Categories)
        item {
            ZenCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = "Category Donut",
                                tint = ZenColorSagePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Category Breakdown",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZenTextPrimary
                            )
                        }

                        Text(
                            text = "Interactive Donut",
                            fontSize = 11.sp,
                            color = ZenTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    MorphingDonutChart(
                        items = categoryBreakdown,
                        totalSpent = comparison.currentPeriodAmount
                    )
                }
            }
        }

        // Section 2: Smooth Bézier Trend Chart
        item {
            ZenCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoGraph,
                                contentDescription = "Trend Line",
                                tint = ZenColorSagePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedPeriod == SpendPeriod.THIS_MONTH) "Daily Spend Trajectory" else "Monthly Spend Trajectory",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZenTextPrimary
                            )
                        }

                        Text(
                            text = "Bézier Curve",
                            fontSize = 11.sp,
                            color = ZenTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val isDarkTheme = LocalIsDarkTheme.current
                    val trendFill = if (comparison.isMoreSpent) {
                        ApricotCaution.copy(alpha = 0.25f)
                    } else {
                        if (isDarkTheme) DarkSagePastel.copy(alpha = 0.55f) else SagePastel.copy(alpha = 0.55f)
                    }

                    BezierTrendChart(
                        points = trendPoints,
                        lineColor = if (comparison.isMoreSpent) ApricotCaution else SageAccent,
                        fillColor = trendFill
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PeriodTabPill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(ZenPillShape)
            .background(if (isSelected) ZenColorSagePrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else ZenTextSecondary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else ZenTextSecondary
            )
        }
    }
}
