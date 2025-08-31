package com.avi.smartdailyexpensetracker.ui.state

import androidx.compose.runtime.Stable
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import java.time.LocalDate

@Stable
data class SearchFilterState(
    val searchQuery: String = "",
    val selectedCategories: Set<ExpenseCategory> = emptySet(),
    val dateRange: DateRange = DateRange(),
    val amountRange: AmountRange = AmountRange(),
    val sortBy: SortOption = SortOption.DATE_DESC,
    val groupBy: GroupOption = GroupOption.NONE
)

@Stable
data class DateRange(
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val type: DateRangeType = DateRangeType.ALL_TIME
) {
    fun isValid(): Boolean {
        return when (type) {
            DateRangeType.ALL_TIME -> true
            DateRangeType.CUSTOM -> startDate != null && endDate != null && !startDate.isAfter(endDate)
            DateRangeType.TODAY -> true
            DateRangeType.THIS_WEEK -> true
            DateRangeType.THIS_MONTH -> true
            DateRangeType.LAST_7_DAYS -> true
            DateRangeType.LAST_30_DAYS -> true
        }
    }
}

@Stable
data class AmountRange(
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val type: AmountRangeType = AmountRangeType.ALL_AMOUNTS
) {
    fun isValid(): Boolean {
        return when (type) {
            AmountRangeType.ALL_AMOUNTS -> true
            AmountRangeType.CUSTOM -> {
                val min = minAmount ?: 0.0
                val max = maxAmount ?: Double.MAX_VALUE
                min <= max
            }
            AmountRangeType.LOW -> true
            AmountRangeType.MEDIUM -> true
            AmountRangeType.HIGH -> true
        }
    }
}

enum class DateRangeType(val displayName: String) {
    ALL_TIME("All Time"),
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    LAST_7_DAYS("Last 7 Days"),
    LAST_30_DAYS("Last 30 Days"),
    CUSTOM("Custom Range")
}

enum class AmountRangeType(val displayName: String, val min: Double?, val max: Double?) {
    ALL_AMOUNTS("All Amounts", null, null),
    LOW("Low (₹0 - ₹1000)", 0.0, 1000.0),
    MEDIUM("Medium (₹1000 - ₹5000)", 1000.0, 5000.0),
    HIGH("High (₹5000+)", 5000.0, null),
    CUSTOM("Custom Range", null, null)
}

enum class SortOption(val displayName: String) {
    DATE_DESC("Date (Newest First)"),
    DATE_ASC("Date (Oldest First)"),
    AMOUNT_DESC("Amount (High to Low)"),
    AMOUNT_ASC("Amount (Low to High)"),
    TITLE_ASC("Title (A-Z)"),
    TITLE_DESC("Title (Z-A)"),
    CATEGORY_ASC("Category (A-Z)")
}

enum class GroupOption(val displayName: String) {
    NONE("No Grouping"),
    BY_CATEGORY("Group by Category"),
    BY_DATE("Group by Date"),
    BY_AMOUNT_RANGE("Group by Amount Range")
}
