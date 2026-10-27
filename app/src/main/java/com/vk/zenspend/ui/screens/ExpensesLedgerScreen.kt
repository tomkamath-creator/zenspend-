package com.vk.zenspend.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.domain.model.Transaction
import com.vk.zenspend.domain.model.TransactionType
import com.vk.zenspend.ui.components.AddTransactionSheet
import com.vk.zenspend.ui.components.ReceiptViewerModal
import com.vk.zenspend.ui.components.ZenCard
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
import com.vk.zenspend.ui.theme.ZenColorTextMuted
import com.vk.zenspend.ui.theme.ZenColorTextPrimary
import com.vk.zenspend.ui.theme.ZenColorTextSecondary
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesLedgerScreen(
    viewModel: ZenSpendViewModel
) {
    val groupedTransactions by viewModel.groupedTransactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var selectedReceiptUri by remember { mutableStateOf<Pair<String, String>?>(null) }
    var isAddExpenseOpen by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = ZenColorBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    transactionToEdit = null
                    isAddExpenseOpen = true
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
                        contentDescription = "Add Expense",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add Expense",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Daily Expenditure",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZenTextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Track, search, edit & view receipts of your daily spending in ₹",
                        fontSize = 13.sp,
                        color = ZenTextSecondary
                    )
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Search by name, category, or note...", color = ZenTextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ZenTextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ZenColorTextPrimary,
                        unfocusedTextColor = ZenColorTextPrimary,
                        focusedBorderColor = SageAccent,
                        unfocusedBorderColor = ZenColorBorder,
                        focusedContainerColor = ZenColorSurface,
                        unfocusedContainerColor = ZenColorSurface
                    ),
                    singleLine = true
                )
            }

            // Filter Chips
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Expenses", "Income").forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(ZenPillShape)
                                .background(if (isSelected) ZenColorSagePrimary else ZenColorSurface)
                                .border(1.dp, if (isSelected) ZenColorSagePrimary else ZenColorBorder, ZenPillShape)
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else ZenTextPrimary
                            )
                        }
                    }
                }
            }

            // Grouped Transactions
            if (groupedTransactions.isEmpty()) {
                item {
                    ZenCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = ZenTextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "No records matching \"$searchQuery\"" else "No transactions recorded yet",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = ZenTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap '+ Add Expense' below to add your first expense.",
                                fontSize = 12.sp,
                                color = ZenTextMuted
                            )
                        }
                    }
                }
            } else {
                groupedTransactions.forEach { (dateHeader, txList) ->
                    val filteredList = when (selectedFilter) {
                        "Expenses" -> txList.filter { it.type == TransactionType.EXPENSE }
                        "Income" -> txList.filter { it.type == TransactionType.INCOME }
                        else -> txList
                    }

                    if (filteredList.isNotEmpty()) {
                        item {
                            Text(
                                text = dateHeader,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ZenTextMuted,
                                modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                            )
                        }

                        items(filteredList, key = { it.id }) { tx ->
                            LedgerItemRow(
                                transaction = tx,
                                onViewReceipt = { uri ->
                                    selectedReceiptUri = Pair(uri, tx.title)
                                },
                                onEdit = {
                                    transactionToEdit = tx
                                    isAddExpenseOpen = true
                                },
                                onDelete = {
                                    viewModel.deleteTransaction(tx)
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (isAddExpenseOpen) {
        AddTransactionSheet(
            sheetState = sheetState,
            categories = categories,
            transactionToEdit = transactionToEdit,
            onDismiss = {
                isAddExpenseOpen = false
                transactionToEdit = null
            },
            onSaveTransaction = { savedTx ->
                if (transactionToEdit != null) {
                    viewModel.updateTransaction(savedTx)
                } else {
                    viewModel.addTransaction(savedTx)
                }
                transactionToEdit = null
            },
            onAddCustomCategory = { name, icon, color ->
                viewModel.addCustomCategory(name, icon, color)
            }
        )
    }

    selectedReceiptUri?.let { (uri, title) ->
        ReceiptViewerModal(
            imageUriString = uri,
            title = title,
            onDismiss = { selectedReceiptUri = null }
        )
    }
}

@Composable
private fun LedgerItemRow(
    transaction: Transaction,
    onViewReceipt: (String) -> Unit,
    onEdit: () -> Unit,
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

            Spacer(modifier = Modifier.width(12.dp))

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

            // Attached Bill / Receipt Thumbnail Button
            if (transaction.receiptImagePath != null) {
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clip(ZenPillShape)
                        .background(ZenColorSagePastel)
                        .border(1.dp, SageAccent.copy(alpha = 0.5f), ZenPillShape)
                        .clickable { onViewReceipt(transaction.receiptImagePath) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "View Bill",
                            tint = ZenColorSagePrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Bill 📷",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZenColorSagePrimary
                        )
                    }
                }
            }

            // Amount in ₹
            Text(
                text = String.format(Locale.US, "%s₹%.2f", amountPrefix, transaction.amount),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )

            // Edit Action Button
            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .padding(start = 2.dp)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Transaction",
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
