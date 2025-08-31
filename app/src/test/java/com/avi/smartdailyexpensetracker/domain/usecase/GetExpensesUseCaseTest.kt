package com.avi.smartdailyexpensetracker.domain.usecase

import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate
import java.time.LocalDateTime
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue

@RunWith(MockitoJUnitRunner::class)
class GetExpensesUseCaseTest {

    @Mock
    private lateinit var mockRepository: ExpenseRepository

    private lateinit var getExpensesUseCase: GetExpensesUseCase

    @Before
    fun setup() {
        getExpensesUseCase = GetExpensesUseCase(mockRepository)
    }

    @Test
    fun `invoke with date should return expenses for specific date successfully`() = runTest {
        // Given
        val date = LocalDate.now()
        val mockExpenses = createMockExpenses()
        `when`(mockRepository.getExpensesByDate(date)).thenReturn(flowOf(mockExpenses))

        // When
        val result = getExpensesUseCase(date)

        // Then
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val expenses = result.getOrNull()
        assertEquals(3, expenses?.size)
        assertEquals(ExpenseCategory.FOOD, expenses?.get(0)?.category)
        assertEquals(ExpenseCategory.TRAVEL, expenses?.get(1)?.category)
        assertEquals(ExpenseCategory.UTILITY, expenses?.get(2)?.category)
        verify(mockRepository).getExpensesByDate(date)
        verifyNoMoreInteractions(mockRepository)
    }

    @Test
    fun `invoke with date should return empty list when no expenses exist`() = runTest {
        // Given
        val date = LocalDate.now()
        `when`(mockRepository.getExpensesByDate(date)).thenReturn(flowOf(emptyList()))

        // When
        val result = getExpensesUseCase(date)

        // Then
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val expenses = result.getOrNull()
        assertEquals(0, expenses?.size)
        verify(mockRepository).getExpensesByDate(date)
    }

    @Test
    fun `invoke with date range should return expenses in date range successfully`() = runTest {
        // Given
        val startDate = LocalDate.now().minusDays(7)
        val endDate = LocalDate.now()
        val mockExpenses = createMockExpenses()
        `when`(mockRepository.getExpensesBetweenDates(
            startDate.atStartOfDay(),
            endDate.atTime(23, 59, 59)
        )).thenReturn(flowOf(mockExpenses))

        // When
        val result = getExpensesUseCase(startDate, endDate)

        // Then
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val expenses = result.getOrNull()
        assertEquals(3, expenses?.size)
        verify(mockRepository).getExpensesBetweenDates(
            startDate.atStartOfDay(),
            endDate.atTime(23, 59, 59)
        )
    }

    @Test
    fun `invoke with category should return expenses by category successfully`() = runTest {
        // Given
        val category = ExpenseCategory.FOOD
        val mockExpenses = createMockExpenses().filter { it.category == category }
        `when`(mockRepository.getExpensesByCategory(category)).thenReturn(flowOf(mockExpenses))

        // When
        val result = getExpensesUseCase(category)

        // Then
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val expenses = result.getOrNull()
        assertEquals(1, expenses?.size)
        assertEquals(ExpenseCategory.FOOD, expenses?.get(0)?.category)
        verify(mockRepository).getExpensesByCategory(category)
    }

    @Test
    fun `invoke should handle repository errors gracefully`() = runTest {
        // Given
        val date = LocalDate.now()
        val errorMessage = "Database connection failed"
        `when`(mockRepository.getExpensesByDate(date)).thenThrow(RuntimeException(errorMessage))

        // When & Then
        val result = getExpensesUseCase(date)
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
        verify(mockRepository).getExpensesByDate(date)
    }

    @Test
    fun `invoke should return expenses with correct data structure`() = runTest {
        // Given
        val date = LocalDate.now()
        val mockExpenses = createMockExpenses()
        `when`(mockRepository.getExpensesByDate(date)).thenReturn(flowOf(mockExpenses))

        // When
        val result = getExpensesUseCase(date)

        // Then
        assertTrue(result.isSuccess)
        val expenses = result.getOrNull()
        expenses?.forEach { expense ->
            assertNotNull(expense.id)
            assertNotNull(expense.title)
            assertNotNull(expense.amount)
            assertNotNull(expense.category)
            assertNotNull(expense.timestamp)
        }
    }

    @Test
    fun `invoke with date range should handle empty results`() = runTest {
        // Given
        val startDate = LocalDate.now().minusDays(7)
        val endDate = LocalDate.now()
        `when`(mockRepository.getExpensesBetweenDates(
            startDate.atStartOfDay(),
            endDate.atTime(23, 59, 59)
        )).thenReturn(flowOf(emptyList()))

        // When
        val result = getExpensesUseCase(startDate, endDate)

        // Then
        assertTrue(result.isSuccess)
        val expenses = result.getOrNull()
        assertEquals(0, expenses?.size)
        verify(mockRepository).getExpensesBetweenDates(
            startDate.atStartOfDay(),
            endDate.atTime(23, 59, 59)
        )
    }

    @Test
    fun `invoke with category should handle empty results`() = runTest {
        // Given
        val category = ExpenseCategory.STAFF
        `when`(mockRepository.getExpensesByCategory(category)).thenReturn(flowOf(emptyList()))

        // When
        val result = getExpensesUseCase(category)

        // Then
        assertTrue(result.isSuccess)
        val expenses = result.getOrNull()
        assertEquals(0, expenses?.size)
        verify(mockRepository).getExpensesByCategory(category)
    }

    @Test
    fun `invoke should handle single expense correctly`() = runTest {
        // Given
        val date = LocalDate.now()
        val singleExpense = listOf(createMockExpenses()[0])
        `when`(mockRepository.getExpensesByDate(date)).thenReturn(flowOf(singleExpense))

        // When
        val result = getExpensesUseCase(date)

        // Then
        assertTrue(result.isSuccess)
        val expenses = result.getOrNull()
        assertEquals(1, expenses?.size)
        assertEquals("Lunch", expenses?.get(0)?.title)
        assertEquals(25.0, expenses?.get(0)?.amount ?: 0.0, 0.01)
    }

    @Test
    fun `invoke should handle multiple expenses with same category`() = runTest {
        // Given
        val category = ExpenseCategory.FOOD
        val foodExpenses = listOf(
            createMockExpenses()[0],
            createMockExpenses()[0].copy(id = 4L, title = "Dinner", amount = 35.0)
        )
        `when`(mockRepository.getExpensesByCategory(category)).thenReturn(flowOf(foodExpenses))

        // When
        val result = getExpensesUseCase(category)

        // Then
        assertTrue(result.isSuccess)
        val expenses = result.getOrNull()
        assertEquals(2, expenses?.size)
        expenses?.forEach { expense ->
            assertEquals(ExpenseCategory.FOOD, expense.category)
        }
    }

    private fun createMockExpenses(): List<Expense> {
        val now = LocalDateTime.now()
        return listOf(
            Expense(
                id = 1L,
                title = "Lunch",
                amount = 25.00,
                category = ExpenseCategory.FOOD,
                notes = "Office lunch",
                receiptImagePath = null,
                timestamp = now.minusHours(2)
            ),
            Expense(
                id = 2L,
                title = "Taxi",
                amount = 45.50,
                category = ExpenseCategory.TRAVEL,
                notes = "Client meeting",
                receiptImagePath = null,
                timestamp = now.minusHours(1)
            ),
            Expense(
                id = 3L,
                title = "Electricity Bill",
                amount = 120.00,
                category = ExpenseCategory.UTILITY,
                notes = "Monthly bill",
                receiptImagePath = null,
                timestamp = now
            )
        )
    }
}
