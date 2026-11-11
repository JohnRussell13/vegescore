package com.vegescore.app.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GreenPrimary = Color(0xFF2E7D32)
private val GreenContainer = Color(0xFFA5D6A7)
private val YellowContainer = Color(0xFFFFF9C4)
private val RedContainer = Color(0xFFFFCDD2)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenContainer,
    secondary = Color(0xFF388E3C),
    background = Color.White,
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceContainer = Color.White,
    surfaceContainerLow = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = GreenPrimary,
)

@Composable
fun VegeScoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}

val MeatColor = Color(0xFFE53935)
val DairyColor = Color(0xFFF9A825)
val DividerColor = Color(0xFFF0F0F0)
val VeganColor = Color(0xFF43A047)
