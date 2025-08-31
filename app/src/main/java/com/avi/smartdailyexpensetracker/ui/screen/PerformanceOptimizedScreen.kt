package com.avi.smartdailyexpensetracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity
import com.avi.smartdailyexpensetracker.ui.viewmodel.PerformanceOptimizedExpenseViewModel
import com.avi.smartdailyexpensetracker.data.service.SimplePaginationInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceOptimizedScreen(
    viewModel: PerformanceOptimizedExpenseViewModel,
    onNavigateBack: () -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()
    val paginationInfo by viewModel.paginationInfo.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    val lazyListState = rememberLazyListState()
    
    // Auto-load next page when reaching end
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                if (lastVisibleItem != null && 
                    lastVisibleItem.index >= expenses.size - 3 && 
                    paginationInfo?.hasNextPage == true) {
                    viewModel.loadNextPage()
                }
            }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Optimized") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search and Filter Section
            SearchAndFilterSection(
                searchQuery = searchQuery,
                selectedCategory = selectedCategory,
                onSearchQueryChange = { viewModel.searchExpenses(it) },
                onCategorySelect = { viewModel.filterByCategory(it) },
                onClearFilters = { viewModel.clearFilters() }
            )
            
            // Pagination Info
            paginationInfo?.let { info ->
                PaginationInfoCard(info = info)
            }
            
            // Content
            when {
                isLoading && expenses.isEmpty() -> {
                    PerformanceLoadingState()
                }
                error != null && expenses.isEmpty() -> {
                    ErrorState(
                        error = error!!,
                        onRetry = { viewModel.loadExpenses() }
                    )
                }
                expenses.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    PerformanceOptimizedExpenseList(
                        expenses = expenses,
                        lazyListState = lazyListState,
                        onLoadNextPage = { viewModel.loadNextPage() },
                        onLoadPreviousPage = { viewModel.loadPreviousPage() }
                    )
                }
            }
            
            // Loading indicator for pagination
            if (isLoading && expenses.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SearchAndFilterSection(
    searchQuery: String,
    selectedCategory: String?,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Search & Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search expenses...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Category filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelect(null) },
                    label = { Text("All") }
                )
                
                FilterChip(
                    selected = selectedCategory == "Staff",
                    onClick = { onCategorySelect("Staff") },
                    label = { Text("Staff") }
                )
                
                FilterChip(
                    selected = selectedCategory == "Travel",
                    onClick = { onCategorySelect("Travel") },
                    label = { Text("Travel") }
                )
                
                FilterChip(
                    selected = selectedCategory == "Food",
                    onClick = { onCategorySelect("Food") },
                    label = { Text("Food") }
                )
                
                FilterChip(
                    selected = selectedCategory == "Utility",
                    onClick = { onCategorySelect("Utility") },
                    label = { Text("Utility") }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Clear filters button
            if (searchQuery.isNotBlank() || selectedCategory != null) {
                OutlinedButton(
                    onClick = onClearFilters,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear Filters")
                }
            }
        }
    }
}

@Composable
private fun PaginationInfoCard(info: SimplePaginationInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Page ${info.currentPage} of ${info.totalPages}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "${info.totalCount} total expenses",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PerformanceOptimizedExpenseList(
    expenses: List<ExpenseEntity>,
    lazyListState: androidx.compose.foundation.lazy.LazyListState,
    onLoadNextPage: () -> Unit,
    onLoadPreviousPage: () -> Unit
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Navigation buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onLoadPreviousPage,
                    enabled = expenses.isNotEmpty()
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Previous")
                }
                
                OutlinedButton(
                    onClick = onLoadNextPage,
                    enabled = expenses.isNotEmpty()
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
        
        // Expenses
        items(
            items = expenses,
            key = { it.id }
        ) { expense ->
            PerformanceOptimizedExpenseCard(expense = expense)
        }
    }
}

@Composable
private fun PerformanceOptimizedExpenseCard(expense: ExpenseEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "₹${expense.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text(expense.category) }
                )
                
                Text(
                    text = expense.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            expense.notes?.let { notes ->
                if (notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
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
private fun PerformanceLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading expenses...")
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.List,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No expenses found",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try adjusting your search or filters",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
