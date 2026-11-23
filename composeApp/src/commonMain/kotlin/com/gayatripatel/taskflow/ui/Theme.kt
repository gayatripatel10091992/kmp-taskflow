package com.gayatripatel.taskflow.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.gayatripatel.taskflow.shared.Priority

private val Light = lightColorScheme(
    primary = Color(0xFF4F46E5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E1B4B),
    secondaryContainer = Color(0xFFEDE9FE),
    background = Color(0xFFF8F9FC),
    surface = Color(0xFFF8F9FC),
    surfaceContainerLow = Color(0xFFF1F3F9),
    surfaceContainer = Color(0xFFECEFF6),
)

private val Dark = darkColorScheme(
    primary = Color(0xFFA5B4FC),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondaryContainer = Color(0xFF312E81),
    background = Color(0xFF0F1117),
    surface = Color(0xFF0F1117),
    surfaceContainerLow = Color(0xFF171A22),
    surfaceContainer = Color(0xFF1C1F28),
)

@Composable
fun TaskFlowTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) Dark else Light, content = content)
}

fun Priority.color(): Color = when (this) {
    Priority.HIGH -> Color(0xFFE5484D)
    Priority.MEDIUM -> Color(0xFFF5A524)
    Priority.LOW -> Color(0xFF30A46C)
}
