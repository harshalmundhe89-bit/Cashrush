package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = SlateDarkBg,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = AmberGold,
    onSecondary = SlateDarkBg,
    secondaryContainer = AmberContainer,
    onSecondaryContainer = OnAmberContainer,
    tertiary = CyanAccent,
    background = SlateDarkBg,
    onBackground = TextPrimary,
    surface = SlateCardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder,
    error = CrimsonAlert
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek dark mode for cash game aesthetics
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = SlateDarkBg.toArgb()
                window.navigationBarColor = SlateDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
