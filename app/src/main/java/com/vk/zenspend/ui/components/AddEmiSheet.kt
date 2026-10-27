package com.vk.zenspend.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.domain.model.Emi
import com.vk.zenspend.ui.theme.DarkSagePastel
import com.vk.zenspend.ui.theme.DarkSagePrimary
import com.vk.zenspend.ui.theme.LocalIsDarkTheme
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmiSheet(
    sheetState: SheetState,
    emiToEdit: Emi? = null,
    onDismiss: () -> Unit,
    onSaveEmi: (Emi) -> Unit
) {
    val isEditMode = emiToEdit != null

    var selectedType by remember(emiToEdit) {
        mutableStateOf(emiToEdit?.commitmentType ?: "EMI")
    }
    var title by remember(emiToEdit) {
        mutableStateOf(emiToEdit?.title ?: "")
    }
    var amountText by remember(emiToEdit) {
        mutableStateOf(
            emiToEdit?.monthlyAmount?.let {
                if (it % 1.0 == 0.0) it.toLong().toString() else it.toString()
            } ?: ""
        )
    }
    var dueDayText by remember(emiToEdit) {
        mutableStateOf(emiToEdit?.dueDayOfMonth?.toString() ?: "5")
    }
    var tenureText by remember(emiToEdit) {
        mutableStateOf(emiToEdit?.totalTenureMonths?.toString() ?: "")
    }
    var notesText by remember(emiToEdit) {
        mutableStateOf(emiToEdit?.notes ?: "")
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = ZenColorTextPrimary,
        unfocusedTextColor = ZenColorTextPrimary,
        focusedBorderColor = SageAccent,
        unfocusedBorderColor = ZenColorBorder,
        focusedContainerColor = ZenColorSurfaceVariant,
        unfocusedContainerColor = ZenColorSurfaceVariant,
        focusedLabelColor = SageAccent,
        unfocusedLabelColor = ZenColorTextSecondary
    )

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
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = if (isEditMode) "Edit Monthly Commitment" else "Add Monthly Commitment",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ZenTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Commitment Type Segmented Selector: EMI, Mutual Fund (SIP), Monthly Bill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ZenPillShape)
                    .background(ZenColorSurfaceVariant)
                    .border(1.dp, ZenColorBorder, ZenPillShape)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CommitmentTypeTab(
                    label = "Loan / EMI",
                    isSelected = selectedType == "EMI",
                    modifier = Modifier.weight(1f)
                ) {
                    selectedType = "EMI"
                }

                CommitmentTypeTab(
                    label = "Mutual Fund (SIP)",
                    isSelected = selectedType == "MUTUAL_FUND",
                    modifier = Modifier.weight(1.3f)
                ) {
                    selectedType = "MUTUAL_FUND"
                }

                CommitmentTypeTab(
                    label = "Monthly Bill",
                    isSelected = selectedType == "BILL",
                    modifier = Modifier.weight(1f)
                ) {
                    selectedType = "BILL"
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title / Scheme Name Field
            val namePlaceholder = when (selectedType) {
                "MUTUAL_FUND" -> "e.g. HDFC Top 100 SIP, Parag Parikh Flexi"
                "BILL" -> "e.g. Apartment Rent, Electricity, WiFi"
                else -> "e.g. Home Loan, Car Loan, iPhone EMI"
            }
            val nameLabel = when (selectedType) {
                "MUTUAL_FUND" -> "Fund / SIP Name *"
                "BILL" -> "Bill / Subscription Name *"
                else -> "Loan / EMI Name *"
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(nameLabel) },
                placeholder = { Text(namePlaceholder, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Monthly Amount Field
            val amountLabel = when (selectedType) {
                "MUTUAL_FUND" -> "Monthly SIP Amount (₹) *"
                "BILL" -> "Monthly Bill Amount (₹) *"
                else -> "Monthly EMI Amount (₹) *"
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                label = { Text(amountLabel) },
                placeholder = { Text("e.g. 5000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Day of Month (1 - 31)
            val dayLabel = when (selectedType) {
                "MUTUAL_FUND" -> "SIP Date of Month (1-31)"
                "BILL" -> "Bill Due Date (1-31)"
                else -> "EMI Due Date (1-31)"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = dueDayText,
                    onValueChange = {
                        val num = it.filter { ch -> ch.isDigit() }.take(2)
                        val intVal = num.toIntOrNull() ?: 0
                        if (num.isEmpty() || (intVal in 1..31)) {
                            dueDayText = num
                        }
                    },
                    label = { Text(dayLabel) },
                    placeholder = { Text("5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    singleLine = true
                )

                if (selectedType == "EMI") {
                    OutlinedTextField(
                        value = tenureText,
                        onValueChange = { tenureText = it.filter { ch -> ch.isDigit() }.take(3) },
                        label = { Text("Total Tenure (Months)") },
                        placeholder = { Text("e.g. 24") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notes / Folio
            val notesLabel = when (selectedType) {
                "MUTUAL_FUND" -> "Folio / Scheme Details (Optional)"
                "BILL" -> "Provider / Account ID (Optional)"
                else -> "Bank / Loan Account (Optional)"
            }

            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text(notesLabel) },
                placeholder = { Text("Optional reference notes") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
            val canSave = title.isNotBlank() && parsedAmount > 0.0

            Button(
                onClick = {
                    if (canSave) {
                        val dueDay = dueDayText.toIntOrNull() ?: 5
                        val tenure = tenureText.toIntOrNull()
                        val updated = Emi(
                            id = emiToEdit?.id ?: 0L,
                            title = title.trim(),
                            monthlyAmount = parsedAmount,
                            dueDayOfMonth = dueDay,
                            totalTenureMonths = tenure,
                            remainingMonths = tenure,
                            isPaidThisMonth = emiToEdit?.isPaidThisMonth ?: false,
                            notes = notesText.takeIf { it.isNotBlank() },
                            commitmentType = selectedType
                        )
                        onSaveEmi(updated)
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
                val buttonLabel = when {
                    !canSave -> "Enter Name & Amount"
                    isEditMode -> "Update Commitment"
                    selectedType == "MUTUAL_FUND" -> "Save Mutual Fund SIP"
                    selectedType == "BILL" -> "Save Monthly Bill"
                    else -> "Save Monthly EMI"
                }
                Text(
                    text = buttonLabel,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (canSave) Color.White else ZenTextMuted
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CommitmentTypeTab(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val bgColor = if (isSelected) {
        if (isDark) DarkSagePastel else ZenColorSagePastel
    } else Color.Transparent

    val textColor = if (isSelected) {
        if (isDark) DarkSagePrimary else SagePrimary
    } else ZenColorTextSecondary

    Box(
        modifier = modifier
            .clip(ZenPillShape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            maxLines = 1
        )
    }
}
