@file:Suppress("DEPRECATION")

package com.example.safebite.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),      // Un verde vibrante pero equilibrado
    onPrimary = Color.White,          // <--- ESTO arregla el texto morado (ahora será blanco)

    background = Color(0xFF121212),   // Negro suave
    onBackground = Color.White,       // Texto general en blanco

    surface = Color(0xFF1E1E1E),      // Gris oscuro para tarjetas/inputs
    onSurface = Color.White,

    outline = Color(0xFF4CAF50),      // Para los bordes de los botones "Outlined"
    secondary = Color(0xFF81C784)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF5F5F5),
    onSurface = Color.Black,
    outline = Color(0xFF4CAF50)
)

@Composable
fun SafeBiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            // Esto ajusta los iconos de la batería/hora según el fondo
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de tener esto o quítalo si te da error
        content = content
    )
}