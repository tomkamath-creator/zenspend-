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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.vk.zenspend.ui.components.ReceiptViewerModal
import com.vk.zenspend.ui.components.ZenCard
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.TerracottaAlert
import com.vk.zenspend.ui.theme.TerracottaPastel
import com.vk.zenspend.ui.theme.ZenCardBorder
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpensesLedgerScreen(
    viewModel: ZenSpendViewModel
) {
    val groupedTransactions by viewModel.groupedTransactions.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedReceiptUri by remember { mutableStateOf<Pair<String, String>?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PorcelainBg),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Expense Ledger",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Search & inspect your bills, receipts and expenses in ₹",
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
                    focusedBorderColor = SageAccent,
                    unfocusedBorderColor = ZenCardBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
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
                            .background(if (isSelected) SagePrimary else Color.White)
                            .border(1.dp, if (isSelected) SagePrimary else ZenCardBorder, ZenPillShape)
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
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "No transactions matching \"$searchQuery\"" else "No transactions recorded yet",
                            fontSize = 14.sp,
                            color = ZenTextSecondary
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

            // Bill / Receipt Attached Badge
            if (transaction.receiptImagePath != null) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(ZenPillShape)
                        .background(SagePastel)
                        .border(1.dp, SageAccent.copy(alpha = 0.5f), ZenPillShape)
                        .clickable { onViewReceipt(transaction.receiptImagePath) }
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "View Bill",
                            tint = SagePrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "View Bill 📷",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SagePrimary
                        )
                    }
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
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}
