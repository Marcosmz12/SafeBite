package com.example.safebite.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

// ─── Esquema LIGHT ───────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary            = GreenPrimary,
    onPrimary          = OnPrimaryLight,
    primaryContainer   = GreenSoft,
    onPrimaryContainer = GreenPrimary,

    secondary          = AmberAccent,
    onSecondary        = OnSecondaryLight,
    secondaryContainer = Color(0xFFFFF8E1),
    onSecondaryContainer = Color(0xFF5C3D00),

    background         = BackgroundLight,
    onBackground       = OnSurfaceLight,

    surface            = SurfaceLight,
    onSurface          = OnSurfaceLight,
    surfaceVariant     = GreenSoft,
    onSurfaceVariant   = Color(0xFF444746),

    error              = ErrorLight,
    onError            = OnErrorLight,
)

// ─── Esquema DARK ────────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = GreenLight,
    onPrimary          = OnPrimaryDark,
    primaryContainer   = GreenPrimary,
    onPrimaryContainer = GreenSoft,

    secondary          = AmberAccent,
    onSecondary        = OnSecondaryDark,
    secondaryContainer = Color(0xFF4A3000),
    onSecondaryContainer = Color(0xFFFFDEA3),

    background         = BackgroundDark,
    onBackground       = OnSurfaceDark,

    surface            = SurfaceDark,
    onSurface          = OnSurfaceDark,
    surfaceVariant     = CardBackground,
    onSurfaceVariant   = Color(0xFFC4C7C5),

    error              = ErrorDark,
    onError            = OnErrorDark,
)

// ─── Composable principal ────────────────────────────────────────────────────
@Composable
fun SafeBiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Colores dinámicos disponibles desde Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    // Cambia el color de la status bar según el tema
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}
