package com.avi.smartdailyexpensetracker.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

data class ThemeState(
    val currentTheme: AppTheme = AppTheme.SYSTEM,
    val isDarkMode: Boolean = false
)

class ThemeManager {
    var themeState by mutableStateOf(ThemeState())
        private set
    
    fun toggleTheme() {
        themeState = when (themeState.currentTheme) {
            AppTheme.LIGHT -> ThemeState(AppTheme.DARK, true)
            AppTheme.DARK -> ThemeState(AppTheme.LIGHT, false)
            AppTheme.SYSTEM -> ThemeState(AppTheme.LIGHT, false)
        }
    }
    
    fun setTheme(theme: AppTheme) {
        themeState = when (theme) {
            AppTheme.LIGHT -> ThemeState(theme, false)
            AppTheme.DARK -> ThemeState(theme, true)
            AppTheme.SYSTEM -> ThemeState(theme, false) // Will be determined by system
        }
    }
    
    fun isDarkMode(): Boolean = themeState.isDarkMode
}

val LocalThemeManager = staticCompositionLocalOf { ThemeManager() }

@Composable
fun rememberThemeManager(): ThemeManager {
    return LocalThemeManager.current
}
