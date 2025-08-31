package com.avi.smartdailyexpensetracker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedExpenseEntry(
    title: String,
    amount: String,
    category: String,
    notes: String?,
    timestamp: String,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(500, easing = EaseOutBack),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .alpha(alpha),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with title and amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                AnimatedAmount(
                    amount = amount,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Category chip
            AnimatedCategoryChip(
                category = category,
                modifier = Modifier.align(Alignment.Start)
            )
            
            // Notes (if available)
            notes?.let { noteText ->
                if (noteText.isNotBlank()) {
                    AnimatedText(
                        text = noteText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Timestamp
            AnimatedText(
                text = timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun AnimatedText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "textAlpha"
    )
    
    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 20f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "textOffset"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
            .alpha(alpha)
            .offset(y = offsetY.dp)
    )
}

@Composable
fun AnimatedAmount(
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = tween(400, easing = EaseOutBack),
        label = "amountScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "amountAlpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Text(
        text = "₹$amount",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        color = color,
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
    )
}

@Composable
fun AnimatedCategoryChip(
    category: String,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(400, easing = EaseOutBack),
        label = "chipScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "chipAlpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Surface(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
