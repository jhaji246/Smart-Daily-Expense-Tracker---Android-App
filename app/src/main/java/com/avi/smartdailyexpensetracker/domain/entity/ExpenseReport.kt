package com.avi.smartdailyexpensetracker.domain.entity

import java.time.LocalDate

data class ExpenseReport(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val dailyTotals: List<DailyTotal>,
    val categoryTotals: List<CategoryTotal>,
    val totalAmount: Double,
    val totalCount: Int
)

data class DailyTotal(
    val date: LocalDate,
    val amount: Double,
    val count: Int
)

data class CategoryTotal(
    val category: ExpenseCategory,
    val amount: Double,
    val count: Int,
    val percentage: Double
)
