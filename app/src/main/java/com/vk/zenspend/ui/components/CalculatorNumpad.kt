package com.vk.zenspend.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.ZenCardBorder
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenTextPrimary

@Composable
fun CalculatorNumpad(
    onDigitClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: 7, 8, 9, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NumpadKey("7", Modifier.weight(1f)) { onDigitClick("7") }
            NumpadKey("8", Modifier.weight(1f)) { onDigitClick("8") }
            NumpadKey("9", Modifier.weight(1f)) { onDigitClick("9") }
            NumpadKey("÷", Modifier.weight(1f), isOperator = true) { onOperatorClick("÷") }
        }

        // Row 2: 4, 5, 6, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NumpadKey("4", Modifier.weight(1f)) { onDigitClick("4") }
            NumpadKey("5", Modifier.weight(1f)) { onDigitClick("5") }
            NumpadKey("6", Modifier.weight(1f)) { onDigitClick("6") }
            NumpadKey("×", Modifier.weight(1f), isOperator = true) { onOperatorClick("×") }
        }

        // Row 3: 1, 2, 3, -
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NumpadKey("1", Modifier.weight(1f)) { onDigitClick("1") }
            NumpadKey("2", Modifier.weight(1f)) { onDigitClick("2") }
            NumpadKey("3", Modifier.weight(1f)) { onDigitClick("3") }
            NumpadKey("-", Modifier.weight(1f), isOperator = true) { onOperatorClick("-") }
        }

        // Row 4: C, 0, ., +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NumpadKey("C", Modifier.weight(1f), isClear = true) { onClearClick() }
            NumpadKey("0", Modifier.weight(1f)) { onDigitClick("0") }
            NumpadKey(".", Modifier.weight(1f)) { onDigitClick(".") }
            NumpadKey("+", Modifier.weight(1f), isOperator = true) { onOperatorClick("+") }
        }

        // Row 5: Backspace pill key
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            BackspaceKey(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                onBackspaceClick()
            }
        }
    }
}

@Composable
private fun NumpadKey(
    text: String,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false,
    isClear: Boolean = false,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = ZenMotion.DefaultSpring,
        label = "keyScale"
    )

    val bgColor = when {
        isClear -> Color(0xFFFEECEB)
        isOperator -> SagePastel
        else -> Color.White
    }

    val textColor = when {
        isClear -> Color(0xFFC53030)
        isOperator -> SagePrimary
        else -> ZenTextPrimary
    }

    Box(
        modifier = modifier
            .height(54.dp)
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .border(1.dp, ZenCardBorder.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = if (isOperator || isClear) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun BackspaceKey(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = ZenMotion.DefaultSpring,
        label = "backspaceScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(PorcelainBg)
            .border(1.dp, ZenCardBorder, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Delete",
                tint = ZenTextPrimary.copy(alpha = 0.7f),
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = "Delete",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ZenTextPrimary.copy(alpha = 0.7f)
            )
        }
    }
}
