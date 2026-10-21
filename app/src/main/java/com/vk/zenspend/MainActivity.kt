package com.vk.zenspend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.vk.zenspend.ui.screens.ZenSpendRootScreen
import com.vk.zenspend.ui.screens.ZenSpendViewModel
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
                ZenSpendRootScreen(viewModel = viewModel)
            }
        }
    }
}
