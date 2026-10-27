package com.vk.zenspend.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.domain.model.Category
import com.vk.zenspend.domain.model.Transaction
import com.vk.zenspend.domain.model.TransactionType
import com.vk.zenspend.domain.util.ImageStorageHelper
import com.vk.zenspend.domain.util.MathEvaluator
import com.vk.zenspend.ui.theme.ApricotCaution
import com.vk.zenspend.ui.theme.DarkSagePastel
import com.vk.zenspend.ui.theme.DarkSagePrimary
import com.vk.zenspend.ui.theme.DarkTerracottaPastel
import com.vk.zenspend.ui.theme.LocalIsDarkTheme
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.TerracottaAlert
import com.vk.zenspend.ui.theme.TerracottaPastel
import com.vk.zenspend.ui.theme.ZenBottomSheetShape
import com.vk.zenspend.ui.theme.ZenColorBorder
import com.vk.zenspend.ui.theme.ZenColorSagePastel
import com.vk.zenspend.ui.theme.ZenColorSagePrimary
import com.vk.zenspend.ui.theme.ZenColorSurface
import com.vk.zenspend.ui.theme.ZenColorSurfaceVariant
import com.vk.zenspend.ui.theme.ZenColorTextPrimary
import com.vk.zenspend.ui.theme.ZenColorTextSecondary
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    sheetState: SheetState,
    categories: List<Category>,
    transactionToEdit: Transaction? = null,
    onDismiss: () -> Unit,
    onSaveTransaction: (Transaction) -> Unit,
    onAddCustomCategory: ((name: String, iconName: String, colorHex: Long) -> Unit)? = null
) {
    val context = LocalContext.current
    val isEditMode = transactionToEdit != null

    var rawInput by remember(transactionToEdit) {
        mutableStateOf(
            transactionToEdit?.amount?.let {
                if (it % 1.0 == 0.0) it.toLong().toString() else it.toString()
            } ?: ""
        )
    }
    var selectedType by remember(transactionToEdit) {
        mutableStateOf(transactionToEdit?.type ?: TransactionType.EXPENSE)
    }
    var title by remember(transactionToEdit) {
        mutableStateOf(transactionToEdit?.title ?: "")
    }
    var note by remember(transactionToEdit) {
        mutableStateOf(transactionToEdit?.note ?: "")
    }
    var receiptPath by remember(transactionToEdit) {
        mutableStateOf(transactionToEdit?.receiptImagePath)
    }
    var receiptBitmap by remember(receiptPath) {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(receiptPath) {
        if (!receiptPath.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                receiptBitmap = ImageStorageHelper.loadBitmap(receiptPath!!, context)
            }
        } else {
            receiptBitmap = null
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val localSavedPath = ImageStorageHelper.saveImageLocally(context, uri)
            receiptPath = localSavedPath ?: uri.toString()
        }
    }

    // No preselected category when adding fresh! User MUST select one
    var selectedCategory by remember(transactionToEdit) {
        mutableStateOf<Category?>(
            transactionToEdit?.let { tx -> categories.find { it.id == tx.categoryId } }
        )
    }

    var showCustomCategoryDialog by remember { mutableStateOf(false) }

    val evaluatedAmount = MathEvaluator.evaluate(rawInput)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = ZenBottomSheetShape,
        containerColor = ZenColorSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(ZenColorBorder)
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
                    text = if (isEditMode) "Edit Transaction" else "Add Transaction",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary
                )

                // Expense vs Income Pill Toggle
                Row(
                    modifier = Modifier
                        .clip(ZenPillShape)
                        .background(ZenColorSurfaceVariant)
                        .border(1.dp, ZenColorBorder, ZenPillShape)
                        .padding(3.dp)
                ) {
                    TypeToggleButton(
                        text = "Expense",
                        isSelected = selectedType == TransactionType.EXPENSE,
                        activeColor = TerracottaAlert,
                        activeBg = TerracottaPastel
                    ) {
                        selectedType = TransactionType.EXPENSE
                        if (selectedCategory?.type != TransactionType.EXPENSE) {
                            selectedCategory = null
                        }
                    }

                    TypeToggleButton(
                        text = "Income",
                        isSelected = selectedType == TransactionType.INCOME,
                        activeColor = SagePrimary,
                        activeBg = SagePastel
                    ) {
                        selectedType = TransactionType.INCOME
                        if (selectedCategory?.type != TransactionType.INCOME) {
                            selectedCategory = null
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display Input Line + Real-Time Math Result (in ₹)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ZenColorSurfaceVariant)
                    .border(1.dp, ZenColorBorder, RoundedCornerShape(20.dp))
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

                        if (evaluatedAmount != null && rawInput.any { it in "+-×÷*/" }) {
                            Text(
                                text = String.format(Locale.US, "= ₹%.2f", evaluatedAmount),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ZenColorSagePrimary
                            )
                        }
                    }

                    if (rawInput.any { it in "+-×÷*/" }) {
                        Text(
                            text = "Calculator active",
                            fontSize = 11.sp,
                            color = ZenColorSagePrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Name / Label Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        text = "Expense Name (e.g. Grocery, Dining, Cab)",
                        fontSize = 13.sp,
                        color = ZenTextMuted
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ZenColorTextPrimary,
                    unfocusedTextColor = ZenColorTextPrimary,
                    focusedBorderColor = SageAccent,
                    unfocusedBorderColor = ZenColorBorder,
                    focusedContainerColor = ZenColorSurfaceVariant,
                    unfocusedContainerColor = ZenColorSurfaceVariant
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Selection Header & Warning
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedCategory == null) "Select Category *" else "Category: ${selectedCategory?.name}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selectedCategory == null) ApricotCaution else ZenColorTextSecondary
                )

                if (selectedCategory == null) {
                    Text(
                        text = "(Required)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApricotCaution
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Category Horizontal Selector + Custom Category Button
            val filteredCategories = categories.filter { it.type == selectedType }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
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

                // Add Custom Category Chip
                item {
                    Box(
                        modifier = Modifier
                            .clip(ZenPillShape)
                            .background(ZenColorSurfaceVariant)
                            .border(1.dp, SageAccent, ZenPillShape)
                            .clickable { showCustomCategoryDialog = true }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = SageAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Custom",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SageAccent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Attached Bill / Screenshot Photo Card
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
                        if (receiptBitmap != null) {
                            Image(
                                bitmap = receiptBitmap!!.asImageBitmap(),
                                contentDescription = "Attached receipt preview",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, SageAccent, RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (receiptPath != null) ZenColorSagePastel else ZenColorSurfaceVariant)
                                    .border(1.dp, if (receiptPath != null) SageAccent else ZenColorBorder, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (receiptPath != null) Icons.Default.Check else Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Bill Icon",
                                    tint = if (receiptPath != null) ZenColorSagePrimary else ZenTextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (receiptPath != null) "✓ Bill / Screenshot Attached" else "Attach Bill or Screenshot",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (receiptPath != null) ZenColorSagePrimary else ZenTextPrimary
                            )
                            Text(
                                text = if (receiptPath != null) "Tap to change image" else "Select photo from device (Optional)",
                                fontSize = 11.sp,
                                color = ZenTextMuted
                            )
                        }
                    }

                    if (receiptPath != null) {
                        IconButton(
                            onClick = {
                                receiptPath = null
                                receiptBitmap = null
                            },
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
                                .background(ZenColorSagePastel)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Browse 📷",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZenColorSagePrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Custom Calculator Numpad
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

            // Confirm / Save Button
            val canSave = (evaluatedAmount ?: 0.0) > 0.0 && selectedCategory != null

            Button(
                onClick = {
                    val finalAmount = evaluatedAmount ?: 0.0
                    val currentCat = selectedCategory
                    if (finalAmount > 0.0 && currentCat != null) {
                        val finalTitle = when {
                            title.isNotBlank() -> title.trim()
                            else -> currentCat.name
                        }
                        val finalTx = Transaction(
                            id = transactionToEdit?.id ?: 0L,
                            title = finalTitle,
                            amount = finalAmount,
                            type = selectedType,
                            categoryId = currentCat.id,
                            categoryName = currentCat.name,
                            categoryIcon = currentCat.iconName,
                            categoryColor = currentCat.colorHex,
                            timestamp = transactionToEdit?.timestamp ?: System.currentTimeMillis(),
                            note = note.takeIf { it.isNotBlank() },
                            receiptImagePath = receiptPath
                        )
                        onSaveTransaction(finalTx)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = canSave,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ZenColorSagePrimary,
                    disabledContainerColor = ZenColorSurfaceVariant
                )
            ) {
                val buttonText = when {
                    selectedCategory == null -> "Please Select a Category"
                    (evaluatedAmount ?: 0.0) <= 0.0 -> "Enter Amount in ₹"
                    isEditMode -> String.format(Locale.US, "Update Transaction • ₹%.2f", evaluatedAmount)
                    else -> String.format(Locale.US, "Save %s • ₹%.2f", if (selectedType == TransactionType.EXPENSE) "Expense" else "Income", evaluatedAmount)
                }

                Text(
                    text = buttonText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (canSave) Color.White else ZenTextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showCustomCategoryDialog) {
        CustomCategoryDialog(
            onDismiss = { showCustomCategoryDialog = false },
            onConfirm = { name, colorHex ->
                onAddCustomCategory?.invoke(name, "Label", colorHex)
                selectedCategory = Category(
                    id = "custom_${System.currentTimeMillis()}",
                    name = name,
                    iconName = "Label",
                    colorHex = colorHex,
                    type = selectedType,
                    isDefault = false
                )
                showCustomCategoryDialog = false
            }
        )
    }
}

@Composable
private fun CustomCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, colorHex: Long) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    val colors = listOf(
        0xFF48BB78L, // Emerald
        0xFF3182CEL, // Ocean
        0xFF805AD5L, // Purple
        0xFFD69E2EL, // Amber
        0xFFE53E3EL, // Crimson
        0xFF38B2ACL, // Teal
        0xFFED64A6L  // Rose
    )
    var selectedColor by remember { mutableStateOf(colors[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Custom Category",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ZenTextPrimary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    placeholder = { Text("e.g. Gym, Pet Food, Coffee") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ZenColorTextPrimary,
                        unfocusedTextColor = ZenColorTextPrimary,
                        focusedBorderColor = SageAccent,
                        unfocusedBorderColor = ZenColorBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Pick Category Color",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ZenTextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { colorVal ->
                        val isPicked = selectedColor == colorVal
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .border(
                                    width = if (isPicked) 3.dp else 1.dp,
                                    color = if (isPicked) ZenColorTextPrimary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorVal },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPicked) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onConfirm(categoryName.trim(), selectedColor)
                    }
                },
                enabled = categoryName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ZenColorSagePrimary)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ZenTextMuted)
            }
        },
        containerColor = ZenColorSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun TypeToggleButton(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    activeBg: Color,
    onClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val resolvedBg = if (isSelected) {
        if (isDark) {
            if (activeColor == TerracottaAlert) DarkTerracottaPastel else DarkSagePastel
        } else activeBg
    } else Color.Transparent

    val resolvedTextColor = if (isSelected) {
        if (isDark && activeColor == SagePrimary) DarkSagePrimary else activeColor
    } else ZenTextSecondary

    Box(
        modifier = Modifier
            .clip(ZenPillShape)
            .background(resolvedBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = resolvedTextColor
        )
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val baseColor = Color(category.colorHex)
    val bgColor = if (isSelected) {
        if (isDark) baseColor.copy(alpha = 0.28f) else baseColor.copy(alpha = 0.15f)
    } else {
        ZenColorSurfaceVariant
    }
    val borderColor = if (isSelected) baseColor else ZenColorBorder

    Box(
        modifier = Modifier
            .clip(ZenPillShape)
            .background(bgColor)
            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, ZenPillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(baseColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) ZenTextPrimary else ZenTextSecondary
            )
        }
    }
}
