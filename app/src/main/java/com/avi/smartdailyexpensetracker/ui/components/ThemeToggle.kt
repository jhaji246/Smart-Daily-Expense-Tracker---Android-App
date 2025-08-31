package com.avi.smartdailyexpensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.avi.smartdailyexpensetracker.ui.theme.AppTheme
import com.avi.smartdailyexpensetracker.ui.theme.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeToggle(
    themeManager: ThemeManager,
    modifier: Modifier = Modifier
) {
    var showThemeOptions by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (showThemeOptions) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "rotation"
    )

    Box(modifier = modifier) {
        // Theme toggle button
        FloatingActionButton(
            onClick = { showThemeOptions = !showThemeOptions },
            modifier = Modifier
                .size(56.dp)
                .rotate(rotation),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Theme Settings"
            )
        }

        // Theme options dropdown
        AnimatedVisibility(
            visible = showThemeOptions,
            enter = slideInVertically(
                animationSpec = tween(300),
                initialOffsetY = { -it }
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                animationSpec = tween(300),
                targetOffsetY = { -it }
            ) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 70.dp)
        ) {
            ThemeOptionsCard(
                themeManager = themeManager,
                onThemeSelected = { 
                    themeManager.setTheme(it)
                    showThemeOptions = false
                }
            )
        }
    }
}

@Composable
private fun ThemeOptionsCard(
    themeManager: ThemeManager,
    onThemeSelected: (AppTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Choose Theme",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            ThemeOption(
                icon = Icons.Default.Settings,
                title = "Light",
                isSelected = themeManager.themeState.currentTheme == AppTheme.LIGHT,
                onClick = { onThemeSelected(AppTheme.LIGHT) }
            )
            
            ThemeOption(
                icon = Icons.Default.Settings,
                title = "Dark",
                isSelected = themeManager.themeState.currentTheme == AppTheme.DARK,
                onClick = { onThemeSelected(AppTheme.DARK) }
            )
            
            ThemeOption(
                icon = Icons.Default.Settings,
                title = "System",
                isSelected = themeManager.themeState.currentTheme == AppTheme.SYSTEM,
                onClick = { onThemeSelected(AppTheme.SYSTEM) }
            )
        }
    }
}

@Composable
private fun ThemeOption(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                   else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface
        )
        
        if (isSelected) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
