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
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

// ui/theme/Theme.kt
@Composable
fun SafeBiteTheme(
    // Si quieres forzar modo claro siempre, pon 'false' aquí
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Definimos los colores para ambos modos
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF4CAF50),
            background = Color(0xFF121212), // Fondo oscuro para modo noche
            surface = Color(0xFF1E1E1E),
            onBackground = Color.White,     // Texto sobre fondo oscuro
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF4CAF50),
            background = Color.White,       // Fondo blanco para modo claro
            surface = Color.White,
            onBackground = Color.Black,     // Texto sobre fondo blanco
            onSurface = Color.Black
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            // Esto hace que los iconos de la batería/hora sean negros si el fondo es claro
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}