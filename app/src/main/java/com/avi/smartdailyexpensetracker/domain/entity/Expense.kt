package com.avi.smartdailyexpensetracker.domain.entity

import java.time.LocalDateTime

data class Expense(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val notes: String? = null,
    val receiptImagePath: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

enum class ExpenseCategory(val displayName: String, val color: Long) {
    STAFF("Staff", 0xFF1976D2),
    TRAVEL("Travel", 0xFF388E3C),
    FOOD("Food", 0xFFFF9800),
    UTILITY("Utility", 0xFF9C27B0)
}
