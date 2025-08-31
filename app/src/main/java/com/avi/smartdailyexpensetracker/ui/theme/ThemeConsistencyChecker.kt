package com.avi.smartdailyexpensetracker.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * Theme consistency checker that provides standardized spacing, colors, and typography
 * to ensure consistent UI across all screens
 */
object ThemeConsistencyChecker {
    
    // Standardized spacing values
    object Spacing {
        val xs = 4.dp
        val sm = 8.dp
        val md = 16.dp
        val lg = 24.dp
        val xl = 32.dp
        val xxl = 48.dp
        
        // Screen padding
        val screenPadding = PaddingValues(
            start = md,
            end = md,
            top = md,
            bottom = md
        )
        
        // Card padding
        val cardPadding = PaddingValues(md)
        
        // List item padding
        val listItemPadding = PaddingValues(
            start = md,
            end = md,
            top = sm,
            bottom = sm
        )
        
        // Button padding
        val buttonPadding = PaddingValues(
            start = lg,
            end = lg,
            top = md,
            bottom = md
        )
        
        // Input field padding
        val inputFieldPadding = PaddingValues(
            start = md,
            end = md,
            top = sm,
            bottom = sm
        )
    }
    
    // Standardized corner radius
    object CornerRadius {
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 24.dp
        val round = 50.dp
    }
    
    // Standardized elevation
    object Elevation {
        val xs = 1.dp
        val sm = 2.dp
        val md = 4.dp
        val lg = 8.dp
        val xl = 16.dp
    }
    
    // Standardized animation durations
    object AnimationDuration {
        val fast = 150
        val normal = 300
        val slow = 500
        val verySlow = 800
    }
    
    // Standardized content heights
    object ContentHeight {
        val button = 48.dp
        val inputField = 56.dp
        val listItem = 72.dp
        val card = 120.dp
        val appBar = 64.dp
        val bottomBar = 80.dp
    }
    
    // Standardized content widths
    object ContentWidth {
        val minButton = 120.dp
        val minCard = 280.dp
        val maxContent = 600.dp
    }
    
    // Get theme-aware colors
    @Composable
    fun getThemeColors() = ThemeColors(
        primary = MaterialTheme.colorScheme.primary,
        onPrimary = MaterialTheme.colorScheme.onPrimary,
        secondary = MaterialTheme.colorScheme.secondary,
        onSecondary = MaterialTheme.colorScheme.onSecondary,
        tertiary = MaterialTheme.colorScheme.tertiary,
        onTertiary = MaterialTheme.colorScheme.onTertiary,
        background = MaterialTheme.colorScheme.background,
        onBackground = MaterialTheme.colorScheme.onBackground,
        surface = MaterialTheme.colorScheme.surface,
        onSurface = MaterialTheme.colorScheme.onSurface,
        surfaceVariant = MaterialTheme.colorScheme.surfaceVariant,
        onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant,
        outline = MaterialTheme.colorScheme.outline,
        outlineVariant = MaterialTheme.colorScheme.outlineVariant,
        error = MaterialTheme.colorScheme.error,
        onError = MaterialTheme.colorScheme.onError,
        errorContainer = MaterialTheme.colorScheme.errorContainer,
        onErrorContainer = MaterialTheme.colorScheme.onErrorContainer,
        inversePrimary = MaterialTheme.colorScheme.inversePrimary,
        scrim = MaterialTheme.colorScheme.scrim
    )
}

data class ThemeColors(
    val primary: androidx.compose.ui.graphics.Color,
    val onPrimary: androidx.compose.ui.graphics.Color,
    val secondary: androidx.compose.ui.graphics.Color,
    val onSecondary: androidx.compose.ui.graphics.Color,
    val tertiary: androidx.compose.ui.graphics.Color,
    val onTertiary: androidx.compose.ui.graphics.Color,
    val background: androidx.compose.ui.graphics.Color,
    val onBackground: androidx.compose.ui.graphics.Color,
    val surface: androidx.compose.ui.graphics.Color,
    val onSurface: androidx.compose.ui.graphics.Color,
    val surfaceVariant: androidx.compose.ui.graphics.Color,
    val onSurfaceVariant: androidx.compose.ui.graphics.Color,
    val outline: androidx.compose.ui.graphics.Color,
    val outlineVariant: androidx.compose.ui.graphics.Color,
    val error: androidx.compose.ui.graphics.Color,
    val onError: androidx.compose.ui.graphics.Color,
    val errorContainer: androidx.compose.ui.graphics.Color,
    val onErrorContainer: androidx.compose.ui.graphics.Color,
    val inversePrimary: androidx.compose.ui.graphics.Color,
    val scrim: androidx.compose.ui.graphics.Color
)
