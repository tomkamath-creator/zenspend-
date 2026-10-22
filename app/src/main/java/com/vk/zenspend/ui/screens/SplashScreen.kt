package com.vk.zenspend.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.ui.components.ZenLogo
import com.vk.zenspend.ui.theme.DarkBackground
import com.vk.zenspend.ui.theme.DarkCardBorder
import com.vk.zenspend.ui.theme.DarkSagePrimary
import com.vk.zenspend.ui.theme.DarkSurface
import com.vk.zenspend.ui.theme.LocalIsDarkTheme
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.ZenColorSagePrimary
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(300))
        scale.animateTo(1f, animationSpec = ZenMotion.GentlePulse)
        // 1-second calming display duration
        delay(1000L)
        onTimeout()
    }

    val isDark = LocalIsDarkTheme.current
    val gradientColors = if (isDark) {
        listOf(
            Color(0xFF161920),
            DarkBackground,
            Color(0xFF0F172A)
        )
    } else {
        listOf(
            Color(0xFFFAFBFC),
            PorcelainBg,
            SagePastel.copy(alpha = 0.4f)
        )
    }

    val pillBg = if (isDark) DarkSurface else Color.White.copy(alpha = 0.95f)
    val pillBorder = if (isDark) DarkCardBorder else Color.Transparent
    val sageColor = if (isDark) DarkSagePrimary else SagePrimary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .padding(32.dp)
        ) {
            // App Logo
            ZenLogo(size = 115.dp)

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "ZenSpend",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = ZenTextPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Expense & EMI Calculator",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = sageColor
            )

            Spacer(modifier = Modifier.height(22.dp))

            Box(
                modifier = Modifier
                    .clip(ZenPillShape)
                    .background(pillBg)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "by VK",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = sageColor,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}
