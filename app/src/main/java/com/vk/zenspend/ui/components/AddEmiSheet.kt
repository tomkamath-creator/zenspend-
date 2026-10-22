package com.vk.zenspend.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.domain.model.Emi
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.ZenBottomSheetShape
import com.vk.zenspend.ui.theme.ZenCardBorder
import com.vk.zenspend.ui.theme.ZenColorBorder
import com.vk.zenspend.ui.theme.ZenColorSagePrimary
import com.vk.zenspend.ui.theme.ZenColorSurface
import com.vk.zenspend.ui.theme.ZenColorSurfaceVariant
import com.vk.zenspend.ui.theme.ZenColorTextPrimary
import com.vk.zenspend.ui.theme.ZenColorTextSecondary
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmiSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSaveEmi: (Emi) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var dueDayText by remember { mutableStateOf("5") }
    var tenureText by remember { mutableStateOf("") }

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
                text = "Add Monthly EMI / Loan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ZenTextPrimary
            )
            Text(
                text = "Track your monthly loans and EMI due dates in ₹.",
                fontSize = 13.sp,
                color = ZenTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // EMI Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("EMI Name (e.g. Home Loan, iPhone EMI, Car Loan)") },
                placeholder = { Text("e.g. Phone EMI", color = ZenTextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Monthly EMI Amount in ₹
            OutlinedTextField(
                value = amountText,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amountText = it },
                label = { Text("Monthly Amount (₹)") },
                prefix = { Text("₹ ", fontWeight = FontWeight.Bold, color = ZenColorSagePrimary) },
                placeholder = { Text("e.g. 2499", color = ZenTextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Due Date & Tenure Row
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = dueDayText,
                    onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) dueDayText = it },
                    label = { Text("Due Day (1-31)") },
                    placeholder = { Text("5", color = ZenTextMuted) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = tenureText,
                    onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) tenureText = it },
                    label = { Text("Months Left") },
                    placeholder = { Text("e.g. 12", color = ZenTextMuted) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    val dueDay = dueDayText.toIntOrNull()?.coerceIn(1, 31) ?: 5
                    val remaining = tenureText.toIntOrNull()
                    if (title.isNotBlank() && amount > 0) {
                        onSaveEmi(
                            Emi(
                                title = title.trim(),
                                monthlyAmount = amount,
                                dueDayOfMonth = dueDay,
                                remainingMonths = remaining,
                                isPaidThisMonth = false
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = title.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ZenColorSagePrimary,
                    disabledContainerColor = ZenColorSurfaceVariant
                )
            ) {
                Text(
                    text = "Save EMI",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
