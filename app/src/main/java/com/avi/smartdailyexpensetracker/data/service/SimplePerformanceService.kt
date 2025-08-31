package com.avi.smartdailyexpensetracker.data.service

import com.avi.smartdailyexpensetracker.data.dao.ExpenseDao
import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SimplePerformanceService(
    private val expenseDao: ExpenseDao
) {
    
    companion object {
        const val DEFAULT_PAGE_SIZE = 20
        const val MAX_PAGE_SIZE = 100
    }
    
    // Simple pagination without complex Flow operations
    suspend fun getExpensesPaginated(
        pageSize: Int = DEFAULT_PAGE_SIZE,
        offset: Int = 0
    ): SimplePaginatedResult {
        return withContext(Dispatchers.IO) {
            try {
                val actualPageSize = pageSize.coerceIn(1, MAX_PAGE_SIZE)
                val allExpenses = expenseDao.getAllExpenses().first()
                val expensesList = allExpenses.toList()
                
                val totalCount = expensesList.size
                val expenses = expensesList.drop(offset).take(actualPageSize)
                
                val hasNextPage = (offset + actualPageSize) < totalCount
                val hasPreviousPage = offset > 0
                val currentPage = (offset / actualPageSize) + 1
                val totalPages = if (totalCount > 0) (totalCount + actualPageSize - 1) / actualPageSize else 0
                
                SimplePaginatedResult(
                    data = expenses,
                    paginationInfo = SimplePaginationInfo(
                        currentPage = currentPage,
                        totalPages = totalPages,
                        pageSize = actualPageSize,
                        totalCount = totalCount,
                        hasNextPage = hasNextPage,
                        hasPreviousPage = hasPreviousPage,
                        offset = offset
                    ),
                    error = null
                )
                
            } catch (e: Exception) {
                SimplePaginatedResult(
                    data = emptyList(),
                    paginationInfo = null,
                    error = e.message
                )
            }
        }
    }
    
    // Simple search with pagination
    suspend fun searchExpensesPaginated(
        query: String,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        offset: Int = 0
    ): SimplePaginatedResult {
        return withContext(Dispatchers.IO) {
            try {
                val actualPageSize = pageSize.coerceIn(1, MAX_PAGE_SIZE)
                val allExpenses = expenseDao.getAllExpenses().first()
                val expensesList = allExpenses.toList()
                
                val filteredExpenses = expensesList.filter { expense ->
                    expense.title.contains(query, ignoreCase = true) ||
                    expense.category.contains(query, ignoreCase = true) ||
                    (expense.notes?.contains(query, ignoreCase = true) == true)
                }
                
                val totalCount = filteredExpenses.size
                val expenses = filteredExpenses.drop(offset).take(actualPageSize)
                
                val hasNextPage = (offset + actualPageSize) < totalCount
                val hasPreviousPage = offset > 0
                val currentPage = (offset / actualPageSize) + 1
                val totalPages = if (totalCount > 0) (totalCount + actualPageSize - 1) / actualPageSize else 0
                
                SimplePaginatedResult(
                    data = expenses,
                    paginationInfo = SimplePaginationInfo(
                        currentPage = currentPage,
                        totalPages = totalPages,
                        pageSize = actualPageSize,
                        totalCount = totalCount,
                        hasNextPage = hasNextPage,
                        hasPreviousPage = hasPreviousPage,
                        offset = offset
                    ),
                    error = null
                )
                
            } catch (e: Exception) {
                SimplePaginatedResult(
                    data = emptyList(),
                    paginationInfo = null,
                    error = e.message
                )
            }
        }
    }
    
    // Simple category filtering
    suspend fun getExpensesByCategoryPaginated(
        category: String,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        offset: Int = 0
    ): SimplePaginatedResult {
        return withContext(Dispatchers.IO) {
            try {
                val actualPageSize = pageSize.coerceIn(1, MAX_PAGE_SIZE)
                val allExpenses = expenseDao.getAllExpenses().first()
                val expensesList = allExpenses.toList()
                
                val categoryExpenses = expensesList.filter { expense -> expense.category == category }
                val totalCount = categoryExpenses.size
                val expenses = categoryExpenses.drop(offset).take(actualPageSize)
                
                val hasNextPage = (offset + actualPageSize) < totalCount
                val hasPreviousPage = offset > 0
                val currentPage = (offset / actualPageSize) + 1
                val totalPages = if (totalCount > 0) (totalCount + actualPageSize - 1) / actualPageSize else 0
                
                SimplePaginatedResult(
                    data = expenses,
                    paginationInfo = SimplePaginationInfo(
                        currentPage = currentPage,
                        totalPages = totalPages,
                        pageSize = actualPageSize,
                        totalCount = totalCount,
                        hasNextPage = hasNextPage,
                        hasPreviousPage = hasPreviousPage,
                        offset = offset
                    ),
                    error = null
                )
                
            } catch (e: Exception) {
                SimplePaginatedResult(
                    data = emptyList(),
                    paginationInfo = null,
                    error = e.message
                )
            }
        }
    }
    
    // Memory optimization: Get only essential fields
    suspend fun getExpenseSummaries(
        limit: Int = 50
    ): List<ExpenseSummary> {
        return withContext(Dispatchers.IO) {
            try {
                val allExpenses = expenseDao.getAllExpenses().first()
                allExpenses.take(limit).map { expense ->
                    ExpenseSummary(
                        id = expense.id,
                        title = expense.title,
                        amount = expense.amount,
                        category = expense.category,
                        date = expense.date
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    // Batch operations for better performance
    suspend fun batchInsertExpenses(
        expenses: List<ExpenseEntity>
    ): BatchOperationResult {
        return withContext(Dispatchers.IO) {
            try {
                var successCount = 0
                var failureCount = 0
                
                expenses.forEach { expense ->
                    try {
                        expenseDao.insertExpense(expense)
                        successCount++
                    } catch (e: Exception) {
                        failureCount++
                    }
                }
                
                BatchOperationResult.Success(
                    totalProcessed = expenses.size,
                    successCount = successCount,
                    failureCount = failureCount
                )
                
            } catch (e: Exception) {
                BatchOperationResult.Failure(e.message ?: "Batch operation failed")
            }
        }
    }
}

// Simplified data classes
data class SimplePaginatedResult(
    val data: List<ExpenseEntity>,
    val paginationInfo: SimplePaginationInfo?,
    val error: String? = null
)

data class SimplePaginationInfo(
    val currentPage: Int,
    val totalPages: Int,
    val pageSize: Int,
    val totalCount: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val offset: Int
)

data class ExpenseSummary(
    val id: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String
)

sealed class BatchOperationResult {
    data class Success(
        val totalProcessed: Int,
        val successCount: Int,
        val failureCount: Int
    ) : BatchOperationResult()
    
    data class Failure(val error: String) : BatchOperationResult()
}
