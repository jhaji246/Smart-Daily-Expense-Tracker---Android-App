package com.avi.smartdailyexpensetracker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ExportOptionsDialog(
    onDismiss: () -> Unit,
    onExportPdf: () -> Unit,
    onExportCsv: () -> Unit,
    onSharePdf: () -> Unit,
    onShareCsv: () -> Unit,
    onShareViaEmail: () -> Unit,
    onShareViaWhatsApp: () -> Unit,
    onShareViaTelegram: () -> Unit,
    canShareViaWhatsApp: Boolean = false,
    canShareViaTelegram: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(300, easing = EaseOutBack),
        label = "dialogScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "dialogAlpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .scale(scale)
                .alpha(alpha),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Export & Share",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Export Options
                ExportSection(
                    title = "Export Options",
                    icon = Icons.Default.Info,
                    items = listOf(
                        ExportOption(
                            title = "Export as PDF",
                            subtitle = "Professional PDF report",
                            icon = Icons.Default.Info,
                            onClick = onExportPdf
                        ),
                        ExportOption(
                            title = "Export as CSV",
                            subtitle = "Spreadsheet format",
                            icon = Icons.Default.List,
                            onClick = onExportCsv
                        )
                    )
                )
                
                // Share Options
                ShareSection(
                    title = "Share Options",
                    icon = Icons.Default.Share,
                    items = listOf(
                        ShareOption(
                            title = "Share PDF",
                            subtitle = "Share via any app",
                            icon = Icons.Default.Info,
                            onClick = onSharePdf
                        ),
                        ShareOption(
                            title = "Share CSV",
                            subtitle = "Share via any app",
                            icon = Icons.Default.List,
                            onClick = onShareCsv
                        ),
                        ShareOption(
                            title = "Share via Email",
                            subtitle = "Send as attachment",
                            icon = Icons.Default.Email,
                            onClick = onShareViaEmail
                        ),
                        ShareOption(
                            title = "Share via WhatsApp",
                            subtitle = "Send to contacts",
                            icon = Icons.Default.Info,
                            onClick = onShareViaWhatsApp,
                            enabled = canShareViaWhatsApp
                        ),
                        ShareOption(
                            title = "Share via Telegram",
                            subtitle = "Send to contacts",
                            icon = Icons.Default.Send,
                            onClick = onShareViaTelegram,
                            enabled = canShareViaTelegram
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun ExportSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<ExportOption>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        items.forEach { option ->
            ExportOptionItem(option = option)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ShareSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<ShareOption>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        items.forEach { option ->
            ShareOptionItem(option = option)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ExportOptionItem(
    option: ExportOption,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { option.onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = option.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ShareOptionItem(
    option: ShareOption,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = option.enabled) { option.onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (option.enabled) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (option.enabled) MaterialTheme.colorScheme.secondary 
                       else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (option.enabled) MaterialTheme.colorScheme.onSurface
                           else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                
                Text(
                    text = option.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (option.enabled) MaterialTheme.colorScheme.onSurfaceVariant
                           else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            
            if (option.enabled) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class ExportOption(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)

data class ShareOption(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit,
    val enabled: Boolean = true
)
