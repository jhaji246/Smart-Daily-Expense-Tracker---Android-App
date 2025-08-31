package com.avi.smartdailyexpensetracker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avi.smartdailyexpensetracker.domain.entity.CategoryTotal
import com.avi.smartdailyexpensetracker.domain.entity.DailyTotal

@Composable
fun EnhancedBarChart(
    dailyTotals: List<DailyTotal>,
    onBarClick: (DailyTotal) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(600, easing = EaseOutBack),
        label = "chartScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "chartAlpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(alpha),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Daily Spending Trend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (dailyTotals.isNotEmpty()) {
                // Simplified chart representation
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dailyTotals.forEachIndexed { index, dailyTotal ->
                        val maxAmount = dailyTotals.maxOfOrNull { it.amount } ?: 1.0
                        val barHeight = ((dailyTotal.amount / maxAmount) * 100).toInt()
                        
                        DailyBarItem(
                            date = dailyTotal.date,
                            amount = dailyTotal.amount,
                            barHeight = barHeight,
                            onClick = { onBarClick(dailyTotal) }
                        )
                    }
                }
            } else {
                EmptyChartState(
                    message = "No spending data available",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }
        }
    }
}

@Composable
private fun DailyBarItem(
    date: java.time.LocalDate,
    amount: Double,
    barHeight: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd")),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(60.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width((barHeight * 0.01f * 200).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        
        Text(
            text = "₹${amount.toInt()}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EnhancedPieChart(
    categoryTotals: List<CategoryTotal>,
    onSliceClick: (CategoryTotal) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(600, easing = EaseOutBack),
        label = "pieScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "pieAlpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(alpha),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Category Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (categoryTotals.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Simplified pie chart representation
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chart: ${categoryTotals.size} categories",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Enhanced legend
                    EnhancedChartLegend(
                        categoryTotals = categoryTotals,
                        onCategoryClick = onSliceClick,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    )
                }
            } else {
                EmptyChartState(
                    message = "No category data available",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}

@Composable
private fun EnhancedChartLegend(
    categoryTotals: List<CategoryTotal>,
    onCategoryClick: (CategoryTotal) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categoryTotals.forEachIndexed { index, categoryTotal ->
            var isVisible by remember { mutableStateOf(false) }
            val alpha by animateFloatAsState(
                targetValue = if (isVisible) 1f else 0f,
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = index * 100,
                    easing = FastOutSlowInEasing
                ),
                label = "legendAlpha$index"
            )
            
            LaunchedEffect(Unit) {
                isVisible = true
            }
            
            Row(
                modifier = Modifier
                    .alpha(alpha)
                    .clickable { onCategoryClick(categoryTotal) }
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (index % 2 == 0) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        else Color.Transparent
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(getCategoryColor(categoryTotal.category.name))
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = categoryTotal.category.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "₹${categoryTotal.amount.toInt()} (${String.format("%.1f", categoryTotal.percentage)}%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyChartState(
    message: String,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = tween(600, easing = EaseOutBack),
        label = "emptyChartScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "emptyChartAlpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getCategoryColor(categoryName: String): Color {
    return when (categoryName) {
        "STAFF" -> Color(0xFF1976D2)
        "TRAVEL" -> Color(0xFF388E3C)
        "FOOD" -> Color(0xFFFF9800)
        "UTILITY" -> Color(0xFF9C27B0)
        else -> Color(0xFF757575)
    }
}
