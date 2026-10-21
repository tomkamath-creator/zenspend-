package com.vk.zenspend.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.domain.model.Emi
import com.vk.zenspend.ui.components.ZenCard
import com.vk.zenspend.ui.components.ZenLogo
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.ZenCardBorder
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary
import java.util.Locale

/**
 * Interactive Q&A Onboarding Setup:
 * Q1: "Hi enter name"
 * Q2: "Hi {Name}, what's your total salary?"
 * Q3: "What's your monthly budget?"
 * Q4: "Any EMIs to track?"
 * Once added, this is saved and not asked again!
 */
@Composable
fun InteractiveOnboardingScreen(
    onComplete: (name: String, salary: Double, budget: Double, emi: Emi?) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var nameInput by remember { mutableStateOf("") }
    var salaryInput by remember { mutableStateOf("") }
    var budgetInput by remember { mutableStateOf("") }

    // Optional EMI fields
    var hasEmi by remember { mutableStateOf(false) }
    var emiTitle by remember { mutableStateOf("") }
    var emiAmountInput by remember { mutableStateOf("") }
    var emiDueDayInput by remember { mutableStateOf("5") }

    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PorcelainBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                // Progress Step Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..4) {
                        val isPassed = i <= step
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .weight(1f)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isPassed) SagePrimary else ZenCardBorder)
                        )
                        if (i < 4) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Animated Question Container
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally(animationSpec = ZenMotion.IntOffsetSpring) { it } + fadeIn() togetherWith
                                    slideOutHorizontally(animationSpec = ZenMotion.IntOffsetSpring) { -it } + fadeOut()
                        } else {
                            slideInHorizontally(animationSpec = ZenMotion.IntOffsetSpring) { -it } + fadeIn() togetherWith
                                    slideOutHorizontally(animationSpec = ZenMotion.IntOffsetSpring) { it } + fadeOut()
                        }
                    },
                    label = "onboardingStep"
                ) { currentStep ->
                    when (currentStep) {
                        1 -> StepNameQuestion(
                            name = nameInput,
                            onNameChange = { nameInput = it }
                        )

                        2 -> StepSalaryQuestion(
                            userName = nameInput.trim(),
                            salary = salaryInput,
                            onSalaryChange = { salaryInput = it }
                        )

                        3 -> StepBudgetQuestion(
                            userName = nameInput.trim(),
                            salaryAmount = salaryInput.toDoubleOrNull() ?: 0.0,
                            budget = budgetInput,
                            onBudgetChange = { budgetInput = it }
                        )

                        4 -> StepEmiQuestion(
                            userName = nameInput.trim(),
                            hasEmi = hasEmi,
                            onHasEmiToggle = { hasEmi = it },
                            emiTitle = emiTitle,
                            onEmiTitleChange = { emiTitle = it },
                            emiAmount = emiAmountInput,
                            onEmiAmountChange = { emiAmountInput = it },
                            emiDueDay = emiDueDayInput,
                            onEmiDueDayChange = { emiDueDayInput = it }
                        )
                    }
                }
            }

            // Bottom Navigation Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isNextEnabled = when (step) {
                    1 -> nameInput.trim().isNotEmpty()
                    2 -> (salaryInput.toDoubleOrNull() ?: 0.0) >= 0.0
                    3 -> (budgetInput.toDoubleOrNull() ?: 0.0) >= 0.0
                    4 -> true
                    else -> true
                }

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (step < 4) {
                            step += 1
                        } else {
                            // Finish and complete onboarding
                            val salaryVal = salaryInput.toDoubleOrNull() ?: 0.0
                            val budgetVal = budgetInput.toDoubleOrNull() ?: 0.0
                            val emi = if (hasEmi && emiTitle.isNotBlank()) {
                                Emi(
                                    title = emiTitle.trim(),
                                    monthlyAmount = emiAmountInput.toDoubleOrNull() ?: 0.0,
                                    dueDayOfMonth = emiDueDayInput.toIntOrNull() ?: 5,
                                    totalTenureMonths = 12,
                                    remainingMonths = 12,
                                    isPaidThisMonth = false,
                                    notes = "Added during initial setup"
                                )
                            } else null

                            onComplete(nameInput.trim(), salaryVal, budgetVal, emi)
                        }
                    },
                    enabled = isNextEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SagePrimary,
                        disabledContainerColor = SagePastel
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (step == 4) "Finish Setup & Enter ZenSpend ✨" else "Continue →",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (step > 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            if (step > 1) step -= 1
                        }
                    ) {
                        Text(
                            text = "← Back to previous question",
                            fontSize = 13.sp,
                            color = ZenTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepNameQuestion(
    name: String,
    onNameChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        ZenLogo(size = 72.dp)
        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .clip(ZenPillShape)
                .background(SagePastel)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Question 1 of 4",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SagePrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Hello! What's your name?",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = ZenTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "ZenSpend personalizes your budget experience and keeps your finances peaceful.",
            fontSize = 14.sp,
            color = ZenTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        ZenCard(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Your Name or Nickname") },
                placeholder = { Text("e.g. Vignesh") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Name",
                        tint = SagePrimary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SageAccent,
                    unfocusedBorderColor = ZenCardBorder
                )
            )
        }
    }
}

@Composable
private fun StepSalaryQuestion(
    userName: String,
    salary: String,
    onSalaryChange: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(SagePastel),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Savings,
                contentDescription = "Salary",
                tint = SagePrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .clip(ZenPillShape)
                .background(SagePastel)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Question 2 of 4",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SagePrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Hi $userName! 💰\nWhat's your total monthly salary?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ZenTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Enter your monthly take-home salary or expected monthly income in ₹.",
            fontSize = 14.sp,
            color = ZenTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        ZenCard(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = salary,
                onValueChange = onSalaryChange,
                label = { Text("Monthly Salary (₹)") },
                placeholder = { Text("50000") },
                prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SageAccent,
                    unfocusedBorderColor = ZenCardBorder
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Quick Presets:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ZenTextMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(25000.0, 50000.0, 75000.0, 100000.0).forEach { preset ->
                    PresetChip(
                        label = String.format(Locale.getDefault(), "₹%,.0f", preset),
                        isSelected = salary == preset.toInt().toString(),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSalaryChange(preset.toInt().toString())
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepBudgetQuestion(
    userName: String,
    salaryAmount: Double,
    budget: String,
    onBudgetChange: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(SagePastel),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "Budget",
                tint = SagePrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .clip(ZenPillShape)
                .background(SagePastel)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Question 3 of 4",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SagePrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "What's your monthly expense budget? 🎯",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ZenTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "This sets your spending boundary and powers your Daily Calm Allowance gauge.",
            fontSize = 14.sp,
            color = ZenTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        ZenCard(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = budget,
                onValueChange = onBudgetChange,
                label = { Text("Monthly Budget Limit (₹)") },
                placeholder = { Text("25000") },
                prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SageAccent,
                    unfocusedBorderColor = ZenCardBorder
                )
            )

            if (salaryAmount > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Recommended based on ₹%,.0f salary:".format(salaryAmount),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ZenTextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val p30 = (salaryAmount * 0.3).toInt()
                    val p50 = (salaryAmount * 0.5).toInt()
                    val p70 = (salaryAmount * 0.7).toInt()

                    listOf(
                        "30% (₹$p30)" to p30.toString(),
                        "50% (₹$p50)" to p50.toString(),
                        "70% (₹$p70)" to p70.toString()
                    ).forEach { (label, value) ->
                        PresetChip(
                            label = label,
                            isSelected = budget == value,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onBudgetChange(value)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepEmiQuestion(
    userName: String,
    hasEmi: Boolean,
    onHasEmiToggle: (Boolean) -> Unit,
    emiTitle: String,
    onEmiTitleChange: (String) -> Unit,
    emiAmount: String,
    onEmiAmountChange: (String) -> Unit,
    emiDueDay: String,
    onEmiDueDayChange: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(SagePastel),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CreditCard,
                contentDescription = "EMI",
                tint = SagePrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .clip(ZenPillShape)
                .background(SagePastel)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Question 4 of 4",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SagePrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Any monthly EMIs or loans? 💳",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ZenTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Track your monthly loan obligations with 1-tap payment logging.",
            fontSize = 14.sp,
            color = ZenTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle Option
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PresetChip(
                label = "Yes, I have an EMI",
                isSelected = hasEmi,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onHasEmiToggle(true)
                },
                modifier = Modifier.weight(1f)
            )

            PresetChip(
                label = "No active EMIs",
                isSelected = !hasEmi,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onHasEmiToggle(false)
                },
                modifier = Modifier.weight(1f)
            )
        }

        AnimatedVisibility(visible = hasEmi) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                ZenCard(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = emiTitle,
                        onValueChange = onEmiTitleChange,
                        label = { Text("EMI / Loan Name") },
                        placeholder = { Text("e.g. Car Loan, Home EMI, Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageAccent,
                            unfocusedBorderColor = ZenCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emiAmount,
                        onValueChange = onEmiAmountChange,
                        label = { Text("Monthly EMI Amount (₹)") },
                        placeholder = { Text("5000") },
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageAccent,
                            unfocusedBorderColor = ZenCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emiDueDay,
                        onValueChange = onEmiDueDayChange,
                        label = { Text("Monthly Due Date (Day of Month 1-31)") },
                        placeholder = { Text("5") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageAccent,
                            unfocusedBorderColor = ZenCardBorder
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(ZenPillShape)
            .background(if (isSelected) SagePrimary else PorcelainBg)
            .border(
                1.dp,
                if (isSelected) SagePrimary else ZenCardBorder,
                ZenPillShape
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else ZenTextPrimary,
            textAlign = TextAlign.Center
        )
    }
}
