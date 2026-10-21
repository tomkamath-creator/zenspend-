package com.vk.zenspend.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ZenLightColorScheme = lightColorScheme(
    primary = SagePrimary,
    onPrimary = ZenWhite,
    primaryContainer = SagePastel,
    onPrimaryContainer = SagePastelText,

    secondary = SkyBluePastel,
    onSecondary = ZenWhite,
    secondaryContainer = SkyBluePastelBg,
    onSecondaryContainer = ZenTextPrimary,

    tertiary = LavenderPastel,
    onTertiary = ZenWhite,
    tertiaryContainer = LavenderPastelBg,
    onTertiaryContainer = ZenTextPrimary,

    background = PorcelainBg,
    onBackground = ZenTextPrimary,

    surface = ZenWhite,
    onSurface = ZenTextPrimary,
    surfaceVariant = PorcelainBgSubtle,
    onSurfaceVariant = ZenTextSecondary,

    outline = ZenCardBorder,
    outlineVariant = ZenDivider,

    error = TerracottaAlert,
    onError = ZenWhite,
    errorContainer = TerracottaPastel,
    onErrorContainer = TerracottaPastelText
)

@Composable
fun ZenSpendTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = ZenLightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = PorcelainBg.toArgb()
                window.navigationBarColor = PorcelainBg.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZenTypography,
        shapes = ZenShapes,
        content = content
    )
}
