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
    background = Color(0xFFF1F8E9),
    surface = Color.White,
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
val DairyColor = Color(0xFFFDD835)
val VeganColor = Color(0xFF43A047)
