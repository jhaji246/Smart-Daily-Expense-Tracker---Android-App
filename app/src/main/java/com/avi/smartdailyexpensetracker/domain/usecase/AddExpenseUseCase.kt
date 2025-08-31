package com.avi.smartdailyexpensetracker.domain.usecase

import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.domain.exception.ValidationException
import com.avi.smartdailyexpensetracker.domain.repository.ExpenseRepository


class AddExpenseUseCase(
    private val repository: ExpenseRepository
) {
    
    suspend operator fun invoke(
        title: String,
        amount: String,
        category: ExpenseCategory,
        notes: String?,
        receiptImagePath: String?
    ): Result<Long> {
        return try {
            // Business validation rules
            validateExpenseData(title, amount, notes)
            
            val expense = Expense(
                title = title.trim(),
                amount = amount.toDouble(),
                category = category,
                notes = notes?.trim()?.takeIf { it.isNotBlank() },
                receiptImagePath = receiptImagePath
            )
            
            val expenseId = repository.insertExpense(expense)
            Result.success(expenseId)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun validateExpenseData(title: String, amount: String, notes: String?) {
        if (title.isBlank()) {
            throw ValidationException("Expense title cannot be empty")
        }
        
        if (title.length > 100) {
            throw ValidationException("Expense title cannot exceed 100 characters")
        }
        
        val amountValue = amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            throw ValidationException("Expense amount must be a positive number")
        }
        
        if (amountValue > 999999.99) {
            throw ValidationException("Expense amount cannot exceed ₹999,999.99")
        }
        
        notes?.let {
            if (it.length > 100) {
                throw ValidationException("Notes cannot exceed 100 characters")
            }
        }
    }
}
