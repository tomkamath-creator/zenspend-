package com.vk.zenspend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.vk.zenspend.ui.screens.InteractiveOnboardingScreen
import com.vk.zenspend.ui.screens.MainAppScaffold
import com.vk.zenspend.ui.screens.SplashScreen
import com.vk.zenspend.ui.screens.ZenSpendViewModel
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenSpendTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ZenSpendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ZenSpendTheme {
                val isSplashVisible by viewModel.isSplashVisible.collectAsState()
                val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PorcelainBg
                ) {
                    AnimatedContent(
                        targetState = isSplashVisible,
                        transitionSpec = {
                            fadeIn(animationSpec = ZenMotion.DefaultSpring) togetherWith
                                    fadeOut(animationSpec = ZenMotion.DefaultSpring)
                        },
                        label = "splashTransition"
                    ) { showSplash ->
                        if (showSplash) {
                            SplashScreen(
                                onTimeout = {
                                    viewModel.dismissSplash()
                                }
                            )
                        } else {
                            AnimatedContent(
                                targetState = hasCompletedOnboarding,
                                transitionSpec = {
                                    fadeIn(animationSpec = ZenMotion.DefaultSpring) togetherWith
                                            fadeOut(animationSpec = ZenMotion.DefaultSpring)
                                },
                                label = "onboardingTransition"
                            ) { isDone ->
                                if (isDone) {
                                    MainAppScaffold(viewModel = viewModel)
                                } else {
                                    InteractiveOnboardingScreen(
                                        onComplete = { name, salary, budget, emi ->
                                            viewModel.completeOnboarding(name, salary, budget, emi)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
