package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  lightColorScheme(
    primary = Color(0xFFFF4B2B), // Vibrant orange-red
    secondary = Color(0xFF00D1FF), // Vibrant cyan
    tertiary = Color(0xFFFFE600), // Vibrant yellow
    background = Color(0xFFFDFCF8), // Cream off-white
    surface = Color(0xFFFDFCF8),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFF121212),
    onSurface = Color(0xFF121212),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFFFF4B2B), // Vibrant orange-red
    secondary = Color(0xFF00D1FF), // Vibrant cyan
    tertiary = Color(0xFFFFE600), // Vibrant yellow
    background = Color(0xFFFDFCF8), // Cream off-white
    surface = Color(0xFFFDFCF8),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFF121212),
    onSurface = Color(0xFF121212),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is disabled to keep the custom brand intact
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
