package com.avi.smartdailyexpensetracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseEntryEvent
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseEntryState
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    viewModel: ExpenseEntryViewModel,
    onExpenseAdded: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    
    // Call the callback when an expense is successfully added
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onExpenseAdded()
        }
    }
    
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            // Header with total spent today
            TotalSpentCard(totalSpent = state.totalSpentToday)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Form fields
            ExpenseEntryForm(
                state = state,
                onEvent = viewModel::onEvent
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Submit button
            SubmitButton(
                isLoading = state.isLoading,
                onSubmit = { viewModel.onEvent(ExpenseEntryEvent.SubmitExpense) }
            )
            
            // Error message
            if (state.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                ErrorMessage(error = state.error!!)
            }
            
            // Success message
            if (state.isSuccess) {
                Spacer(modifier = Modifier.height(16.dp))
                SuccessMessage()
            }
        }
    }
}

@Composable
fun TotalSpentCard(totalSpent: Double) {
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
                text = "Total Spent Today",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "₹${String.format("%.2f", totalSpent)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryForm(
    state: ExpenseEntryState,
    onEvent: (ExpenseEntryEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title field
        OutlinedTextField(
            value = state.title,
            onValueChange = { onEvent(ExpenseEntryEvent.TitleChanged(it)) },
            label = { Text("Expense Title") },
            placeholder = { Text("Enter expense title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Title"
                )
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Amount field
        OutlinedTextField(
            value = state.amount,
            onValueChange = { onEvent(ExpenseEntryEvent.AmountChanged(it)) },
            label = { Text("Amount (₹)") },
            placeholder = { Text("0.00") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Amount"
                )
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Category selection
        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExpenseCategory.values().forEach { category ->
                val isSelected = state.category == category
                FilterChip(
                    selected = isSelected,
                    onClick = { onEvent(ExpenseEntryEvent.CategoryChanged(category)) },
                    label = { Text(category.displayName) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Notes field
        OutlinedTextField(
            value = state.notes,
            onValueChange = { onEvent(ExpenseEntryEvent.NotesChanged(it)) },
            label = { Text("Notes (Optional)") },
            placeholder = { Text("Add any additional notes...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Notes"
                )
            },
            supportingText = {
                Text("${state.notes.length}/100 characters")
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Receipt image upload (mock)
        OutlinedButton(
            onClick = { 
                // TODO: Implement actual image picker
                onEvent(ExpenseEntryEvent.ReceiptImageChanged("mock_receipt_path"))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Upload Receipt"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Upload Receipt (Optional)")
        }
    }
}

@Composable
fun SubmitButton(
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    Button(
        onClick = onSubmit,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Submit"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add Expense",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ErrorMessage(error: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SuccessMessage() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Expense added successfully!",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
