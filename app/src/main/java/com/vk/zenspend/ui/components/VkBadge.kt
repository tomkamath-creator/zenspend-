package com.vk.zenspend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.ui.theme.PorcelainBgSubtle
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.ZenCardBorder
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary

@Composable
fun VkBadge(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(ZenPillShape)
            .background(PorcelainBgSubtle)
            .border(1.dp, ZenCardBorder, ZenPillShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Serene pulsating green dot indicator
        Spacer(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SageAccent, SagePrimary)
                    )
                )
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = "ZenSpend",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = ZenTextPrimary,
            letterSpacing = 0.5.sp
        )
        Text(
            text = " • by VK",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = ZenTextSecondary
        )
    }
}
