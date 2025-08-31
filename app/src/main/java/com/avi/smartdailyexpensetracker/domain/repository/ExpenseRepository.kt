package com.avi.smartdailyexpensetracker.domain.repository

import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseReport
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

interface ExpenseRepository {
    
    fun getAllExpenses(): Flow<List<Expense>>
    
    fun getExpensesByDate(date: LocalDate): Flow<List<Expense>>
    
    fun getExpensesBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Expense>>
    
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>>
    
    fun getTotalAmountForDate(date: LocalDate): Flow<Double?>
    
    fun getExpenseCountForDate(date: LocalDate): Flow<Int>
    
    suspend fun insertExpense(expense: Expense): Long
    
    suspend fun updateExpense(expense: Expense)
    
    suspend fun deleteExpense(expense: Expense)
    
    suspend fun deleteExpenseById(expenseId: Long)
    
    suspend fun generateReport(startDate: LocalDate, endDate: LocalDate): ExpenseReport
}
