package com.avi.smartdailyexpensetracker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.ui.state.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun FilterChips(
    searchFilterState: SearchFilterState,
    onFilterChange: (SearchFilterState) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        // Filter toggle button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            IconButton(
                onClick = { showFilters = !showFilters }
            ) {
                Icon(
                    imageVector = if (showFilters) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (showFilters) "Hide filters" else "Show filters",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Filter options
        AnimatedVisibility(
            visible = showFilters,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category filters
                CategoryFilterChips(
                    selectedCategories = searchFilterState.selectedCategories,
                    onCategoryChange = { categories ->
                        onFilterChange(searchFilterState.copy(selectedCategories = categories))
                    }
                )
                
                // Date range filter
                DateRangeFilter(
                    dateRange = searchFilterState.dateRange,
                    onDateRangeChange = { dateRange ->
                        onFilterChange(searchFilterState.copy(dateRange = dateRange))
                    }
                )
                
                // Amount range filter
                AmountRangeFilter(
                    amountRange = searchFilterState.amountRange,
                    onAmountRangeChange = { amountRange ->
                        onFilterChange(searchFilterState.copy(amountRange = amountRange))
                    }
                )
                
                // Sort options
                SortOptionsFilter(
                    sortBy = searchFilterState.sortBy,
                    onSortChange = { sortBy ->
                        onFilterChange(searchFilterState.copy(sortBy = sortBy))
                    }
                )
                
                // Group options
                GroupOptionsFilter(
                    groupBy = searchFilterState.groupBy,
                    onGroupChange = { groupBy ->
                        onFilterChange(searchFilterState.copy(groupBy = groupBy))
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryFilterChips(
    selectedCategories: Set<ExpenseCategory>,
    onCategoryChange: (Set<ExpenseCategory>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ExpenseCategory.values()) { category ->
                val isSelected = category in selectedCategories
                
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newSelection = if (isSelected) {
                            selectedCategories - category
                        } else {
                            selectedCategories + category
                        }
                        onCategoryChange(newSelection)
                    },
                    label = {
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
fun DateRangeFilter(
    dateRange: DateRange,
    onDateRangeChange: (DateRange) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomDatePicker by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Text(
            text = "Date Range",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DateRangeType.values()) { dateRangeType ->
                val isSelected = dateRange.type == dateRangeType
                
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (dateRangeType == DateRangeType.CUSTOM) {
                            showCustomDatePicker = true
                        } else {
                            onDateRangeChange(DateRange(type = dateRangeType))
                        }
                    },
                    label = {
                        Text(
                            text = dateRangeType.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
        
        // Custom date range display
        if (dateRange.type == DateRangeType.CUSTOM && dateRange.startDate != null && dateRange.endDate != null) {
            Text(
                text = "${dateRange.startDate.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${dateRange.endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun AmountRangeFilter(
    amountRange: AmountRange,
    onAmountRangeChange: (AmountRange) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomAmountInput by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Text(
            text = "Amount Range",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AmountRangeType.values()) { amountRangeType ->
                val isSelected = amountRange.type == amountRangeType
                
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (amountRangeType == AmountRangeType.CUSTOM) {
                            showCustomAmountInput = true
                        } else {
                            onAmountRangeChange(AmountRange(type = amountRangeType))
                        }
                    },
                    label = {
                        Text(
                            text = amountRangeType.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
        
        // Custom amount range display
        if (amountRange.type == AmountRangeType.CUSTOM) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = amountRange.minAmount?.toString() ?: "",
                    onValueChange = { value ->
                        val minAmount = value.toDoubleOrNull()
                        onAmountRangeChange(amountRange.copy(minAmount = minAmount))
                    },
                    label = { Text("Min Amount") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
                
                OutlinedTextField(
                    value = amountRange.maxAmount?.toString() ?: "",
                    onValueChange = { value ->
                        val maxAmount = value.toDoubleOrNull()
                        onAmountRangeChange(amountRange.copy(maxAmount = maxAmount))
                    },
                    label = { Text("Max Amount") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
            }
        }
    }
}

@Composable
fun SortOptionsFilter(
    sortBy: SortOption,
    onSortChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Sort By",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SortOption.values()) { sortOption ->
                val isSelected = sortBy == sortOption
                
                FilterChip(
                    selected = isSelected,
                    onClick = { onSortChange(sortOption) },
                    label = {
                        Text(
                            text = sortOption.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
fun GroupOptionsFilter(
    groupBy: GroupOption,
    onGroupChange: (GroupOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Group By",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(GroupOption.values()) { groupOption ->
                val isSelected = groupBy == groupOption
                
                FilterChip(
                    selected = isSelected,
                    onClick = { onGroupChange(groupOption) },
                    label = {
                        Text(
                            text = groupOption.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}
