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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.vk.zenspend.ui.theme.PorcelainBg
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetAndEmiScreen(
    viewModel: ZenSpendViewModel
) {
    val monthlyBudget by viewModel.monthlyBudgetAmount.collectAsState()
    val totalExpenses by viewModel.totalExpensesThisMonth.collectAsState()
    val emis by viewModel.emis.collectAsState()
    val totalEmiBurden by viewModel.totalMonthlyEmiBurden.collectAsState()

    var isSetBudgetOpen by remember { mutableStateOf(false) }
    var isAddEmiOpen by remember { mutableStateOf(false) }
    val budgetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val emiSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val haptic = LocalHapticFeedback.current

    val budgetProgress = if (monthlyBudget > 0) {
        (totalExpenses / monthlyBudget).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PorcelainBg),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title
        item {
            Column {
                Text(
                    text = "Budget & EMI Tracker",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Plan your monthly limit & recurring commitments in ₹",
                    fontSize = 13.sp,
                    color = ZenTextSecondary
                )
            }
        }

        // Monthly Budget Card
        item {
            ZenCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Monthly Budget Limit",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZenTextSecondary
                        )
                        RollingTickerText(
                            amount = monthlyBudget,
                            currencySymbol = "₹"
                        )
                    }

                    // Edit Budget Button
                    Box(
                        modifier = Modifier
                            .clip(ZenPillShape)
                            .background(SagePastel)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isSetBudgetOpen = true
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Budget",
                                tint = SagePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Set Budget",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SagePrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { budgetProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = if (budgetProgress > 0.85f) TerracottaAlert else SageAccent,
                    trackColor = SagePastel,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = String.format(Locale.US, "Spent: ₹%.2f", totalExpenses),
                        fontSize = 12.sp,
                        color = ZenTextSecondary
                    )
                    Text(
                        text = String.format(Locale.US, "Left: ₹%.2f", maxOf(0.0, monthlyBudget - totalExpenses)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (totalExpenses > monthlyBudget) TerracottaAlert else SagePrimary
                    )
                }
            }
        }

        // EMI Commitment Overview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Monthly EMIs & Loans",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZenTextPrimary
                    )
                    Text(
                        text = String.format(Locale.US, "Total burden: ₹%.2f / month", totalEmiBurden),
                        fontSize = 12.sp,
                        color = ApricotCaution
                    )
                }

                // Add EMI Bouncy Button
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.90f else 1f,
                    animationSpec = ZenMotion.DefaultSpring,
                    label = "addEmiBounce"
                )

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .clip(ZenPillShape)
                        .background(SagePrimary)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isAddEmiOpen = true
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add EMI",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add EMI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // EMI List
        if (emis.isEmpty()) {
            item {
                ZenCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(SagePastel),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = SagePrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Active EMIs",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZenTextPrimary
                        )
                        Text(
                            text = "Keep track of phone, bike, or home loans easily in ₹.",
                            fontSize = 12.sp,
                            color = ZenTextSecondary
                        )
                    }
                }
            }
        } else {
            items(emis, key = { it.id }) { emi ->
                EmiCardItem(
                    emi = emi,
                    onTogglePaid = {
                        viewModel.toggleEmiPaid(emi)
                    },
                    onDelete = {
                        viewModel.deleteEmi(emi)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
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

    if (isAddEmiOpen) {
        AddEmiSheet(
            sheetState = emiSheetState,
            onDismiss = { isAddEmiOpen = false },
            onSaveEmi = { newEmi ->
                viewModel.addEmi(newEmi)
            }
        )
    }
}

@Composable
private fun EmiCardItem(
    emi: Emi,
    onTogglePaid: () -> Unit,
    onDelete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

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
                    .background(if (emi.isPaidThisMonth) SagePastel else ApricotPastel),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (emi.isPaidThisMonth) Icons.Default.Check else Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = if (emi.isPaidThisMonth) SagePrimary else ApricotCaution,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = emi.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZenTextPrimary
                )
                Text(
                    text = "Due on ${emi.dueDayOfMonth}th of month • ${emi.remainingMonths?.let { "$it mos left" } ?: "Active"}",
                    fontSize = 12.sp,
                    color = ZenTextSecondary
                )
            }

            // Amount
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.US, "₹%.2f", emi.monthlyAmount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary
                )

                // Paid / Unpaid Button
                Box(
                    modifier = Modifier
                        .clip(ZenPillShape)
                        .background(if (emi.isPaidThisMonth) SagePastel else PorcelainBg)
                        .border(
                            1.dp,
                            if (emi.isPaidThisMonth) SagePrimary else ZenCardBorder,
                            ZenPillShape
                        )
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onTogglePaid()
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (emi.isPaidThisMonth) "Paid ✓" else "Mark Paid",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (emi.isPaidThisMonth) SagePrimary else ZenTextSecondary
                    )
                }
            }

            // Delete
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete EMI",
                    tint = ZenTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
