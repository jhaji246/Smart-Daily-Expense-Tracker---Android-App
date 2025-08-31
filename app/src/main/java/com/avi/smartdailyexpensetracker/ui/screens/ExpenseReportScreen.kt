package com.avi.smartdailyexpensetracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avi.smartdailyexpensetracker.domain.entity.CategoryTotal
import com.avi.smartdailyexpensetracker.domain.entity.DailyTotal
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseReport
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseReportEvent
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseReportState
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseReportViewModel
import com.avi.smartdailyexpensetracker.ui.components.AnalyticsDashboard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseReportScreen(
    viewModel: ExpenseReportViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header with date range selector
        ReportHeader(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(16.dp)
        )
        
        // Enhanced Analytics Dashboard
        if (state.report != null) {
            AnalyticsDashboard(
                expenseReport = state.report,
                onCategoryClick = { categoryTotal ->
                    // TODO: Navigate to category details or filter expenses
                },
                onDailyTotalClick = { dailyTotal ->
                    // TODO: Navigate to daily details or filter expenses
                },
                modifier = Modifier.weight(1f)
            )
        } else if (state.isLoading) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyReportState()
            }
        }
        
        // Error message
        if (state.error != null) {
            ErrorMessage(
                error = state.error!!,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHeader(
    state: ExpenseReportState,
    onEvent: (ExpenseReportEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Expense Report",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Date range selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Start Date",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    
                    OutlinedButton(
                        onClick = {
                            // TODO: Implement date picker
                            val newDate = state.startDate.minusDays(1)
                            onEvent(ExpenseReportEvent.StartDateChanged(newDate))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                        )
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "End Date",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    
                    OutlinedButton(
                        onClick = {
                            // TODO: Implement date picker
                            val newDate = state.endDate.plusDays(1)
                            onEvent(ExpenseReportEvent.EndDateChanged(newDate))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { onEvent(ExpenseReportEvent.GenerateReport) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Generate Report"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Report")
            }
        }
    }
}

@Composable
fun ReportSummary(report: ExpenseReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Report Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "Total Amount",
                    value = "₹${String.format("%.2f", report.totalAmount)}",
                    icon = Icons.Default.Edit
                )
                
                SummaryItem(
                    label = "Total Expenses",
                    value = report.totalCount.toString(),
                    icon = Icons.Default.List
                )
                
                SummaryItem(
                    label = "Date Range",
                    value = "${report.startDate.dayOfMonth} - ${report.endDate.dayOfMonth}",
                    icon = Icons.Default.DateRange
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun DailyTotalsChart(dailyTotals: List<DailyTotal>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Daily Totals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simple chart representation
            Text(
                text = "Chart: ${dailyTotals.size} days of data",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Daily totals list
            Column {
                dailyTotals.forEach { dailyTotal ->
                    DailyTotalRow(dailyTotal = dailyTotal)
                }
            }
        }
    }
}

@Composable
fun DailyTotalRow(dailyTotal: DailyTotal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dailyTotal.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${dailyTotal.count} expenses",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "₹${String.format("%.2f", dailyTotal.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CategoryBreakdown(categoryTotals: List<CategoryTotal>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Category Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categoryTotals) { categoryTotal ->
                    CategoryTotalRow(categoryTotal = categoryTotal)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryTotalRow(categoryTotal: CategoryTotal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = categoryTotal.category.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Text(
                    text = "${categoryTotal.count} expenses",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₹${String.format("%.2f", categoryTotal.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Text(
                    text = "${String.format("%.1f", categoryTotal.percentage)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun ExportButton(onExport: () -> Unit) {
    Button(
        onClick = onExport,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Export Report"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Export Report (PDF/CSV)")
    }
}

@Composable
fun EmptyReportState() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "No Report",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No report data available",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Select a date range and generate a report",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorMessage(
    error: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}


