package com.avi.smartdailyexpensetracker.data.mapper

import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity as DataExpense
import com.avi.smartdailyexpensetracker.domain.entity.Expense as DomainExpense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory as DomainExpenseCategory

object ExpenseMapper {
    
    fun mapToDomain(dataExpense: DataExpense): DomainExpense {
        return DomainExpense(
            id = dataExpense.id,
            title = dataExpense.title,
            amount = dataExpense.amount,
            category = mapCategoryToDomain(dataExpense.category),
            notes = dataExpense.notes,
            receiptImagePath = dataExpense.receiptImagePath,
            timestamp = dataExpense.timestamp
        )
    }
    
    fun mapToData(domainExpense: DomainExpense): DataExpense {
        return DataExpense(
            id = domainExpense.id,
            title = domainExpense.title,
            amount = domainExpense.amount,
            category = mapCategoryToData(domainExpense.category),
            notes = domainExpense.notes,
            receiptImagePath = domainExpense.receiptImagePath,
            timestamp = domainExpense.timestamp
        )
    }
    
    fun mapCategoryToDomain(dataCategory: String): DomainExpenseCategory {
        return when (dataCategory) {
            "STAFF" -> DomainExpenseCategory.STAFF
            "TRAVEL" -> DomainExpenseCategory.TRAVEL
            "FOOD" -> DomainExpenseCategory.FOOD
            "UTILITY" -> DomainExpenseCategory.UTILITY
            else -> DomainExpenseCategory.FOOD // Default fallback
        }
    }
    
    fun mapCategoryToData(domainCategory: DomainExpenseCategory): String {
        return when (domainCategory) {
            DomainExpenseCategory.STAFF -> "STAFF"
            DomainExpenseCategory.TRAVEL -> "TRAVEL"
            DomainExpenseCategory.FOOD -> "FOOD"
            DomainExpenseCategory.UTILITY -> "UTILITY"
        }
    }
}
