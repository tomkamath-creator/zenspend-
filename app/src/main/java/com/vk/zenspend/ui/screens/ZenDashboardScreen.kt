package com.vk.zenspend.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.domain.model.Transaction
import com.vk.zenspend.domain.model.TransactionType
import com.vk.zenspend.ui.components.AddTransactionSheet
import com.vk.zenspend.ui.components.CalmAllowanceGauge
import com.vk.zenspend.ui.components.ReceiptViewerModal
import com.vk.zenspend.ui.components.RollingTickerText
import com.vk.zenspend.ui.components.SetBudgetSheet
import com.vk.zenspend.ui.components.ZenCard
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenDashboardScreen(
    viewModel: ZenSpendViewModel
) {
    val categories by viewModel.categories.collectAsState()
    val groupedTransactions by viewModel.groupedTransactions.collectAsState()
    val totalExpenses by viewModel.totalExpensesThisMonth.collectAsState()
    val totalIncome by viewModel.totalIncomeThisMonth.collectAsState()
    val monthlyBudget by viewModel.monthlyBudgetAmount.collectAsState()
    val dailyCalmAllowance by viewModel.dailyCalmAllowance.collectAsState()
    val remainingDays = viewModel.remainingDaysInMonth

    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val budgetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isAddSheetOpen by remember { mutableStateOf(false) }
    var isSetBudgetOpen by remember { mutableStateOf(false) }
    var selectedReceiptUri by remember { mutableStateOf<Pair<String, String>?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PorcelainBg,
        floatingActionButton = {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val fabScale by animateFloatAsState(
                targetValue = if (isPressed) 0.88f else 1f,
                animationSpec = ZenMotion.DefaultSpring,
                label = "fabBounce"
            )

            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    isAddSheetOpen = true
                },
                modifier = Modifier.scale(fabScale),
                shape = CircleShape,
                containerColor = SagePrimary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                interactionSource = interactionSource
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Header: Title & Tagline
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ZenSpend",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZenTextPrimary,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "Smart Expense & EMI Calculator",
                                fontSize = 13.sp,
                                color = ZenTextSecondary
                            )
                        }

                        // Set Budget Pill Button
                        Box(
                            modifier = Modifier
                                .clip(ZenPillShape)
                                .background(SagePastel)
                                .clickable { isSetBudgetOpen = true }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Set Budget",
                                    tint = SagePrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Set Budget",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SagePrimary
                                )
                            }
                        }
                    }
                }

                // Month's Calm Allowance Liquid Gauge with Water Drop Ripple Card
                item {
                    ZenCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CalmAllowanceGauge(
                                dailyCalmAllowance = dailyCalmAllowance,
                                spentSoFar = totalExpenses,
                                monthlyBudget = monthlyBudget,
                                remainingDays = remainingDays,
                                onClick = { isSetBudgetOpen = true }
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Stats Pill Row (Spent vs Income in ₹)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MiniMetricBox(
                                    label = "Spent this month",
                                    amount = totalExpenses,
                                    isExpense = true,
                                    modifier = Modifier.weight(1f)
                                )
                                MiniMetricBox(
                                    label = "Income recorded",
                                    amount = totalIncome,
                                    isExpense = false,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Recent Transactions Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Expenses",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ZenTextPrimary
                        )
                        Text(
                            text = "Tap + to add",
                            fontSize = 12.sp,
                            color = ZenTextMuted
                        )
                    }
                }

                // Empty State if no transactions
                if (groupedTransactions.isEmpty()) {
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
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(SagePastel),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Spa,
                                        contentDescription = null,
                                        tint = SagePrimary,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Your ledger is peaceful and clear",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ZenTextPrimary
                                )
                                Text(
                                    text = "Tap the + button to log your first mindful expense in ₹.",
                                    fontSize = 13.sp,
                                    color = ZenTextSecondary
                                )
                            }
                        }
                    }
                } else {
                    groupedTransactions.forEach { (dateHeader, txList) ->
                        item {
                            Text(
                                text = dateHeader,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ZenTextMuted,
                                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                            )
                        }

                        items(txList, key = { it.id }) { tx ->
                            DashboardTransactionRow(
                                transaction = tx,
                                onViewReceipt = { uri ->
                                    selectedReceiptUri = Pair(uri, tx.title)
                                },
                                onDelete = {
                                    viewModel.deleteTransaction(tx)
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Deleted \"${tx.title}\"",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.addTransaction(tx)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }

    // Add Transaction Sheet
    if (isAddSheetOpen) {
        AddTransactionSheet(
            sheetState = addSheetState,
            categories = categories,
            onDismiss = { isAddSheetOpen = false },
            onSaveTransaction = { tx ->
                viewModel.addTransaction(tx)
                isAddSheetOpen = false
            }
        )
    }

    // Set Budget Sheet
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

    // View Receipt Modal
    selectedReceiptUri?.let { (uri, title) ->
        ReceiptViewerModal(
            imageUriString = uri,
            title = title,
            onDismiss = { selectedReceiptUri = null }
        )
    }
}

@Composable
private fun MiniMetricBox(
    label: String,
    amount: Double,
    isExpense: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isExpense) TerracottaPastel else SagePastel
    val textColor = if (isExpense) TerracottaAlert else SagePrimary
    val icon = if (isExpense) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ZenTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            RollingTickerText(
                amount = amount,
                currencySymbol = "₹",
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
        }
    }
}

@Composable
private fun DashboardTransactionRow(
    transaction: Transaction,
    onViewReceipt: (String) -> Unit,
    onDelete: () -> Unit
) {
    val isExpense = transaction.type == TransactionType.EXPENSE
    val amountColor = if (isExpense) ZenTextPrimary else SageAccent
    val amountPrefix = if (isExpense) "-" else "+"
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(transaction.timestamp))

    ZenCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(transaction.categoryColor).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color(transaction.categoryColor))
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZenTextPrimary
                )
                Text(
                    text = "${transaction.categoryName} • $formattedTime",
                    fontSize = 12.sp,
                    color = ZenTextMuted
                )
            }

            // Attached Bill Icon
            if (transaction.receiptImagePath != null) {
                IconButton(
                    onClick = { onViewReceipt(transaction.receiptImagePath) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "View Bill",
                        tint = SagePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Amount in ₹
            Text(
                text = String.format(Locale.US, "%s₹%.2f", amountPrefix, transaction.amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )

            // Delete action button
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = ZenTextMuted.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
