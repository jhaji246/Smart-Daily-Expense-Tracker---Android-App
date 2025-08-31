package com.avi.smartdailyexpensetracker.data.repository

import com.avi.smartdailyexpensetracker.data.dao.ExpenseDao
import com.avi.smartdailyexpensetracker.data.mapper.ExpenseMapper
import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity as DataExpense
import com.avi.smartdailyexpensetracker.data.entity.SyncStatus
import com.avi.smartdailyexpensetracker.domain.entity.Expense as DomainExpense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory as DomainExpenseCategory
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseReport as DomainExpenseReport
import com.avi.smartdailyexpensetracker.domain.entity.DailyTotal as DomainDailyTotal
import com.avi.smartdailyexpensetracker.domain.entity.CategoryTotal as DomainCategoryTotal
import com.avi.smartdailyexpensetracker.domain.repository.ExpenseRepository as DomainExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao
) : DomainExpenseRepository {
    
    override fun getAllExpenses(): Flow<List<DomainExpense>> = 
        expenseDao.getAllExpenses().map { dataExpenses ->
            dataExpenses.map { ExpenseMapper.mapToDomain(it) }
        }
    
    override fun getExpensesByDate(date: LocalDate): Flow<List<DomainExpense>> = 
        expenseDao.getExpensesByDate(date.toString()).map { dataExpenses ->
            dataExpenses.map { ExpenseMapper.mapToDomain(it) }
        }
    
        override fun getExpensesBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<DomainExpense>> =
        expenseDao.getExpensesByDateRange(startDate.toLocalDate().toString(), endDate.toLocalDate().toString()).map { dataExpenses ->
            dataExpenses.map { ExpenseMapper.mapToDomain(it) }
        }
    
    override fun getExpensesByCategory(category: DomainExpenseCategory): Flow<List<DomainExpense>> = 
        expenseDao.getExpensesByCategory(category.name).map { dataExpenses ->
            dataExpenses.map { ExpenseMapper.mapToDomain(it) }
        }
    
    override fun getTotalAmountForDate(date: LocalDate): Flow<Double?> = 
        kotlinx.coroutines.flow.flow { 
            emit(expenseDao.getExpenseAmountByDate(date.toString()))
        }
    
    override fun getExpenseCountForDate(date: LocalDate): Flow<Int> = 
        kotlinx.coroutines.flow.flow { 
            emit(expenseDao.getExpenseCountByDate(date.toString()))
        }
    
    override suspend fun insertExpense(expense: DomainExpense): Long = 
        expenseDao.insertExpense(ExpenseMapper.mapToData(expense))
    
    override suspend fun updateExpense(expense: DomainExpense) = 
        expenseDao.updateExpense(ExpenseMapper.mapToData(expense))
    
    override suspend fun deleteExpense(expense: DomainExpense) = 
        expenseDao.softDeleteExpense(expense.id)
    
    override suspend fun deleteExpenseById(expenseId: Long) = 
        expenseDao.softDeleteExpense(expenseId)
    
    override suspend fun generateReport(startDate: LocalDate, endDate: LocalDate): DomainExpenseReport {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        
        val expenses = expenseDao.getExpensesByDateRange(startDate.toString(), endDate.toString()).first()
        
        val dailyTotals = expenses
            .groupBy { it.timestamp.toLocalDate() }
            .map { (date, expenseList) ->
                DomainDailyTotal(
                    date = date,
                    amount = expenseList.sumOf { it.amount },
                    count = expenseList.size
                )
            }
            .sortedBy { it.date }
        
        val categoryTotals = expenses
            .groupBy { it.category }
            .map { (category, expenseList) ->
                val totalAmount = expenseList.sumOf { it.amount }
                val totalAmountAll = expenses.sumOf { it.amount }
                DomainCategoryTotal(
                    category = ExpenseMapper.mapCategoryToDomain(category),
                    amount = totalAmount,
                    count = expenseList.size,
                    percentage = if (totalAmountAll > 0) (totalAmount / totalAmountAll) * 100 else 0.0
                )
            }
            .sortedByDescending { it.amount }
        
        return DomainExpenseReport(
            startDate = startDate,
            endDate = endDate,
            dailyTotals = dailyTotals,
            categoryTotals = categoryTotals,
            totalAmount = expenses.sumOf { it.amount },
            totalCount = expenses.size
        )
    }
}


