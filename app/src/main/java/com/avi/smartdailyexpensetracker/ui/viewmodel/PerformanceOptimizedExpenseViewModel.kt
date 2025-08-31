package com.avi.smartdailyexpensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity
import com.avi.smartdailyexpensetracker.data.service.SimplePerformanceService
import com.avi.smartdailyexpensetracker.data.service.SimplePaginationInfo
import com.avi.smartdailyexpensetracker.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerformanceOptimizedExpenseViewModel(
    private val performanceService: SimplePerformanceService
) : BaseViewModel() {
    
    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val expenses: StateFlow<List<ExpenseEntity>> = _expenses.asStateFlow()
    
    private val _paginationInfo = MutableStateFlow<SimplePaginationInfo?>(null)
    val paginationInfo: StateFlow<SimplePaginationInfo?> = _paginationInfo.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private var currentPage = 0
    private val pageSize = SimplePerformanceService.DEFAULT_PAGE_SIZE
    
    init {
        loadExpenses()
    }
    
    fun loadExpenses() {
        launchWithCleanup {
            setLoading(true)
            try {
                val result = performanceService.getExpensesPaginated(pageSize, currentPage * pageSize)
                if (result.error == null) {
                    _expenses.value = result.data
                    _paginationInfo.value = result.paginationInfo
                } else {
                    handleError(Exception(result.error))
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                setLoading(false)
            }
        }
    }
    
    fun loadNextPage() {
        val pagination = _paginationInfo.value
        if (pagination?.hasNextPage == true) {
            currentPage++
            loadExpenses()
        }
    }
    
    fun loadPreviousPage() {
        val pagination = _paginationInfo.value
        if (pagination?.hasPreviousPage == true) {
            currentPage = (currentPage - 1).coerceAtLeast(0)
            loadExpenses()
        }
    }
    
    fun searchExpenses(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            currentPage = 0
            loadExpenses()
        } else {
            performSearch(query)
        }
    }
    
    private fun performSearch(query: String) {
        launchWithCleanup {
            setLoading(true)
            try {
                val result = performanceService.searchExpensesPaginated(query, pageSize, 0)
                if (result.error == null) {
                    _expenses.value = result.data
                    _paginationInfo.value = result.paginationInfo
                    currentPage = 0
                } else {
                    handleError(Exception(result.error))
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                setLoading(false)
            }
        }
    }
    
    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
        if (category == null) {
            currentPage = 0
            loadExpenses()
        } else {
            loadExpensesByCategory(category)
        }
    }
    
    private fun loadExpensesByCategory(category: String) {
        launchWithCleanup {
            setLoading(true)
            try {
                val result = performanceService.getExpensesByCategoryPaginated(category, pageSize, 0)
                if (result.error == null) {
                    _expenses.value = result.data
                    _paginationInfo.value = result.paginationInfo
                    currentPage = 0
                } else {
                    handleError(Exception(result.error))
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                setLoading(false)
            }
        }
    }
    
    fun refresh() {
        _isRefreshing.value = true
        currentPage = 0
        loadExpenses()
        _isRefreshing.value = false
    }
    
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        currentPage = 0
        loadExpenses()
    }
    
    fun getExpenseSummaries() {
        launchWithCleanup {
            try {
                val summaries = performanceService.getExpenseSummaries()
                // Handle summaries as needed
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }
}
