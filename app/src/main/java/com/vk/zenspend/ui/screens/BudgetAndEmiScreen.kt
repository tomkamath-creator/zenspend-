package com.vk.zenspend.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.domain.model.Emi
import com.vk.zenspend.ui.components.AddEmiSheet
import com.vk.zenspend.ui.components.RollingTickerText
import com.vk.zenspend.ui.components.SetBudgetSheet
import com.vk.zenspend.ui.components.ZenCard
import com.vk.zenspend.ui.theme.ApricotCaution
import com.vk.zenspend.ui.theme.ApricotPastel
import com.vk.zenspend.ui.theme.DarkApricotPastel
import com.vk.zenspend.ui.theme.DarkSagePastel
import com.vk.zenspend.ui.theme.DarkSagePrimary
import com.vk.zenspend.ui.theme.LocalIsDarkTheme
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.TerracottaAlert
import com.vk.zenspend.ui.theme.TerracottaPastel
import com.vk.zenspend.ui.theme.ZenColorBackground
import com.vk.zenspend.ui.theme.ZenColorBorder
import com.vk.zenspend.ui.theme.ZenColorSagePastel
import com.vk.zenspend.ui.theme.ZenColorSagePrimary
import com.vk.zenspend.ui.theme.ZenColorSurface
import com.vk.zenspend.ui.theme.ZenColorSurfaceVariant
import com.vk.zenspend.ui.theme.ZenColorTextMuted
import com.vk.zenspend.ui.theme.ZenColorTextPrimary
import com.vk.zenspend.ui.theme.ZenColorTextSecondary
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetAndEmiScreen(
    viewModel: ZenSpendViewModel
) {
    val monthlyBudget by viewModel.monthlyBudgetAmount.collectAsState()
    val totalExpenses by viewModel.totalExpensesThisMonth.collectAsState()
    val allEmis by viewModel.emis.collectAsState()
    val totalCommitmentsBurden by viewModel.totalMonthlyEmiBurden.collectAsState()
    val totalInvestedPaid by viewModel.totalMonthlyInvestedPaid.collectAsState()
    val totalPending by viewModel.totalMonthlyPending.collectAsState()

    var selectedSubTab by remember { mutableStateOf("ALL") }
    var isSetBudgetOpen by remember { mutableStateOf(false) }
    var isAddCommitmentOpen by remember { mutableStateOf(false) }
    var commitmentToEdit by remember { mutableStateOf<Emi?>(null) }

    val budgetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val emiSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val budgetProgress = if (monthlyBudget > 0) {
        (totalExpenses / monthlyBudget).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    val filteredList = when (selectedSubTab) {
        "EMI" -> allEmis.filter { it.commitmentType == "EMI" }
        "MUTUAL_FUND" -> allEmis.filter { it.commitmentType == "MUTUAL_FUND" }
        "BILL" -> allEmis.filter { it.commitmentType == "BILL" }
        else -> allEmis
    }

    Scaffold(
        containerColor = ZenColorBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    commitmentToEdit = null
                    isAddCommitmentOpen = true
                },
                containerColor = ZenColorSagePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Commitment",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add Monthly",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ZenColorBackground),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Monthly Commitments",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZenTextPrimary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "EMIs, Mutual Funds (SIPs) & Recurring Bills in ₹",
                            fontSize = 13.sp,
                            color = ZenTextSecondary
                        )
                    }

                    // Set Budget quick button
                    Box(
                        modifier = Modifier
                            .clip(ZenPillShape)
                            .background(ZenColorSagePastel)
                            .clickable { isSetBudgetOpen = true }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = ZenColorSagePrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Budget Limit",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZenColorSagePrimary
                            )
                        }
                    }
                }
            }

            // Summary Card: Total Monthly Fixed, Paid/Invested, Pending
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
                            Column {
                                Text(
                                    text = "Total Monthly Commitments",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ZenTextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = String.format(Locale.getDefault(), "₹%,.0f/mo", totalCommitmentsBurden),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZenTextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(ZenPillShape)
                                    .background(ZenColorSurfaceVariant)
                                    .border(1.dp, ZenColorBorder, ZenPillShape)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${allEmis.size} active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZenColorTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Paid / Invested Box
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(ZenColorSagePastel)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Paid / Invested",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ZenColorSagePrimary
                                    )
                                    Text(
                                        text = String.format(Locale.getDefault(), "₹%,.0f", totalInvestedPaid),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ZenColorSagePrimary
                                    )
                                }
                            }

                            // Pending Box
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (LocalIsDarkTheme.current) DarkApricotPastel else ApricotPastel
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Pending",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ApricotCaution
                                    )
                                    Text(
                                        text = String.format(Locale.getDefault(), "₹%,.0f", totalPending),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ApricotCaution
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sub-Tabs: All, Loans & EMIs, Mutual Funds (SIPs), Monthly Bills
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterSubTabChip(
                            label = "All (${allEmis.size})",
                            isSelected = selectedSubTab == "ALL",
                            onClick = { selectedSubTab = "ALL" }
                        )
                    }
                    item {
                        FilterSubTabChip(
                            label = "Loans & EMIs (${allEmis.count { it.commitmentType == "EMI" }})",
                            isSelected = selectedSubTab == "EMI",
                            onClick = { selectedSubTab = "EMI" }
                        )
                    }
                    item {
                        FilterSubTabChip(
                            label = "Mutual Funds (${allEmis.count { it.commitmentType == "MUTUAL_FUND" }})",
                            isSelected = selectedSubTab == "MUTUAL_FUND",
                            onClick = { selectedSubTab = "MUTUAL_FUND" }
                        )
                    }
                    item {
                        FilterSubTabChip(
                            label = "Monthly Bills (${allEmis.count { it.commitmentType == "BILL" }})",
                            isSelected = selectedSubTab == "BILL",
                            onClick = { selectedSubTab = "BILL" }
                        )
                    }
                }
            }

            // Commitment List
            if (filteredList.isEmpty()) {
                item {
                    ZenCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(ZenColorSagePastel),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (selectedSubTab) {
                                        "MUTUAL_FUND" -> Icons.Default.TrendingUp
                                        "BILL" -> Icons.Default.ReceiptLong
                                        else -> Icons.Default.CreditCard
                                    },
                                    contentDescription = null,
                                    tint = ZenColorSagePrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = when (selectedSubTab) {
                                    "MUTUAL_FUND" -> "No Mutual Fund SIPs Added"
                                    "BILL" -> "No Monthly Bills Added"
                                    "EMI" -> "No Active Loans or EMIs"
                                    else -> "No Commitments Yet"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZenTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap '+ Add Monthly' below to track and log recurring payments.",
                                fontSize = 12.sp,
                                color = ZenTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { item ->
                    CommitmentCardItem(
                        emi = item,
                        onTogglePaid = {
                            viewModel.toggleEmiPaid(item)
                        },
                        onEdit = {
                            commitmentToEdit = item
                            isAddCommitmentOpen = true
                        },
                        onDelete = {
                            viewModel.deleteEmi(item)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (isSetBudgetOpen) {
        SetBudgetSheet(
            sheetState = budgetSheetState,
            currentBudget = monthlyBudget,
            onDismiss = { isSetBudgetOpen = false },
            onSaveBudget = { newAmount ->
                viewModel.setMonthlyBudget(newAmount)
            }
        )
    }

    if (isAddCommitmentOpen) {
        AddEmiSheet(
            sheetState = emiSheetState,
            emiToEdit = commitmentToEdit,
            onDismiss = {
                isAddCommitmentOpen = false
                commitmentToEdit = null
            },
            onSaveEmi = { savedEmi ->
                if (commitmentToEdit != null) {
                    viewModel.updateEmi(savedEmi)
                } else {
                    viewModel.addEmi(savedEmi)
                }
                commitmentToEdit = null
            }
        )
    }
}

@Composable
private fun FilterSubTabChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val bgColor = if (isSelected) {
        if (isDark) DarkSagePastel else ZenColorSagePastel
    } else ZenColorSurface

    val borderColor = if (isSelected) SageAccent else ZenColorBorder
    val textColor = if (isSelected) {
        if (isDark) DarkSagePrimary else SagePrimary
    } else ZenTextSecondary

    Box(
        modifier = Modifier
            .clip(ZenPillShape)
            .background(bgColor)
            .border(1.dp, borderColor, ZenPillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun CommitmentCardItem(
    emi: Emi,
    onTogglePaid: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val isDark = LocalIsDarkTheme.current

    val (badgeText, badgeIcon, badgeColor) = when (emi.commitmentType) {
        "MUTUAL_FUND" -> Triple("SIP", Icons.Default.TrendingUp, Color(0xFF38B2AC))
        "BILL" -> Triple("BILL", Icons.Default.ReceiptLong, Color(0xFF805AD5))
        else -> Triple("EMI", Icons.Default.CreditCard, Color(0xFFDD6B20))
    }

    val iconBg = if (emi.isPaidThisMonth) {
        if (isDark) DarkSagePastel else SagePastel
    } else {
        if (isDark) DarkApricotPastel else ApricotPastel
    }

    val iconTint = if (emi.isPaidThisMonth) {
        if (isDark) DarkSagePrimary else SagePrimary
    } else ApricotCaution

    val buttonBg = if (emi.isPaidThisMonth) {
        if (isDark) DarkSagePastel else SagePastel
    } else ZenColorSurfaceVariant

    val buttonBorder = if (emi.isPaidThisMonth) {
        if (isDark) DarkSagePrimary else SagePrimary
    } else ZenColorBorder

    val buttonText = if (emi.isPaidThisMonth) {
        if (isDark) DarkSagePrimary else SagePrimary
    } else ZenTextSecondary

    val toggleLabel = when {
        emi.isPaidThisMonth -> if (emi.commitmentType == "MUTUAL_FUND") "Invested ✓" else "Paid ✓"
        emi.commitmentType == "MUTUAL_FUND" -> "Invest"
        else -> "Mark Paid"
    }

    val dateSubtext = when (emi.commitmentType) {
        "MUTUAL_FUND" -> "SIP on ${emi.dueDayOfMonth}th of month"
        "BILL" -> "Due on ${emi.dueDayOfMonth}th of month"
        else -> "Due on ${emi.dueDayOfMonth}th • ${emi.remainingMonths?.let { "$it mos left" } ?: "Active"}"
    }

    ZenCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (emi.isPaidThisMonth) Icons.Default.Check else badgeIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = emi.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZenTextPrimary,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = dateSubtext,
                    fontSize = 11.sp,
                    color = ZenTextSecondary
                )
            }

            // Amount + Action
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = String.format(Locale.US, "₹%,.0f", emi.monthlyAmount),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Toggle Paid/Invested Button
                Box(
                    modifier = Modifier
                        .clip(ZenPillShape)
                        .background(buttonBg)
                        .border(1.dp, buttonBorder, ZenPillShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onTogglePaid()
                        }
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = toggleLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = buttonText
                    )
                }
            }

            // Edit Action Button
            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Commitment",
                    tint = ZenTextMuted,
                    modifier = Modifier.size(15.dp)
                )
            }

            // Delete Action Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = ZenTextMuted.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
