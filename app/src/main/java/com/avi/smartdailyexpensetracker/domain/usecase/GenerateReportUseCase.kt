package com.avi.smartdailyexpensetracker.domain.usecase

import com.avi.smartdailyexpensetracker.domain.entity.ExpenseReport
import com.avi.smartdailyexpensetracker.domain.entity.DailyTotal
import com.avi.smartdailyexpensetracker.domain.entity.CategoryTotal
import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.exception.ValidationException
import com.avi.smartdailyexpensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime

class GenerateReportUseCase(
    private val repository: ExpenseRepository
) {
    
    suspend operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<ExpenseReport> {
        return try {
            // Business validation
            validateDateRange(startDate, endDate)
            
            val startDateTime = startDate.atStartOfDay()
            val endDateTime = endDate.atTime(23, 59, 59)
            
            val expenses = repository.getExpensesBetweenDates(startDateTime, endDateTime).first()
            
            val report = ExpenseReport(
                startDate = startDate,
                endDate = endDate,
                dailyTotals = generateDailyTotals(expenses),
                categoryTotals = generateCategoryTotals(expenses),
                totalAmount = expenses.sumOf { it.amount },
                totalCount = expenses.size
            )
            
            Result.success(report)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun validateDateRange(startDate: LocalDate, endDate: LocalDate) {
        if (startDate.isAfter(endDate)) {
            throw ValidationException("Start date cannot be after end date")
        }
        
        val daysBetween = endDate.toEpochDay() - startDate.toEpochDay()
        if (daysBetween > 365) {
            throw ValidationException("Report range cannot exceed 1 year")
        }
    }
    
    private fun generateDailyTotals(expenses: List<Expense>): List<DailyTotal> {
        return expenses
            .groupBy { it.timestamp.toLocalDate() }
            .map { (date, expenseList) ->
                DailyTotal(
                    date = date,
                    amount = expenseList.sumOf { it.amount },
                    count = expenseList.size
                )
            }
            .sortedBy { it.date }
    }
    
    private fun generateCategoryTotals(expenses: List<Expense>): List<CategoryTotal> {
        val totalAmount = expenses.sumOf { it.amount }
        
        return expenses
            .groupBy { it.category }
            .map { (category, expenseList) ->
                val categoryAmount = expenseList.sumOf { it.amount }
                CategoryTotal(
                    category = category,
                    amount = categoryAmount,
                    count = expenseList.size,
                    percentage = if (totalAmount > 0) (categoryAmount / totalAmount) * 100 else 0.0
                )
            }
            .sortedByDescending { it.amount }
    }
}


