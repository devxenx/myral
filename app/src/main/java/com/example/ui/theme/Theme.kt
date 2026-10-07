package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = ElectricViolet,
    onPrimaryContainer = Color.White,
    secondary = NeonPink,
    onSecondary = Color.White,
    secondaryContainer = RadiantPurple,
    onSecondaryContainer = Color.White,
    tertiary = NeonBlue,
    onTertiary = Color.Black,
    background = SpaceDark,
    onBackground = Color.White,
    surface = SpaceCardBg,
    onSurface = Color.White,
    surfaceVariant = GlassSurface,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = GlassBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
