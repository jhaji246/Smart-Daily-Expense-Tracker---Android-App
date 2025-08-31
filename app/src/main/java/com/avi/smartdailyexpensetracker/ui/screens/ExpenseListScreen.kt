package com.avi.smartdailyexpensetracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.ui.components.ExpenseCard
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseListEvent
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseListState
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseListViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    viewModel: ExpenseListViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Header with filters and totals
        ExpenseListHeader(
            state = state,
            onEvent = viewModel::onEvent
        )
        
        // Content
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.expenses.isEmpty()) {
            EmptyState()
        } else {
            ExpenseListContent(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListHeader(
    state: ExpenseListState,
    onEvent: (ExpenseListEvent) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Date selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Expenses for ${state.selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = {
                        // TODO: Implement date picker
                        val newDate = if (state.selectedDate == LocalDate.now()) {
                            state.selectedDate.minusDays(1)
                        } else {
                            LocalDate.now()
                        }
                        onEvent(ExpenseListEvent.DateSelected(newDate))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Date"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { query -> onEvent(ExpenseListEvent.SearchQueryChanged(query)) },
                placeholder = { Text("Search expenses by title, notes, or category...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = { onEvent(ExpenseListEvent.SearchQueryChanged("")) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Totals row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TotalItem(
                    label = "Total Amount",
                    value = "₹${String.format("%.2f", state.totalAmount)}",
                    icon = Icons.Default.Edit
                )
                
                TotalItem(
                    label = "Total Count",
                    value = state.totalCount.toString(),
                    icon = Icons.Default.List
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Category filter
            Text(
                text = "Filter by Category",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.selectedCategory == null,
                    onClick = { onEvent(ExpenseListEvent.CategoryFilterChanged(null)) },
                    label = { Text("All") }
                )
                
                ExpenseCategory.values().forEach { category ->
                    FilterChip(
                        selected = state.selectedCategory == category,
                        onClick = { onEvent(ExpenseListEvent.CategoryFilterChanged(category)) },
                        label = { Text(category.displayName) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Grouping toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Group by Category",
                    style = MaterialTheme.typography.titleSmall
                )
                
                Switch(
                    checked = state.groupByCategory,
                    onCheckedChange = { onEvent(ExpenseListEvent.ToggleGrouping) }
                )
            }
        }
    }
}

@Composable
fun TotalItem(
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
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ExpenseListContent(
    state: ExpenseListState,
    onEvent: (ExpenseListEvent) -> Unit
) {
    if (state.groupByCategory) {
        GroupedExpenseList(state = state, onEvent = onEvent)
    } else {
        SimpleExpenseList(expenses = state.expenses)
    }
}

@Composable
fun GroupedExpenseList(
    state: ExpenseListState,
    onEvent: (ExpenseListEvent) -> Unit
) {
    // For now, we'll group expenses manually since we don't have access to viewModel methods
    val groupedExpenses = state.expenses.groupBy { it.category }
    
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        groupedExpenses.forEach { (category, expenses) ->
            item {
                CategoryHeader(category = category, expenseCount = expenses.size)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            items(expenses) { expense ->
                ExpenseCard(
                    expense = expense,
                    onEdit = { /* TODO: Implement edit */ },
                    onDelete = { /* TODO: Implement delete */ }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SimpleExpenseList(expenses: List<Expense>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(expenses) { expense ->
            ExpenseCard(
                expense = expense,
                onEdit = { /* TODO: Implement edit */ },
                onDelete = { /* TODO: Implement delete */ }
            )
        }
    }
}

@Composable
fun CategoryHeader(
    category: ExpenseCategory,
    expenseCount: Int
) {
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
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Text(
                text = "$expenseCount expenses",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "No Expenses",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No expenses found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Add your first expense to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
