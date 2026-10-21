package com.vk.zenspend.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary
import kotlinx.coroutines.delay

/**
 * ZenSpend Launch Screen • by VK
 * Shows the peaceful Zen logo, app name, and "made by vk" for ~1 second
 * with gentle spring physics before transitioning to the Dashboard.
 */
@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Smooth entrance
        alpha.animateTo(1f, animationSpec = tween(350))
        scale.animateTo(1f, animationSpec = ZenMotion.GentlePulse)
        
        // Exact 1-second display duration requested
        delay(1000L)
        
        // Notify parent to reveal Dashboard
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAFBFC),
                        PorcelainBg,
                        SagePastel.copy(alpha = 0.35f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .padding(32.dp)
        ) {
            // Balanced Zen Logo with aura
            ZenLogo(size = 110.dp)

            Spacer(modifier = Modifier.height(28.dp))

            // App Name
            Text(
                text = "ZenSpend",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = ZenTextPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Gentle Tagline
            Text(
                text = "Mindful, effortless finances",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = ZenTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Made by VK Signature Pill
            Box(
                modifier = Modifier
                    .clip(ZenPillShape)
                    .background(Color.White.copy(alpha = 0.85f))
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "made by vk",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SagePrimary,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}
