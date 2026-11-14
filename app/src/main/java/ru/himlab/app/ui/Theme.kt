package ru.himlab.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Palette {
    val Purple = Color(0xFF6C4DFF)
    val PurpleDark = Color(0xFF4630C9)
    val Sky = Color(0xFFE8F4FF)
    val Mint = Color(0xFF2EC4B6)
    val Sun = Color(0xFFFFC53D)
    val Coral = Color(0xFFFF6B6B)
    val Green = Color(0xFF3DBE6B)
    val Orange = Color(0xFFFF8A3D)
    val Ink = Color(0xFF1F2340)
    val Soft = Color(0xFF5B607F)
}

private val Scheme = lightColorScheme(
    primary = Palette.Purple,
    onPrimary = Color.White,
    secondary = Palette.Mint,
    tertiary = Palette.Sun,
    background = Palette.Sky,
    surface = Color.White,
    onBackground = Palette.Ink,
    onSurface = Palette.Ink,
)

@Composable
fun HimLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, content = content)
}
