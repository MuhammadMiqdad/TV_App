package com.example.tvapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MarqueeGold,
    onPrimary = MarqueeGoldOnDark,
    secondary = Velvet,
    onSecondary = Paper,
    tertiary = MarqueeGold,
    onTertiary = MarqueeGoldOnDark,
    background = Ink,
    onBackground = Paper,
    surface = InkSurface,
    onSurface = Paper,
    surfaceVariant = InkSurfaceVariant,
    onSurfaceVariant = Muted
)

private val LightColorScheme = lightColorScheme(
    primary = Velvet,
    onPrimary = Cream,
    secondary = MarqueeGold,
    onSecondary = InkText,
    tertiary = MarqueeGold,
    onTertiary = InkText,
    background = Cream,
    onBackground = InkText,
    surface = CreamSurface,
    onSurface = InkText,
    surfaceVariant = Cream,
    onSurfaceVariant = MutedOnLight
)

@Composable
fun TVAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}