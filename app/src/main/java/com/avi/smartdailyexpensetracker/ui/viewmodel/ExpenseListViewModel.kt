package com.avi.smartdailyexpensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.domain.usecase.GetExpensesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ExpenseListState(
    val expenses: List<Expense> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedCategory: ExpenseCategory? = null,
    val groupByCategory: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalAmount: Double = 0.0,
    val totalCount: Int = 0,
    val searchQuery: String = ""
)

sealed class ExpenseListEvent {
    data class DateSelected(val date: LocalDate) : ExpenseListEvent()
    data class CategoryFilterChanged(val category: ExpenseCategory?) : ExpenseListEvent()
    data class SearchQueryChanged(val query: String) : ExpenseListEvent()
    object ToggleGrouping : ExpenseListEvent()
    object RefreshExpenses : ExpenseListEvent()
}

class ExpenseListViewModel(
    private val getExpensesUseCase: GetExpensesUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ExpenseListState())
    val state: StateFlow<ExpenseListState> = _state.asStateFlow()
    
    init {
        loadExpenses()
    }
    
    fun onEvent(event: ExpenseListEvent) {
        when (event) {
            is ExpenseListEvent.DateSelected -> {
                _state.value = _state.value.copy(selectedDate = event.date)
                loadExpenses()
            }
            is ExpenseListEvent.CategoryFilterChanged -> {
                _state.value = _state.value.copy(selectedCategory = event.category)
                loadExpenses()
            }
            is ExpenseListEvent.SearchQueryChanged -> {
                _state.value = _state.value.copy(searchQuery = event.query)
                loadExpenses()
            }
            is ExpenseListEvent.ToggleGrouping -> {
                _state.value = _state.value.copy(
                    groupByCategory = !_state.value.groupByCategory
                )
            }
            is ExpenseListEvent.RefreshExpenses -> {
                loadExpenses()
            }
        }
    }
    
    // Public method to refresh expenses from external sources
    fun refreshExpenses() {
        loadExpenses()
    }
    
    private fun loadExpenses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val currentState = _state.value
                
                // Get expenses for selected date
                val allExpenses = getExpensesUseCase(currentState.selectedDate)
                var expenses = if (currentState.selectedCategory != null) {
                    allExpenses.getOrNull()?.filter { it.category == currentState.selectedCategory } ?: emptyList()
                } else {
                    allExpenses.getOrNull() ?: emptyList()
                }
                
                // Apply search filter if query is not empty
                if (currentState.searchQuery.isNotBlank()) {
                    val searchQuery = currentState.searchQuery.lowercase()
                    expenses = expenses.filter { expense ->
                        expense.title.lowercase().contains(searchQuery) ||
                        expense.notes?.lowercase()?.contains(searchQuery) == true ||
                        expense.category.displayName.lowercase().contains(searchQuery)
                    }
                }
                
                val totalAmount = expenses.sumOf { it.amount }
                val totalCount = expenses.size
                
                _state.value = currentState.copy(
                    expenses = expenses,
                    totalAmount = totalAmount,
                    totalCount = totalCount,
                    isLoading = false
                )
                
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load expenses: ${e.message}"
                )
            }
        }
    }
    
    fun getExpensesGroupedByCategory(): Map<ExpenseCategory, List<Expense>> {
        return _state.value.expenses.groupBy { it.category }
    }
    
    fun getExpensesGroupedByTime(): Map<String, List<Expense>> {
        val currentState = _state.value
        val now = LocalDate.now()
        
        return currentState.expenses.groupBy { expense ->
            when {
                expense.timestamp.toLocalDate() == now -> "Today"
                expense.timestamp.toLocalDate() == now.minusDays(1) -> "Yesterday"
                expense.timestamp.toLocalDate().isAfter(now.minusDays(7)) -> "This Week"
                expense.timestamp.toLocalDate().isAfter(now.minusDays(30)) -> "This Month"
                else -> "Older"
            }
        }
    }
}
