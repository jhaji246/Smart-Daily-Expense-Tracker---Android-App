package com.avi.smartdailyexpensetracker.data.mapper

import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity
import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDateTime
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull

@RunWith(JUnit4::class)
class ExpenseMapperTest {

    @Test
    fun `mapToDomain should correctly map ExpenseEntity to Expense`() {
        // Given
        val timestamp = LocalDateTime.now()
        val expenseEntity = ExpenseEntity(
            id = 1L,
            title = "Test Expense",
            amount = 100.50,
            category = "FOOD",
            notes = "Test notes",
            receiptImagePath = "/path/to/image.jpg",
            timestamp = timestamp
        )

        // When
        val result = ExpenseMapper.mapToDomain(expenseEntity)

        // Then
        assertEquals(1L, result.id)
        assertEquals("Test Expense", result.title)
        assertEquals(100.50, result.amount, 0.01)
        assertEquals(ExpenseCategory.FOOD, result.category)
        assertEquals("Test notes", result.notes)
        assertEquals("/path/to/image.jpg", result.receiptImagePath)
        assertEquals(timestamp, result.timestamp)
    }

    @Test
    fun `mapToDomain should handle null notes and receiptImagePath`() {
        // Given
        val timestamp = LocalDateTime.now()
        val expenseEntity = ExpenseEntity(
            id = 2L,
            title = "Test Expense 2",
            amount = 200.0,
            category = "TRAVEL",
            notes = null,
            receiptImagePath = null,
            timestamp = timestamp
        )

        // When
        val result = ExpenseMapper.mapToDomain(expenseEntity)

        // Then
        assertEquals(2L, result.id)
        assertEquals("Test Expense 2", result.title)
        assertEquals(200.0, result.amount, 0.01)
        assertEquals(ExpenseCategory.TRAVEL, result.category)
        assertNull(result.notes)
        assertNull(result.receiptImagePath)
        assertEquals(timestamp, result.timestamp)
    }

    @Test
    fun `mapToDomain should map all expense categories correctly`() {
        // Given
        val timestamp = LocalDateTime.now()
        val categories = listOf("STAFF", "TRAVEL", "FOOD", "UTILITY")
        val expectedCategories = listOf(
            ExpenseCategory.STAFF,
            ExpenseCategory.TRAVEL,
            ExpenseCategory.FOOD,
            ExpenseCategory.UTILITY
        )

        categories.forEachIndexed { index, category ->
            val expenseEntity = ExpenseEntity(
                id = index.toLong(),
                title = "Test $category",
                amount = 100.0,
                category = category,
                notes = null,
                receiptImagePath = null,
                timestamp = timestamp
            )

            // When
            val result = ExpenseMapper.mapToDomain(expenseEntity)

            // Then
            assertEquals(expectedCategories[index], result.category)
        }
    }

    @Test
    fun `mapToData should correctly map Expense to ExpenseEntity`() {
        // Given
        val timestamp = LocalDateTime.now()
        val expense = Expense(
            id = 1L,
            title = "Test Expense",
            amount = 100.50,
            category = ExpenseCategory.FOOD,
            notes = "Test notes",
            receiptImagePath = "/path/to/image.jpg",
            timestamp = timestamp
        )

        // When
        val result = ExpenseMapper.mapToData(expense)

        // Then
        assertEquals(1L, result.id)
        assertEquals("Test Expense", result.title)
        assertEquals(100.50, result.amount, 0.01)
        assertEquals("FOOD", result.category)
        assertEquals("Test notes", result.notes)
        assertEquals("/path/to/image.jpg", result.receiptImagePath)
        assertEquals(timestamp, result.timestamp)
    }

    @Test
    fun `mapToData should handle null notes and receiptImagePath`() {
        // Given
        val timestamp = LocalDateTime.now()
        val expense = Expense(
            id = 2L,
            title = "Test Expense 2",
            amount = 200.0,
            category = ExpenseCategory.TRAVEL,
            notes = null,
            receiptImagePath = null,
            timestamp = timestamp
        )

        // When
        val result = ExpenseMapper.mapToData(expense)

        // Then
        assertEquals(2L, result.id)
        assertEquals("Test Expense 2", result.title)
        assertEquals(200.0, result.amount, 0.01)
        assertEquals("TRAVEL", result.category)
        assertNull(result.notes)
        assertEquals(timestamp, result.timestamp)
    }

    @Test
    fun `mapToData should map all expense categories to strings correctly`() {
        // Given
        val timestamp = LocalDateTime.now()
        val categories = listOf(
            ExpenseCategory.STAFF,
            ExpenseCategory.TRAVEL,
            ExpenseCategory.FOOD,
            ExpenseCategory.UTILITY
        )
        val expectedCategoryStrings = listOf("STAFF", "TRAVEL", "FOOD", "UTILITY")

        categories.forEachIndexed { index, category ->
            val expense = Expense(
                id = index.toLong(),
                title = "Test ${category.displayName}",
                amount = 100.0,
                category = category,
                notes = null,
                receiptImagePath = null,
                timestamp = timestamp
            )

            // When
            val result = ExpenseMapper.mapToData(expense)

            // Then
            assertEquals(expectedCategoryStrings[index], result.category)
        }
    }
}
