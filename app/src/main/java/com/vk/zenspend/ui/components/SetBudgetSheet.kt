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
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.ZenBottomSheetShape
import com.vk.zenspend.ui.theme.ZenCardBorder
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetBudgetSheet(
    sheetState: SheetState,
    currentBudget: Double,
    onDismiss: () -> Unit,
    onSaveBudget: (Double) -> Unit
) {
    var amountInput by remember { mutableStateOf(if (currentBudget > 0) currentBudget.toInt().toString() else "25000") }

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
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Set Monthly Budget (₹)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ZenTextPrimary
            )
            Text(
                text = "This sets your monthly limit and calculates your daily budget.",
                fontSize = 13.sp,
                color = ZenTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Field
            OutlinedTextField(
                value = amountInput,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        amountInput = input
                    }
                },
                prefix = {
                    Text(
                        text = "₹ ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SagePrimary
                    )
                },
                placeholder = { Text("e.g. 25000", color = ZenTextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SageAccent,
                    unfocusedBorderColor = ZenCardBorder,
                    focusedContainerColor = PorcelainBg,
                    unfocusedContainerColor = PorcelainBg
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Preset Budget Chips (₹10,000, ₹25,000, ₹50,000, ₹1,00,000)
            Text(
                text = "Quick Presets",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ZenTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15000, 25000, 50000, 100000).forEach { preset ->
                    val label = if (preset >= 100000) "₹1 Lakh" else "₹${preset / 1000}k"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(ZenPillShape)
                            .background(if (amountInput == preset.toString()) SagePastel else PorcelainBg)
                            .border(
                                1.dp,
                                if (amountInput == preset.toString()) SagePrimary else ZenCardBorder,
                                ZenPillShape
                            )
                            .clickable { amountInput = preset.toString() }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (amountInput == preset.toString()) SagePrimary else ZenTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val amount = amountInput.toDoubleOrNull() ?: 25000.0
                    onSaveBudget(amount)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
            ) {
                Text(
                    text = "Save Monthly Budget",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
