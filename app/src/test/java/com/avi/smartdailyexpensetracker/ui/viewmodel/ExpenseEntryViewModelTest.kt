package com.avi.smartdailyexpensetracker.ui.viewmodel

import com.avi.smartdailyexpensetracker.domain.entity.ExpenseCategory
import com.avi.smartdailyexpensetracker.domain.usecase.AddExpenseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ExpenseEntryViewModelTest {

    @Mock
    private lateinit var mockAddExpenseUseCase: AddExpenseUseCase

    private lateinit var expenseEntryViewModel: ExpenseEntryViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        expenseEntryViewModel = ExpenseEntryViewModel(mockAddExpenseUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEvent TitleChanged should update title state`() = runTest {
        // Given
        val newTitle = "New Expense Title"

        // When
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.TitleChanged(newTitle))

        // Then
        assertEquals(newTitle, expenseEntryViewModel.state.value.title)
    }

    @Test
    fun `onEvent AmountChanged should update amount state`() = runTest {
        // Given
        val newAmount = "150.75"

        // When
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.AmountChanged(newAmount))

        // Then
        assertEquals(newAmount, expenseEntryViewModel.state.value.amount)
    }

    @Test
    fun `onEvent CategoryChanged should update category state`() = runTest {
        // Given
        val newCategory = ExpenseCategory.TRAVEL

        // When
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.CategoryChanged(newCategory))

        // Then
        assertEquals(newCategory, expenseEntryViewModel.state.value.category)
    }

    @Test
    fun `onEvent NotesChanged should update notes state`() = runTest {
        // Given
        val newNotes = "Updated notes for expense"

        // When
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.NotesChanged(newNotes))

        // Then
        assertEquals(newNotes, expenseEntryViewModel.state.value.notes)
    }

    @Test
    fun `onEvent ReceiptImageChanged should update receipt image path state`() = runTest {
        // Given
        val newPath = "/new/path/to/image.jpg"

        // When
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.ReceiptImageChanged(newPath))

        // Then
        assertEquals(newPath, expenseEntryViewModel.state.value.receiptImagePath)
    }

    @Test
    fun `onEvent SubmitExpense should call use case and update state on success`() = runTest {
        // Given
        val title = "Test Expense"
        val amount = "100.50"
        val category = ExpenseCategory.FOOD
        val notes = "Test notes"
        val receiptImagePath = "/path/to/image.jpg"
        val expectedId = 1L

        expenseEntryViewModel.onEvent(ExpenseEntryEvent.TitleChanged(title))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.AmountChanged(amount))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.CategoryChanged(category))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.NotesChanged(notes))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.ReceiptImageChanged(receiptImagePath))

        `when`(mockAddExpenseUseCase(
            title = title,
            amount = amount,
            category = category,
            notes = notes,
            receiptImagePath = receiptImagePath
        )).thenReturn(Result.success(expectedId))

        // When
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.SubmitExpense)

        // Then
        testDispatcher.scheduler.advanceUntilIdle()
        
        verify(mockAddExpenseUseCase).invoke(title, amount, category, notes, receiptImagePath)
        
        // After successful submission, the form should be reset but we can verify the use case was called
        // The ViewModel resets the state after success, so we check that the form fields are cleared
        assertEquals("", expenseEntryViewModel.state.value.title)
        assertEquals("", expenseEntryViewModel.state.value.amount)
        assertEquals(ExpenseCategory.STAFF, expenseEntryViewModel.state.value.category) // Default category
        assertEquals("", expenseEntryViewModel.state.value.notes)
        assertEquals(null, expenseEntryViewModel.state.value.receiptImagePath)
    }

    @Test
    fun `onEvent SubmitExpense should handle validation failure for empty title`() = runTest {
        // Given
        val title = "" // Invalid empty title
        val amount = "100.50"
        val category = ExpenseCategory.FOOD

        expenseEntryViewModel.onEvent(ExpenseEntryEvent.TitleChanged(title))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.AmountChanged(amount))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.CategoryChanged(category))

        // When
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.SubmitExpense)

        // Then
        testDispatcher.scheduler.advanceUntilIdle()
        
        verifyNoInteractions(mockAddExpenseUseCase)
        assertEquals("Title is required", expenseEntryViewModel.state.value.error)
    }

    @Test
    fun `onEvent SubmitExpense should handle validation failure for invalid amount`() = runTest {
        // Given
        val title = "Test Expense"
        val amount = "0.0" // Invalid amount
        val category = ExpenseCategory.FOOD

        expenseEntryViewModel.onEvent(ExpenseEntryEvent.TitleChanged(title))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.AmountChanged(amount))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.CategoryChanged(category))

        // When
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.SubmitExpense)

        // Then
        testDispatcher.scheduler.advanceUntilIdle()
        
        verifyNoInteractions(mockAddExpenseUseCase)
        assertEquals("Please enter a valid amount", expenseEntryViewModel.state.value.error)
    }

    @Test
    fun `onEvent SubmitExpense should handle use case failure`() = runTest {
        // Given
        val title = "Test Expense"
        val amount = "100.50"
        val category = ExpenseCategory.FOOD
        val errorMessage = "Database error"

        expenseEntryViewModel.onEvent(ExpenseEntryEvent.TitleChanged(title))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.AmountChanged(amount))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.CategoryChanged(category))

        // Mock the use case to return failure
        // The ViewModel uses takeIf { it.isNotBlank() } for notes, so empty string becomes null
        `when`(mockAddExpenseUseCase(
            title = title,
            amount = amount,
            category = category,
            notes = null, // Empty string becomes null due to takeIf { it.isNotBlank() }
            receiptImagePath = null
        )).thenReturn(Result.failure(Exception(errorMessage)))

        // When
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.SubmitExpense)

        // Then
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verify the use case was called with the correct parameters
        verify(mockAddExpenseUseCase).invoke(title, amount, category, null, null)
        assertTrue(expenseEntryViewModel.state.value.error?.contains("Failed to save expense") == true)
    }

    @Test
    fun `onEvent ResetState should clear form fields`() = runTest {
        // Given
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.TitleChanged("Test Title"))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.AmountChanged("100.50"))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.CategoryChanged(ExpenseCategory.FOOD))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.NotesChanged("Test notes"))
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.ReceiptImageChanged("/path/to/image.jpg"))

        // When
        expenseEntryViewModel.onEvent(ExpenseEntryEvent.ResetState)

        // Then
        assertEquals("", expenseEntryViewModel.state.value.title)
        assertEquals("", expenseEntryViewModel.state.value.amount)
        assertEquals(ExpenseCategory.STAFF, expenseEntryViewModel.state.value.category) // Default category
        assertEquals("", expenseEntryViewModel.state.value.notes)
        assertEquals(null, expenseEntryViewModel.state.value.receiptImagePath)
    }
}
