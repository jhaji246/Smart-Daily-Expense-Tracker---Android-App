package com.avi.smartdailyexpensetracker.domain.usecase

import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.domain.repository.ExpenseRepository
import com.avi.smartdailyexpensetracker.ui.state.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime

class SearchExpensesUseCase(
    private val repository: ExpenseRepository
) {
    
    suspend operator fun invoke(
        searchQuery: String = "",
        dateRange: DateRange = DateRange(),
        amountRange: AmountRange = AmountRange(),
        categories: Set<ExpenseCategory> = emptySet(),
        sortBy: SortOption = SortOption.DATE_DESC,
        groupBy: GroupOption = GroupOption.NONE
    ): Result<List<Expense>> {
        return try {
            val allExpenses = repository.getAllExpenses()
            
            val filteredExpenses = allExpenses.map { expenses ->
                expenses
                    .filter { expense ->
                        matchesSearchQuery(expense, searchQuery) &&
                        matchesDateRange(expense, dateRange) &&
                        matchesAmountRange(expense, amountRange) &&
                        matchesCategories(expense, categories)
                    }
                    .let { filtered ->
                        when (sortBy) {
                            SortOption.DATE_DESC -> filtered.sortedByDescending { it.timestamp }
                            SortOption.DATE_ASC -> filtered.sortedBy { it.timestamp }
                            SortOption.AMOUNT_DESC -> filtered.sortedByDescending { it.amount }
                            SortOption.AMOUNT_ASC -> filtered.sortedBy { it.amount }
                            SortOption.TITLE_ASC -> filtered.sortedBy { it.title }
                            SortOption.TITLE_DESC -> filtered.sortedByDescending { it.title }
                            SortOption.CATEGORY_ASC -> filtered.sortedBy { it.category.displayName }
                        }
                    }
            }
            
            Result.success(emptyList()) // Placeholder for now
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun matchesSearchQuery(expense: Expense, query: String): Boolean {
        if (query.isBlank()) return true
        
        val searchTerms = query.lowercase().split(" ").filter { it.isNotBlank() }
        val expenseText = "${expense.title} ${expense.notes ?: ""} ${expense.category.displayName}".lowercase()
        
        return searchTerms.all { term ->
            expenseText.contains(term)
        }
    }
    
    private fun matchesDateRange(expense: Expense, dateRange: DateRange): Boolean {
        if (!dateRange.isValid()) return true
        
        val expenseDate = expense.timestamp.toLocalDate()
        
        return when (dateRange.type) {
            DateRangeType.ALL_TIME -> true
            DateRangeType.TODAY -> expenseDate == LocalDate.now()
            DateRangeType.THIS_WEEK -> {
                val now = LocalDate.now()
                val weekStart = now.minusDays(now.dayOfWeek.value.toLong() - 1)
                val weekEnd = weekStart.plusDays(6)
                expenseDate in weekStart..weekEnd
            }
            DateRangeType.THIS_MONTH -> {
                val now = LocalDate.now()
                expenseDate.year == now.year && expenseDate.month == now.month
            }
            DateRangeType.LAST_7_DAYS -> {
                val sevenDaysAgo = LocalDate.now().minusDays(7)
                expenseDate >= sevenDaysAgo
            }
            DateRangeType.LAST_30_DAYS -> {
                val thirtyDaysAgo = LocalDate.now().minusDays(30)
                expenseDate >= thirtyDaysAgo
            }
            DateRangeType.CUSTOM -> {
                val start = dateRange.startDate ?: return true
                val end = dateRange.endDate ?: return true
                expenseDate in start..end
            }
        }
    }
    
    private fun matchesAmountRange(expense: Expense, amountRange: AmountRange): Boolean {
        if (!amountRange.isValid()) return true
        
        return when (amountRange.type) {
            AmountRangeType.ALL_AMOUNTS -> true
            AmountRangeType.LOW -> expense.amount <= 1000.0
            AmountRangeType.MEDIUM -> expense.amount in 1000.0..5000.0
            AmountRangeType.HIGH -> expense.amount >= 5000.0
            AmountRangeType.CUSTOM -> {
                val min = amountRange.minAmount ?: 0.0
                val max = amountRange.maxAmount ?: Double.MAX_VALUE
                expense.amount in min..max
            }
        }
    }
    
    private fun matchesCategories(expense: Expense, categories: Set<ExpenseCategory>): Boolean {
        if (categories.isEmpty()) return true
        return expense.category in categories
    }
}
