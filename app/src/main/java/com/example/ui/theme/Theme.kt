package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DvsLightColorScheme = lightColorScheme(
    primary = DvsAccent,
    onPrimary = Color.White,
    primaryContainer = DvsAccentSoft,
    onPrimaryContainer = DvsAccentActive,
    secondary = DvsMutedText,
    onSecondary = Color.White,
    secondaryContainer = DvsSurfaceSubtle,
    onSecondaryContainer = DvsPrimaryText,
    tertiary = DvsAccentHover,
    onTertiary = Color.White,
    background = DvsBackground,
    onBackground = DvsPrimaryText,
    surface = DvsSurface,
    onSurface = DvsPrimaryText,
    surfaceVariant = DvsSurfaceSubtle,
    onSurfaceVariant = DvsMutedText,
    outline = DvsBorder,
    outlineVariant = DvsBorderStrong,
    error = DvsStatusErrorText,
    onError = Color.White,
    errorContainer = DvsStatusErrorBg,
    onErrorContainer = DvsStatusErrorText
)

private val DvsDarkColorScheme = darkColorScheme(
    primary = DvsAccentSoft,
    onPrimary = DvsAccentActive,
    primaryContainer = DvsAccentActive,
    onPrimaryContainer = DvsAccentSoft,
    secondary = DvsBorderStrong,
    onSecondary = DvsBackground,
    background = Color(0xFF141213),
    onBackground = Color(0xFFFAF7F8),
    surface = Color(0xFF1F1B1D),
    onSurface = Color(0xFFFAF7F8),
    surfaceVariant = Color(0xFF2B2528),
    onSurfaceVariant = Color(0xFFD6C8CC),
    outline = Color(0xFF473C40),
    outlineVariant = Color(0xFF5C4E53)
)

@Composable
fun DvsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DvsDarkColorScheme else DvsLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DvsTypography,
        content = content
    )
}
