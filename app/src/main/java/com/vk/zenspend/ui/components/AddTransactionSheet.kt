package com.vk.zenspend.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.vk.zenspend.domain.model.Category
import com.vk.zenspend.domain.model.Transaction
import com.vk.zenspend.domain.model.TransactionType
import com.vk.zenspend.domain.util.MathEvaluator
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.TerracottaAlert
import com.vk.zenspend.ui.theme.TerracottaPastel
import com.vk.zenspend.ui.theme.ZenBottomSheetShape
import com.vk.zenspend.ui.theme.ZenCardBorder
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    sheetState: SheetState,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSaveTransaction: (Transaction) -> Unit
) {
    var rawInput by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var receiptUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        receiptUri = uri
    }

    var selectedCategory by remember {
        mutableStateOf(categories.firstOrNull { it.type == selectedType } ?: categories.firstOrNull())
    }

    val evaluatedAmount = MathEvaluator.evaluate(rawInput)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = ZenBottomSheetShape,
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(ZenCardBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Header with Type Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Expense / Income",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary
                )

                // Expense vs Income Pill Toggle
                Row(
                    modifier = Modifier
                        .clip(ZenPillShape)
                        .background(PorcelainBg)
                        .border(1.dp, ZenCardBorder, ZenPillShape)
                        .padding(3.dp)
                ) {
                    TypeToggleButton(
                        text = "Expense",
                        isSelected = selectedType == TransactionType.EXPENSE,
                        activeColor = TerracottaAlert,
                        activeBg = TerracottaPastel
                    ) {
                        selectedType = TransactionType.EXPENSE
                        selectedCategory = categories.firstOrNull { it.type == TransactionType.EXPENSE }
                    }

                    TypeToggleButton(
                        text = "Income",
                        isSelected = selectedType == TransactionType.INCOME,
                        activeColor = SagePrimary,
                        activeBg = SagePastel
                    ) {
                        selectedType = TransactionType.INCOME
                        selectedCategory = categories.firstOrNull { it.type == TransactionType.INCOME }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display Input Line + Real-Time Math Result (in ₹)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(PorcelainBg)
                    .border(1.dp, ZenCardBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (rawInput.isEmpty()) "₹0.00" else "₹$rawInput",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (rawInput.isEmpty()) ZenTextMuted else ZenTextPrimary
                        )

                        // Real-time calculated preview in ₹
                        if (evaluatedAmount != null && rawInput.any { it in "+-×÷*/" }) {
                            Text(
                                text = String.format(Locale.US, "= ₹%.2f", evaluatedAmount),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SagePrimary
                            )
                        }
                    }

                    if (rawInput.any { it in "+-×÷*/" }) {
                        Text(
                            text = "Inline calculator active",
                            fontSize = 11.sp,
                            color = SagePrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expense Name / Label Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        text = "Expense Name (e.g. DMart Grocery, Coffee, Rent)",
                        fontSize = 13.sp,
                        color = ZenTextMuted
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SageAccent,
                    unfocusedBorderColor = ZenCardBorder,
                    focusedContainerColor = PorcelainBg,
                    unfocusedContainerColor = PorcelainBg
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Horizontal Selector
            val filteredCategories = categories.filter { it.type == selectedType }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCategories) { cat ->
                    val isSelected = selectedCategory?.id == cat.id
                    CategoryChip(
                        category = cat,
                        isSelected = isSelected
                    ) {
                        selectedCategory = cat
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Prominent Attach Bill / Receipt Photo Card
            ZenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { imagePickerLauncher.launch("image/*") },
                elevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (receiptUri != null) SagePastel else PorcelainBg)
                                .border(1.dp, if (receiptUri != null) SageAccent else ZenCardBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (receiptUri != null) Icons.Default.Check else Icons.Default.AddPhotoAlternate,
                                contentDescription = "Bill Icon",
                                tint = if (receiptUri != null) SagePrimary else ZenTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (receiptUri != null) "✓ Bill / Invoice Attached" else "Attach Bill or Invoice Photo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (receiptUri != null) SagePrimary else ZenTextPrimary
                            )
                            Text(
                                text = if (receiptUri != null) "Tap to change image" else "Upload receipt photo from gallery (Optional)",
                                fontSize = 11.sp,
                                color = ZenTextMuted
                            )
                        }
                    }

                    if (receiptUri != null) {
                        IconButton(
                            onClick = { receiptUri = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove bill",
                                tint = TerracottaAlert,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(ZenPillShape)
                                .background(SagePastel)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Browse 📷",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SagePrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Integrated Custom Calculator Numpad
            CalculatorNumpad(
                onDigitClick = { digit ->
                    if (rawInput.length < 24) {
                        rawInput += digit
                    }
                },
                onOperatorClick = { op ->
                    if (rawInput.isNotEmpty() && rawInput.last() !in "+-×÷*/") {
                        rawInput += " $op "
                    }
                },
                onClearClick = {
                    rawInput = ""
                },
                onBackspaceClick = {
                    rawInput = rawInput.trimEnd()
                    if (rawInput.isNotEmpty()) {
                        rawInput = rawInput.dropLast(1).trimEnd()
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Confirm Button
            Button(
                onClick = {
                    val finalAmount = evaluatedAmount ?: 0.0
                    val currentCat = selectedCategory ?: categories.firstOrNull()
                    if (finalAmount > 0.0 && currentCat != null) {
                        val finalTitle = when {
                            title.isNotBlank() -> title.trim()
                            else -> currentCat.name
                        }
                        val newTx = Transaction(
                            id = 0L,
                            title = finalTitle,
                            amount = finalAmount,
                            type = selectedType,
                            categoryId = currentCat.id,
                            categoryName = currentCat.name,
                            categoryIcon = currentCat.iconName,
                            categoryColor = currentCat.colorHex,
                            timestamp = System.currentTimeMillis(),
                            note = note.takeIf { it.isNotBlank() },
                            receiptImagePath = receiptUri?.toString()
                        )
                        onSaveTransaction(newTx)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = (evaluatedAmount ?: 0.0) > 0.0,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SagePrimary,
                    disabledContainerColor = PorcelainBg
                )
            ) {
                Text(
                    text = if ((evaluatedAmount ?: 0.0) > 0.0) {
                        String.format(Locale.US, "Confirm • ₹%.2f", evaluatedAmount)
                    } else {
                        "Enter Amount in ₹"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if ((evaluatedAmount ?: 0.0) > 0.0) Color.White else ZenTextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TypeToggleButton(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    activeBg: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(ZenPillShape)
            .background(if (isSelected) activeBg else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeColor else ZenTextSecondary
        )
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) SagePrimary else ZenCardBorder
    val bgColor = if (isSelected) SagePastel else PorcelainBg

    Row(
        modifier = Modifier
            .clip(ZenPillShape)
            .background(bgColor)
            .border(1.dp, borderColor, ZenPillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(category.colorHex))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = category.name,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = ZenTextPrimary
        )
    }
}
