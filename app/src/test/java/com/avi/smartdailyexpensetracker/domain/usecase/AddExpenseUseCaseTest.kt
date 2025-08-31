package com.avi.smartdailyexpensetracker.domain.usecase

import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.domain.repository.ExpenseRepository
import com.avi.smartdailyexpensetracker.domain.exception.ValidationException
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertFalse

@RunWith(MockitoJUnitRunner::class)
class AddExpenseUseCaseTest {

    @Mock
    private lateinit var mockRepository: ExpenseRepository

    private lateinit var addExpenseUseCase: AddExpenseUseCase

    @Before
    fun setup() {
        addExpenseUseCase = AddExpenseUseCase(mockRepository)
    }

    @Test
    fun `invoke should throw validation exception for empty title`() = runTest {
        // Given
        val title = ""
        val amount = "100.0"
        val category = ExpenseCategory.FOOD

        // When & Then
        val result = addExpenseUseCase(
            title = title,
            amount = amount,
            category = category,
            notes = null,
            receiptImagePath = null
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
        assertEquals("Expense title cannot be empty", result.exceptionOrNull()?.message)
        verifyNoInteractions(mockRepository)
    }

    @Test
    fun `invoke should throw validation exception for blank title`() = runTest {
        // Given
        val title = "   "
        val amount = "100.0"
        val category = ExpenseCategory.FOOD

        // When & Then
        val result = addExpenseUseCase(
            title = title,
            amount = amount,
            category = category,
            notes = null,
            receiptImagePath = null
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
        assertEquals("Expense title cannot be empty", result.exceptionOrNull()?.message)
        verifyNoInteractions(mockRepository)
    }

    @Test
    fun `invoke should throw validation exception for zero amount`() = runTest {
        // Given
        val title = "Test Expense"
        val amount = "0.0"
        val category = ExpenseCategory.FOOD

        // When & Then
        val result = addExpenseUseCase(
            title = title,
            amount = amount,
            category = category,
            notes = null,
            receiptImagePath = null
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
        assertEquals("Expense amount must be a positive number", result.exceptionOrNull()?.message)
        verifyNoInteractions(mockRepository)
    }

    @Test
    fun `invoke should throw validation exception for negative amount`() = runTest {
        // Given
        val title = "Test Expense"
        val amount = "-100.0"
        val category = ExpenseCategory.FOOD

        // When & Then
        val result = addExpenseUseCase(
            title = title,
            amount = amount,
            category = category,
            notes = null,
            receiptImagePath = null
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
        assertEquals("Expense amount must be a positive number", result.exceptionOrNull()?.message)
        verifyNoInteractions(mockRepository)
    }

    @Test
    fun `invoke should throw validation exception for notes exceeding max length`() = runTest {
        // Given
        val title = "Test Expense"
        val amount = "100.0"
        val category = ExpenseCategory.FOOD
        val longNotes = "A".repeat(101)

        // When & Then
        val result = addExpenseUseCase(
            title = title,
            amount = amount,
            category = category,
            notes = longNotes,
            receiptImagePath = null
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
        assertEquals("Notes cannot exceed 100 characters", result.exceptionOrNull()?.message)
        verifyNoInteractions(mockRepository)
    }

    @Test
    fun `invoke should throw validation exception for amount exceeding limit`() = runTest {
        // Given
        val title = "Test Expense"
        val amount = "1000000.0"
        val category = ExpenseCategory.FOOD

        // When & Then
        val result = addExpenseUseCase(
            title = title,
            amount = amount,
            category = category,
            notes = null,
            receiptImagePath = null
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
        assertEquals("Expense amount cannot exceed ₹999,999.99", result.exceptionOrNull()?.message)
        verifyNoInteractions(mockRepository)
    }

    @Test
    fun `invoke should throw validation exception for title exceeding max length`() = runTest {
        // Given
        val title = "A".repeat(101)
        val amount = "100.0"
        val category = ExpenseCategory.FOOD

        // When & Then
        val result = addExpenseUseCase(
            title = title,
            amount = amount,
            category = category,
            notes = null,
            receiptImagePath = null
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
        assertEquals("Expense title cannot exceed 100 characters", result.exceptionOrNull()?.message)
        verifyNoInteractions(mockRepository)
    }
}
