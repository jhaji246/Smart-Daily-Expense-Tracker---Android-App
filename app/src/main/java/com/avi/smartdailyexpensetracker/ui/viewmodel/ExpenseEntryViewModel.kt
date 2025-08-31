package com.avi.smartdailyexpensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.domain.usecase.AddExpenseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class ExpenseEntryState(
    val title: String = "",
    val amount: String = "",
    val category: ExpenseCategory = ExpenseCategory.STAFF,
    val notes: String = "",
    val receiptImagePath: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val totalSpentToday: Double = 0.0
)

sealed class ExpenseEntryEvent {
    data class TitleChanged(val title: String) : ExpenseEntryEvent()
    data class AmountChanged(val amount: String) : ExpenseEntryEvent()
    data class CategoryChanged(val category: ExpenseCategory) : ExpenseEntryEvent()
    data class NotesChanged(val notes: String) : ExpenseEntryEvent()
    data class ReceiptImageChanged(val imagePath: String?) : ExpenseEntryEvent()
    object SubmitExpense : ExpenseEntryEvent()
    object ResetState : ExpenseEntryEvent()
}

class ExpenseEntryViewModel(
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ExpenseEntryState())
    val state: StateFlow<ExpenseEntryState> = _state.asStateFlow()
    
    init {
        loadTodayTotal()
    }
    
    fun onEvent(event: ExpenseEntryEvent) {
        when (event) {
            is ExpenseEntryEvent.TitleChanged -> {
                _state.value = _state.value.copy(title = event.title)
            }
            is ExpenseEntryEvent.AmountChanged -> {
                _state.value = _state.value.copy(amount = event.amount)
            }
            is ExpenseEntryEvent.CategoryChanged -> {
                _state.value = _state.value.copy(category = event.category)
            }
            is ExpenseEntryEvent.NotesChanged -> {
                _state.value = _state.value.copy(notes = event.notes)
            }
            is ExpenseEntryEvent.ReceiptImageChanged -> {
                _state.value = _state.value.copy(receiptImagePath = event.imagePath)
            }
            is ExpenseEntryEvent.SubmitExpense -> {
                submitExpense()
            }
            is ExpenseEntryEvent.ResetState -> {
                resetState()
            }
        }
    }
    
    private fun submitExpense() {
        val currentState = _state.value
        
        // Validation
        if (currentState.title.isBlank()) {
            _state.value = currentState.copy(error = "Title is required")
            return
        }
        
        val amount = currentState.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _state.value = currentState.copy(error = "Please enter a valid amount")
            return
        }
        
        if (currentState.notes.length > 100) {
            _state.value = currentState.copy(error = "Notes cannot exceed 100 characters")
            return
        }
        
        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true, error = null)
            
            try {
                val result = addExpenseUseCase(
                    title = currentState.title,
                    amount = currentState.amount,
                    category = currentState.category,
                    notes = currentState.notes.takeIf { it.isNotBlank() },
                    receiptImagePath = currentState.receiptImagePath
                )
                
                result.fold(
                    onSuccess = { expenseId ->
                        _state.value = currentState.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                        
                        // Reset form after successful submission
                        resetState()
                        
                        // Reload today's total
                        loadTodayTotal()
                    },
                    onFailure = { exception ->
                        _state.value = currentState.copy(
                            isLoading = false,
                            error = "Failed to save expense: ${exception.message}"
                        )
                    }
                )
                
            } catch (e: Exception) {
                _state.value = currentState.copy(
                    isLoading = false,
                    error = "Failed to save expense: ${e.message}"
                )
            }
        }
    }
    
    private fun resetState() {
        _state.value = ExpenseEntryState(
            totalSpentToday = _state.value.totalSpentToday
        )
    }
    
    private fun loadTodayTotal() {
        // TODO: Implement when we add GetTotalAmountUseCase
        // For now, we'll keep it simple
    }
}
