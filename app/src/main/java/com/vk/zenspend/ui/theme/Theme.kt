package com.vk.zenspend.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalIsDarkTheme = staticCompositionLocalOf { false }

val ZenColorBackground: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkBackground else PorcelainBg

val ZenColorSurface: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkSurface else ZenWhite

val ZenColorSurfaceVariant: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkSurfaceVariant else PorcelainBgSubtle

val ZenColorTextPrimary: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkTextPrimary else LightTextPrimary

val ZenColorTextSecondary: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkTextSecondary else LightTextSecondary

val ZenColorTextMuted: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkTextMuted else LightTextMuted

// Direct typography color aliases dynamically bound to active theme
val ZenTextPrimary: Color
    @Composable get() = ZenColorTextPrimary

val ZenTextSecondary: Color
    @Composable get() = ZenColorTextSecondary

val ZenTextMuted: Color
    @Composable get() = ZenColorTextMuted

val ZenColorBorder: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkCardBorder else ZenCardBorder

val ZenColorSagePastel: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkSagePastel else SagePastel

val ZenColorSagePrimary: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkSagePrimary else SagePrimary

val ZenColorTerracottaPastel: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkTerracottaPastel else TerracottaPastel

val ZenColorApricotPastel: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkApricotPastel else ApricotPastel

private val ZenLightColorScheme = lightColorScheme(
    primary = SagePrimary,
    onPrimary = ZenWhite,
    primaryContainer = SagePastel,
    onPrimaryContainer = SagePastelText,

    secondary = SkyBluePastel,
    onSecondary = ZenWhite,
    secondaryContainer = SkyBluePastelBg,
    onSecondaryContainer = LightTextPrimary,

    tertiary = LavenderPastel,
    onTertiary = ZenWhite,
    tertiaryContainer = LavenderPastelBg,
    onTertiaryContainer = LightTextPrimary,

    background = PorcelainBg,
    onBackground = LightTextPrimary,

    surface = ZenWhite,
    onSurface = LightTextPrimary,
    surfaceVariant = PorcelainBgSubtle,
    onSurfaceVariant = LightTextSecondary,

    outline = ZenCardBorder,
    outlineVariant = ZenDivider,

    error = TerracottaAlert,
    onError = ZenWhite,
    errorContainer = TerracottaPastel,
    onErrorContainer = TerracottaPastelText
)

private val ZenDarkColorScheme = darkColorScheme(
    primary = DarkSagePrimary,
    onPrimary = Color.Black,
    primaryContainer = DarkSagePastel,
    onPrimaryContainer = DarkSagePastelText,

    secondary = SkyBluePastel,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF102A43),
    onSecondaryContainer = DarkTextPrimary,

    tertiary = LavenderPastel,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF2C1B4D),
    onTertiaryContainer = DarkTextPrimary,

    background = DarkBackground,
    onBackground = DarkTextPrimary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,

    outline = DarkCardBorder,
    outlineVariant = DarkDivider,

    error = TerracottaAlert,
    onError = Color.Black,
    errorContainer = DarkTerracottaPastel,
    onErrorContainer = Color(0xFFFFB4AB)
)

@Composable
fun ZenSpendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ZenDarkColorScheme else ZenLightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val navAndStatusColor = if (darkTheme) DarkBackground else PorcelainBg
                window.statusBarColor = navAndStatusColor.toArgb()
                window.navigationBarColor = navAndStatusColor.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ZenTypography,
            shapes = ZenShapes,
            content = content
        )
    }
}
