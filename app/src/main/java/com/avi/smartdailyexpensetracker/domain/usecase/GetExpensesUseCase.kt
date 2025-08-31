package com.avi.smartdailyexpensetracker.domain.usecase

import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class GetExpensesUseCase(
    private val repository: ExpenseRepository
) {
    
    suspend operator fun invoke(date: LocalDate): Result<List<Expense>> {
        return try {
            val expenses = repository.getExpensesByDate(date).first()
            Result.success(expenses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<Expense>> {
        return try {
            val startDateTime = startDate.atStartOfDay()
            val endDateTime = endDate.atTime(23, 59, 59)
            val expenses = repository.getExpensesBetweenDates(startDateTime, endDateTime).first()
            Result.success(expenses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend operator fun invoke(category: ExpenseCategory): Result<List<Expense>> {
        return try {
            val expenses = repository.getExpensesByCategory(category).first()
            Result.success(expenses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


