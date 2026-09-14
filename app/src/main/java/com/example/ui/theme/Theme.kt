package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp)
)

private val BlackAndWhiteColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    primaryContainer = Gray800,
    onPrimaryContainer = White,
    secondary = Gray400,
    onSecondary = Black,
    tertiary = White,
    onTertiary = Black,
    background = Black,
    onBackground = White,
    surface = Gray900,
    onSurface = White,
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray300,
    outline = Gray400,
    error = Color(0xFFCF6679),
    onError = Black
)

private val FallbackDynamicDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    error = Color(0xFFCF6679),
    onError = Black
)

@Composable
fun MyApplicationTheme(
    customPrimaryColor: Color = White, 
    darkTheme: Boolean = true, 
    dynamicColor: Boolean = true, 
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isBw = customPrimaryColor == White
    
    val colorScheme = when {
        dynamicColor -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                FallbackDynamicDarkColorScheme
            }
        }
        isBw -> {
            BlackAndWhiteColorScheme
        }
        else -> {
            // Blend a dynamic theme using the extracted wallpaper primary color
            darkColorScheme(
                primary = customPrimaryColor,
                onPrimary = Black,
                primaryContainer = customPrimaryColor.copy(alpha = 0.3f),
                onPrimaryContainer = customPrimaryColor,
                secondary = customPrimaryColor.copy(alpha = 0.7f),
                onSecondary = Black,
                tertiary = customPrimaryColor,
                onTertiary = Black,
                background = Color.Black.copy(alpha = 0.75f), // Dimmed background to show wallpaper
                onBackground = White,
                surface = Color.Black.copy(alpha = 0.6f), // Glassmorphism for app bars
                onSurface = White,
                surfaceVariant = customPrimaryColor.copy(alpha = 0.15f), // Tinted bubble color
                onSurfaceVariant = White.copy(alpha = 0.9f),
                outline = customPrimaryColor.copy(alpha = 0.5f),
                error = Color(0xFFCF6679),
                onError = Black
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
