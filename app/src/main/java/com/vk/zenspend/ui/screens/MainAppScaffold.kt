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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.ui.theme.ZenColorBackground
import com.vk.zenspend.ui.theme.ZenColorBorder
import com.vk.zenspend.ui.theme.ZenColorSagePastel
import com.vk.zenspend.ui.theme.ZenColorSagePrimary
import com.vk.zenspend.ui.theme.ZenColorSurface
import com.vk.zenspend.ui.theme.ZenColorTextMuted
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenPillShape

/**
 * Main App Container with Crystal-Clear 4-Tab Bottom Navigation:
 * Tab 0: Overview (Dashboard & Daily Allowance Gauge)
 * Tab 1: Daily Spend (Daily Expenditure, Search, Category Filter & Attached Bills)
 * Tab 2: Monthly Fixed (EMIs, Mutual Funds SIPs & Recurring Bills)
 * Tab 3: Analytics (Spend Breakdown, Morphing Donut & Bézier Trend Chart)
 */
@Composable
fun MainAppScaffold(
    viewModel: ZenSpendViewModel
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = ZenColorBackground,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(26.dp),
                            ambientColor = Color.Black.copy(alpha = 0.08f)
                        )
                        .clip(RoundedCornerShape(26.dp))
                        .background(ZenColorSurface)
                        .border(1.dp, ZenColorBorder.copy(alpha = 0.8f), RoundedCornerShape(26.dp))
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavTabItem(
                        icon = Icons.Default.Home,
                        label = "Overview",
                        isSelected = selectedTab == 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedTab = 0
                    }

                    NavTabItem(
                        icon = Icons.Default.ReceiptLong,
                        label = "Daily Spend",
                        isSelected = selectedTab == 1,
                        modifier = Modifier.weight(1f)
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedTab = 1
                    }

                    NavTabItem(
                        icon = Icons.Default.AccountBalanceWallet,
                        label = "Monthly Fixed",
                        isSelected = selectedTab == 2,
                        modifier = Modifier.weight(1.1f)
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedTab = 2
                    }

                    NavTabItem(
                        icon = Icons.Default.PieChart,
                        label = "Analytics",
                        isSelected = selectedTab == 3,
                        modifier = Modifier.weight(1f)
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedTab = 3
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = ZenMotion.DefaultSpring) togetherWith
                            fadeOut(animationSpec = ZenMotion.DefaultSpring)
                },
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    0 -> ZenDashboardScreen(viewModel = viewModel)
                    1 -> ExpensesLedgerScreen(viewModel = viewModel)
                    2 -> BudgetAndEmiScreen(viewModel = viewModel)
                    3 -> AnalyticsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun NavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) ZenColorSagePastel else Color.Transparent
    val contentColor = if (isSelected) ZenColorSagePrimary else ZenColorTextMuted

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
            maxLines = 1
        )
    }
}
