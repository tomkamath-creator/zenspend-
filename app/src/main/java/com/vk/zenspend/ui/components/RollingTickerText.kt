package com.vk.zenspend.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenTextPrimary
import java.util.Locale

/**
 * Rolling Digital Ticker (Odometer Animation):
 * Smoothly animates amount updates digit-by-digit
 * rolling up/down organically instead of abruptly snapping.
 */
@Composable
fun RollingTickerText(
    amount: Double,
    modifier: Modifier = Modifier,
    currencySymbol: String = "$",
    textStyle: TextStyle = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = ZenTextPrimary
    ),
    textColor: Color = ZenTextPrimary
) {
    val formattedString = String.format(Locale.US, "%s%.2f", currencySymbol, amount)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        formattedString.forEachIndexed { index, char ->
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically(animationSpec = ZenMotion.DefaultSpring) { height -> height } togetherWith
                                slideOutVertically(animationSpec = ZenMotion.DefaultSpring) { height -> -height }
                    } else {
                        slideInVertically(animationSpec = ZenMotion.DefaultSpring) { height -> -height } togetherWith
                                slideOutVertically(animationSpec = ZenMotion.DefaultSpring) { height -> height }
                    }
                },
                label = "odometer_digit_$index"
            ) { targetChar ->
                Text(
                    text = targetChar.toString(),
                    style = textStyle.copy(color = textColor)
                )
            }
        }
    }
}
