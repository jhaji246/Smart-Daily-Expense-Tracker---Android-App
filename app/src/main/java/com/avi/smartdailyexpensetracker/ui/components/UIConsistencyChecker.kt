package com.avi.smartdailyexpensetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avi.smartdailyexpensetracker.ui.theme.ThemeConsistencyChecker

/**
 * UI Consistency Checker that provides standardized UI components
 * to ensure consistent design across all screens
 */
object UIConsistencyChecker {
    
    // Standardized Text components
    @Composable
    fun StandardText(
        text: String,
        modifier: Modifier = Modifier,
        style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
        color: Color = MaterialTheme.colorScheme.onSurface,
        fontWeight: FontWeight? = null
    ) {
        Text(
            text = text,
            modifier = modifier,
            style = style,
            color = color,
            fontWeight = fontWeight
        )
    }
    
    @Composable
    fun HeadingText(
        text: String,
        modifier: Modifier = Modifier,
        level: Int = 1
    ) {
        val style = when (level) {
            1 -> MaterialTheme.typography.headlineLarge
            2 -> MaterialTheme.typography.headlineMedium
            3 -> MaterialTheme.typography.headlineSmall
            4 -> MaterialTheme.typography.titleLarge
            5 -> MaterialTheme.typography.titleMedium
            else -> MaterialTheme.typography.titleSmall
        }
        
        StandardText(
            text = text,
            modifier = modifier,
            style = style,
            fontWeight = FontWeight.Bold
        )
    }
    
    @Composable
    fun BodyText(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        StandardText(
            text = text,
            modifier = modifier,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
    
    @Composable
    fun CaptionText(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        StandardText(
            text = text,
            modifier = modifier,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
    
    // Standardized Button components
    @Composable
    fun PrimaryButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        isLoading: Boolean = false
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .height(ThemeConsistencyChecker.ContentHeight.button)
                .fillMaxWidth(),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = text)
            }
        }
    }
    
    @Composable
    fun SecondaryButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .height(ThemeConsistencyChecker.ContentHeight.button)
                .fillMaxWidth(),
            enabled = enabled,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = text)
        }
    }
    
    @Composable
    fun TextButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
    ) {
        TextButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = text)
        }
    }
    
    // Standardized Card components
    @Composable
    fun StandardCard(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = ThemeConsistencyChecker.Elevation.sm
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Box(
                modifier = Modifier.padding(ThemeConsistencyChecker.Spacing.cardPadding)
            ) {
                content()
            }
        }
    }
    
    @Composable
    fun ElevatedCard(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = ThemeConsistencyChecker.Elevation.md
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Box(
                modifier = Modifier.padding(ThemeConsistencyChecker.Spacing.cardPadding)
            ) {
                content()
            }
        }
    }
    
    // Standardized Input components
    @Composable
    fun StandardTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        modifier: Modifier = Modifier,
        placeholder: String? = null,
        isError: Boolean = false,
        errorMessage: String? = null,
        singleLine: Boolean = true,
        maxLines: Int = 1
    ) {
        Column {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(text = label) },
                modifier = modifier
                    .fillMaxWidth()
                    .height(ThemeConsistencyChecker.ContentHeight.inputField),
                placeholder = placeholder?.let { { Text(text = it) } },
                isError = isError,
                singleLine = singleLine,
                maxLines = maxLines,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            
            if (isError && errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = ThemeConsistencyChecker.Spacing.sm)
                )
            }
        }
    }
    
    // Standardized Spacer components
    @Composable
    fun VerticalSpacer(size: androidx.compose.ui.unit.Dp = ThemeConsistencyChecker.Spacing.md) {
        Spacer(modifier = Modifier.height(size))
    }
    
    @Composable
    fun HorizontalSpacer(size: androidx.compose.ui.unit.Dp = ThemeConsistencyChecker.Spacing.md) {
        Spacer(modifier = Modifier.width(size))
    }
    
    // Standardized Divider
    @Composable
    fun StandardDivider(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.outlineVariant
    ) {
        Divider(
            modifier = modifier,
            color = color,
            thickness = 1.dp
        )
    }
    
    // Standardized Loading indicator
    @Composable
    fun StandardLoadingIndicator(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.primary
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = color,
                modifier = Modifier.size(48.dp)
            )
        }
    }
    
    // Standardized Error display
    @Composable
    fun StandardErrorDisplay(
        message: String,
        modifier: Modifier = Modifier,
        onRetry: (() -> Unit)? = null
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            
            VerticalSpacer(ThemeConsistencyChecker.Spacing.md)
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            if (onRetry != null) {
                VerticalSpacer(ThemeConsistencyChecker.Spacing.md)
                PrimaryButton(
                    text = "Retry",
                    onClick = onRetry,
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
    
    // Standardized Empty state
    @Composable
    fun StandardEmptyState(
        message: String,
        modifier: Modifier = Modifier,
        icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.List
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Empty",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(64.dp)
            )
            
            VerticalSpacer(ThemeConsistencyChecker.Spacing.md)
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
