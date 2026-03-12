package com.luisnavarro.fevertest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = WeatherBlueDark,
    onPrimary = DarkBackground,
    background = DarkBackground,
    onBackground = LightSurface,
    surface = DarkSurface,
    onSurface = LightSurface,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkOutline,
)

private val LightColorScheme = lightColorScheme(
    primary = WeatherBlue,
    onPrimary = LightSurface,
    background = LightBackground,
    onBackground = Color(0xFF101828),
    surface = LightSurface,
    onSurface = Color(0xFF101828),
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightTextSecondary,
    outline = LightOutline,
)

@Composable
fun FeverTestTheme(
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
