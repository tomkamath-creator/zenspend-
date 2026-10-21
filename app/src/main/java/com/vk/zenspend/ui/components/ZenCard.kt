package com.vk.zenspend.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vk.zenspend.ui.theme.ZenCardBorder
import com.vk.zenspend.ui.theme.ZenCardShape
import com.vk.zenspend.ui.theme.ZenWhite

/**
 * Surface Card: Pure white with subtle layered ambient shadows
 * (elevation 2.dp to 6.dp) and soft 24dp rounded corners.
 */
@Composable
fun ZenCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 3.dp,
    containerColor: Color = ZenWhite,
    borderColor: Color = ZenCardBorder.copy(alpha = 0.5f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = ZenCardShape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}
