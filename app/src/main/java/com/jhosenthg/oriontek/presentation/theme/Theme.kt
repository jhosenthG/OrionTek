package com.jhosenthg.oriontek.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue500,
    onPrimary = Slate50,
    secondary = Slate700,
    onSecondary = Slate50,
    tertiary = Slate200,
    onTertiary = Navy900,
    background = Navy900,
    onBackground = Slate50,
    surface = Slate800,
    onSurface = Slate50,
    outline = Slate500
)

private val LightColorScheme = lightColorScheme(
    primary = Navy900,
    onPrimary = Slate50,
    secondary = Slate700,
    onSecondary = Slate50,
    tertiary = Blue500,
    onTertiary = Navy900,
    background = Slate50,
    onBackground = Navy900,
    surface = Slate50,
    onSurface = Navy900,
    outline = Slate500
)

@Composable
fun OrionTekTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep dynamic color opt-in so the app preserves the brand palette by default.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}