package com.avi.smartdailyexpensetracker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.avi.smartdailyexpensetracker.domain.entity.Expense

@Composable
fun AnimatedExpenseList(
    expenses: List<Expense>,
    onExpenseClick: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(
            items = expenses,
            key = { _, expense -> expense.id }
        ) { index, expense ->
            AnimatedExpenseListItem(
                expense = expense,
                onExpenseClick = onExpenseClick,
                animationDelay = index * 100L
            )
        }
    }
}

@Composable
fun AnimatedExpenseListItem(
    expense: Expense,
    onExpenseClick: (Expense) -> Unit,
    animationDelay: Long = 0L,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = animationDelay.toInt(),
            easing = EaseOutBack
        ),
        label = "itemScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = animationDelay.toInt(),
            easing = FastOutSlowInEasing
        ),
        label = "itemAlpha"
    )
    
    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 50f,
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = animationDelay.toInt(),
            easing = FastOutSlowInEasing
        ),
        label = "itemOffset"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(alpha)
            .offset(y = offsetY.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onExpenseClick(expense) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Expense details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AnimatedText(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                AnimatedText(
                    text = expense.category.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                expense.notes?.let { notes ->
                    if (notes.isNotBlank()) {
                        AnimatedText(
                            text = notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Amount
            AnimatedAmount(
                amount = expense.amount.toString(),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AnimatedExpenseCard(
    expense: Expense,
    onExpenseClick: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = tween(500, easing = EaseOutBack),
        label = "cardScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "cardAlpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(alpha),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onExpenseClick(expense) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedText(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                AnimatedAmount(
                    amount = expense.amount.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Category and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedCategoryChip(
                    category = expense.category.displayName,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                AnimatedText(
                    text = expense.timestamp.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Notes (if available)
            expense.notes?.let { notes ->
                if (notes.isNotBlank()) {
                    AnimatedText(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedEmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = tween(600, easing = EaseOutBack),
        label = "emptyScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "emptyAlpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(alpha),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
