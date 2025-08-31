package com.avi.smartdailyexpensetracker.ui.navigation

sealed class Screen(val route: String) {
    object ExpenseEntry : Screen("expense_entry")
    object ExpenseList : Screen("expense_list")
    object ExpenseReport : Screen("expense_report")
    object IntelligentInsights : Screen("intelligent_insights")
}

object ExpenseNavigation {
    const val EXPENSE_ENTRY = "expense_entry"
    const val EXPENSE_LIST = "expense_list"
    const val EXPENSE_REPORT = "expense_report"
    const val INTELLIGENT_INSIGHTS = "intelligent_insights"
}
