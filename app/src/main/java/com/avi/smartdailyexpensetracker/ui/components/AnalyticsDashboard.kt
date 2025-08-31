package com.avi.smartdailyexpensetracker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avi.smartdailyexpensetracker.domain.entity.CategoryTotal
import com.avi.smartdailyexpensetracker.domain.entity.DailyTotal
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseReport
import java.time.format.DateTimeFormatter

@Composable
fun AnalyticsDashboard(
    expenseReport: ExpenseReport?,
    onCategoryClick: (CategoryTotal) -> Unit = {},
    onDailyTotalClick: (DailyTotal) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "dashboardAlpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    LazyColumn(
        modifier = modifier.alpha(alpha),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Cards
        item {
            SummaryCards(expenseReport = expenseReport)
        }
        
        // Daily Spending Trend
        item {
            if (expenseReport?.dailyTotals?.isNotEmpty() == true) {
                EnhancedBarChart(
                    dailyTotals = expenseReport.dailyTotals,
                    onBarClick = onDailyTotalClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Category Breakdown
        item {
            if (expenseReport?.categoryTotals?.isNotEmpty() == true) {
                EnhancedPieChart(
                    categoryTotals = expenseReport.categoryTotals,
                    onSliceClick = onCategoryClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Insights and Recommendations
        item {
            InsightsCard(expenseReport = expenseReport)
        }
        
        // Quick Actions
        item {
            QuickActionsCard()
        }
    }
}

@Composable
private fun SummaryCards(
    expenseReport: ExpenseReport?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Total Spent",
                value = "₹${expenseReport?.totalAmount?.toInt() ?: 0}",
                icon = Icons.Default.Info,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            
            SummaryCard(
                title = "Total Expenses",
                value = "${expenseReport?.totalCount ?: 0}",
                icon = Icons.Default.List,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Average/Day",
                value = "₹${expenseReport?.let { (it.totalAmount / it.totalCount).toInt() } ?: 0}",
                icon = Icons.Default.Info,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
            
            SummaryCard(
                title = "Period",
                value = expenseReport?.let { 
                    "${it.startDate.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${it.endDate.format(DateTimeFormatter.ofPattern("MMM dd"))}"
                } ?: "N/A",
                icon = Icons.Default.DateRange,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(400, easing = EaseOutBack),
        label = "cardScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "cardAlpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InsightsCard(
    expenseReport: ExpenseReport?,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = tween(500, easing = EaseOutBack),
        label = "insightsScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "insightsAlpha"
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
                text = "Insights & Recommendations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (expenseReport != null) {
                val insights = generateInsights(expenseReport)
                insights.forEach { insight ->
                    InsightItem(
                        icon = insight.icon,
                        title = insight.title,
                        description = insight.description,
                        color = insight.color
                    )
                }
            } else {
                Text(
                    text = "No data available for insights",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InsightItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionsCard(
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = tween(500, easing = EaseOutBack),
        label = "actionsScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "actionsAlpha"
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
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Add Expense",
                    onClick = { /* TODO: Navigate to add expense */ },
                    color = MaterialTheme.colorScheme.primary
                )
                
                QuickActionButton(
                    icon = Icons.Default.Share,
                    label = "Share Report",
                    onClick = { /* TODO: Share functionality */ },
                    color = MaterialTheme.colorScheme.secondary
                )
                
                QuickActionButton(
                    icon = Icons.Default.Info,
                    label = "Export",
                    onClick = { /* TODO: Export functionality */ },
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledTonalButton(
            onClick = onClick,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = color.copy(alpha = 0.1f),
                contentColor = color
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Data classes for insights
private data class Insight(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
private fun generateInsights(expenseReport: ExpenseReport): List<Insight> {
    val insights = mutableListOf<Insight>()
    
    // Top spending category
    val topCategory = expenseReport.categoryTotals.firstOrNull()
    if (topCategory != null) {
        insights.add(
            Insight(
                icon = Icons.Default.Info,
                title = "Top Spending Category",
                description = "${topCategory.category.displayName} accounts for ${String.format("%.1f", topCategory.percentage)}% of your total spending",
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
    
    // Spending trend
    val dailyAverages = expenseReport.dailyTotals.map { it.amount }
    val averageSpending = dailyAverages.average()
    val maxSpending = dailyAverages.maxOrNull() ?: 0.0
    
    if (maxSpending > averageSpending * 1.5) {
        insights.add(
            Insight(
                icon = Icons.Default.Warning,
                title = "High Spending Day Detected",
                description = "Your highest spending day was ₹${maxSpending.toInt()}, which is ${String.format("%.1f", (maxSpending / averageSpending - 1) * 100)}% above average",
                color = MaterialTheme.colorScheme.error
            )
        )
    }
    
    // Budget insights
    if (expenseReport.totalAmount > 10000) {
        insights.add(
            Insight(
                icon = Icons.Default.Warning,
                title = "Budget Alert",
                description = "Total spending of ₹${expenseReport.totalAmount.toInt()} exceeds typical monthly budget",
                color = MaterialTheme.colorScheme.error
            )
        )
    }
    
    return insights
}
