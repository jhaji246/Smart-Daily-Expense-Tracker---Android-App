package com.avi.smartdailyexpensetracker.domain.entity

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDateTime

@RunWith(JUnit4::class)
class ExpenseTest {

    @Test
    fun `expense should be created with valid data`() {
        // Given
        val id = 1L
        val title = "Test Expense"
        val amount = 100.50
        val category = ExpenseCategory.FOOD
        val notes = "Test notes"
        val receiptImagePath = "/path/to/image.jpg"
        val timestamp = LocalDateTime.now()

        // When
        val expense = Expense(
            id = id,
            title = title,
            amount = amount,
            category = category,
            notes = notes,
            receiptImagePath = receiptImagePath,
            timestamp = timestamp
        )

        // Then
        assertEquals(id, expense.id)
        assertEquals(title, expense.title)
        assertEquals(amount, expense.amount, 0.01)
        assertEquals(category, expense.category)
        assertEquals(notes, expense.notes)
        assertEquals(receiptImagePath, expense.receiptImagePath)
        assertEquals(timestamp, expense.timestamp)
    }

    @Test
    fun `expense should be created with null optional fields`() {
        // Given
        val id = 1L
        val title = "Test Expense"
        val amount = 100.50
        val category = ExpenseCategory.FOOD

        // When
        val expense = Expense(
            id = id,
            title = title,
            amount = amount,
            category = category,
            notes = null,
            receiptImagePath = null,
            timestamp = LocalDateTime.now()
        )

        // Then
        assertEquals(id, expense.id)
        assertEquals(title, expense.title)
        assertEquals(amount, expense.amount, 0.01)
        assertEquals(category, expense.category)
        assertEquals(null, expense.notes)
        assertEquals(null, expense.receiptImagePath)
        assertNotNull(expense.timestamp)
    }

    @Test
    fun `expense should be created without id for new expenses`() {
        // Given
        val title = "New Expense"
        val amount = 50.00
        val category = ExpenseCategory.TRAVEL

        // When
        val expense = Expense(
            id = 0L,
            title = title,
            amount = amount,
            category = category,
            notes = null,
            receiptImagePath = null,
            timestamp = LocalDateTime.now()
        )

        // Then
        assertEquals(0L, expense.id)
        assertEquals(title, expense.title)
        assertEquals(amount, expense.amount, 0.01)
        assertEquals(category, expense.category)
    }

    @Test
    fun `expense copy should create new instance with updated fields`() {
        // Given
        val originalExpense = createSampleExpense()

        // When
        val updatedExpense = originalExpense.copy(
            title = "Updated Title",
            amount = 200.00,
            notes = "Updated notes"
        )

        // Then
        assertEquals(originalExpense.id, updatedExpense.id)
        assertEquals("Updated Title", updatedExpense.title)
        assertEquals(200.00, updatedExpense.amount, 0.01)
        assertEquals(originalExpense.category, updatedExpense.category)
        assertEquals("Updated notes", updatedExpense.notes)
        assertEquals(originalExpense.receiptImagePath, updatedExpense.receiptImagePath)
        assertEquals(originalExpense.timestamp, updatedExpense.timestamp)
    }

    @Test
    fun `expense copy should not modify original instance`() {
        // Given
        val originalExpense = createSampleExpense()
        val originalTitle = originalExpense.title
        val originalAmount = originalExpense.amount

        // When
        originalExpense.copy(
            title = "Updated Title",
            amount = 200.00
        )

        // Then
        assertEquals(originalTitle, originalExpense.title)
        assertEquals(originalAmount, originalExpense.amount, 0.01)
    }

    @Test
    fun `expense should support partial copy operations`() {
        // Given
        val originalExpense = createSampleExpense()

        // When
        val expenseWithUpdatedTitle = originalExpense.copy(title = "New Title")
        val expenseWithUpdatedAmount = originalExpense.copy(amount = 75.25)
        val expenseWithUpdatedCategory = originalExpense.copy(category = ExpenseCategory.UTILITY)

        // Then
        assertEquals("New Title", expenseWithUpdatedTitle.title)
        assertEquals(originalExpense.amount, expenseWithUpdatedTitle.amount, 0.01)
        assertEquals(originalExpense.category, expenseWithUpdatedTitle.category)

        assertEquals(originalExpense.title, expenseWithUpdatedAmount.title)
        assertEquals(75.25, expenseWithUpdatedAmount.amount, 0.01)
        assertEquals(originalExpense.category, expenseWithUpdatedAmount.category)

        assertEquals(originalExpense.title, expenseWithUpdatedCategory.title)
        assertEquals(originalExpense.amount, expenseWithUpdatedCategory.amount, 0.01)
        assertEquals(ExpenseCategory.UTILITY, expenseWithUpdatedCategory.category)
    }

    @Test
    fun `expense should handle large amounts correctly`() {
        // Given
        val largeAmount = 999999.99

        // When
        val expense = createSampleExpense().copy(amount = largeAmount)

        // Then
        assertEquals(largeAmount, expense.amount, 0.01)
    }

    @Test
    fun `expense should handle small amounts correctly`() {
        // Given
        val smallAmount = 0.01

        // When
        val expense = createSampleExpense().copy(amount = smallAmount)

        // Then
        assertEquals(smallAmount, expense.amount, 0.01)
    }

    @Test
    fun `expense should handle long titles correctly`() {
        // Given
        val longTitle = "A".repeat(100)

        // When
        val expense = createSampleExpense().copy(title = longTitle)

        // Then
        assertEquals(longTitle, expense.title)
        assertEquals(100, expense.title.length)
    }

    @Test
    fun `expense should handle long notes correctly`() {
        // Given
        val longNotes = "A".repeat(100)

        // When
        val expense = createSampleExpense().copy(notes = longNotes)

        // Then
        assertEquals(longNotes, expense.notes)
        assertEquals(100, expense.notes?.length)
    }

    @Test
    fun `expense should handle special characters in title and notes`() {
        // Given
        val specialTitle = "Expense with special chars: !@#$%^&*()"
        val specialNotes = "Notes with symbols: €£¥₹₿"

        // When
        val expense = createSampleExpense().copy(
            title = specialTitle,
            notes = specialNotes
        )

        // Then
        assertEquals(specialTitle, expense.title)
        assertEquals(specialNotes, expense.notes)
    }

    @Test
    fun `expense should handle different timestamp values`() {
        // Given
        val pastTimestamp = LocalDateTime.now().minusDays(30)
        val futureTimestamp = LocalDateTime.now().plusDays(30)

        // When
        val pastExpense = createSampleExpense().copy(timestamp = pastTimestamp)
        val futureExpense = createSampleExpense().copy(timestamp = futureTimestamp)

        // Then
        assertEquals(pastTimestamp, pastExpense.timestamp)
        assertEquals(futureTimestamp, futureExpense.timestamp)
    }

    @Test
    fun `expense should support equality comparison`() {
        // Given
        val fixedTimestamp = LocalDateTime.of(2024, 1, 1, 12, 0, 0)
        val expense1 = createSampleExpense(fixedTimestamp)
        val expense2 = createSampleExpense(fixedTimestamp)

        // Then
        assertEquals(expense1, expense1) // Same instance
        assertEquals(expense1, expense2) // Same data
        assertEquals(expense1.id, expense1.id) // Same id
    }

    @Test
    fun `expense should have consistent hashCode`() {
        // Given
        val fixedTimestamp = LocalDateTime.of(2024, 1, 1, 12, 0, 0)
        val expense1 = createSampleExpense(fixedTimestamp)
        val expense2 = createSampleExpense(fixedTimestamp)

        // Then
        assertEquals(expense1.hashCode(), expense1.hashCode())
        assertEquals(expense1.hashCode(), expense2.hashCode())
    }

    @Test
    fun `expense category should have correct display names`() {
        // Then
        assertEquals("Staff", ExpenseCategory.STAFF.displayName)
        assertEquals("Travel", ExpenseCategory.TRAVEL.displayName)
        assertEquals("Food", ExpenseCategory.FOOD.displayName)
        assertEquals("Utility", ExpenseCategory.UTILITY.displayName)
    }

    @Test
    fun `expense category should have correct colors`() {
        // Then
        assertEquals(0xFF1976D2, ExpenseCategory.STAFF.color)
        assertEquals(0xFF388E3C, ExpenseCategory.TRAVEL.color)
        assertEquals(0xFFFF9800, ExpenseCategory.FOOD.color)
        assertEquals(0xFF9C27B0, ExpenseCategory.UTILITY.color)
    }

    private fun createSampleExpense(timestamp: LocalDateTime = LocalDateTime.now()): Expense {
        return Expense(
            id = 1L,
            title = "Sample Expense",
            amount = 100.00,
            category = ExpenseCategory.FOOD,
            notes = "Sample notes",
            receiptImagePath = "/sample/path.jpg",
            timestamp = timestamp
        )
    }
}
