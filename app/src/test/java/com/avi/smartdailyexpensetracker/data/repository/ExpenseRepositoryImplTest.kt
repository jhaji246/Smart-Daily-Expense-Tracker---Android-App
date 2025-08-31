package com.avi.smartdailyexpensetracker.data.repository

import com.avi.smartdailyexpensetracker.data.dao.ExpenseDao
import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity
import com.avi.smartdailyexpensetracker.domain.entity.Expense
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
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
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertFalse

@RunWith(MockitoJUnitRunner::class)
class ExpenseRepositoryImplTest {

    @Mock
    private lateinit var mockExpenseDao: ExpenseDao

    private lateinit var expenseRepository: ExpenseRepositoryImpl

    @Before
    fun setup() {
        expenseRepository = ExpenseRepositoryImpl(mockExpenseDao)
    }

    @Test
    fun `getAllExpenses should return mapped expenses`() = runTest {
        // Given
        val expenseEntities = listOf(
            ExpenseEntity(id = 1, title = "Test 1", amount = 100.0, category = "FOOD", notes = null, receiptImagePath = null, timestamp = LocalDateTime.now()),
            ExpenseEntity(id = 2, title = "Test 2", amount = 200.0, category = "TRAVEL", notes = "Travel expense", receiptImagePath = null, timestamp = LocalDateTime.now())
        )

        `when`(mockExpenseDao.getAllExpenses()).thenReturn(flowOf(expenseEntities))

        // When
        val result = expenseRepository.getAllExpenses()

        // Then
        val expenses = result.first()
        assertEquals(2, expenses.size)
        assertEquals("Test 1", expenses[0].title)
        assertEquals(100.0, expenses[0].amount, 0.01)
        assertEquals(ExpenseCategory.FOOD, expenses[0].category)
        assertEquals("Test 2", expenses[1].title)
        assertEquals(200.0, expenses[1].amount, 0.01)
        assertEquals(ExpenseCategory.TRAVEL, expenses[1].category)
        assertEquals("Travel expense", expenses[1].notes)
    }

    @Test
    fun `getExpensesByDate should return expenses for specific date`() = runTest {
        // Given
        val date = LocalDate.now()
        val expenseEntities = listOf(
            ExpenseEntity(id = 1, title = "Test 1", amount = 100.0, category = "FOOD", notes = null, receiptImagePath = null, timestamp = LocalDateTime.now())
        )

        `when`(mockExpenseDao.getExpensesByDate(date.toString())).thenReturn(flowOf(expenseEntities))

        // When
        val result = expenseRepository.getExpensesByDate(date)

        // Then
        val expenses = result.first()
        assertEquals(1, expenses.size)
        assertEquals("Test 1", expenses[0].title)
        assertEquals(100.0, expenses[0].amount, 0.01)
        assertEquals(ExpenseCategory.FOOD, expenses[0].category)
    }

    @Test
    fun `getTotalAmountForDate should return total amount`() = runTest {
        // Given
        val date = LocalDate.now()
        val expectedTotal = 300.0

        `when`(mockExpenseDao.getExpenseAmountByDate(date.toString())).thenReturn(expectedTotal)

        // When
        val result = expenseRepository.getTotalAmountForDate(date)

        // Then
        val total = result.first()
        assertEquals(expectedTotal, total)
    }

    @Test
    fun `getExpenseCountForDate should return expense count`() = runTest {
        // Given
        val date = LocalDate.now()
        val expectedCount = 3

        `when`(mockExpenseDao.getExpenseCountByDate(date.toString())).thenReturn(expectedCount)

        // When
        val result = expenseRepository.getExpenseCountForDate(date)

        // Then
        val count = result.first()
        assertEquals(expectedCount, count)
    }
}
