package com.avi.smartdailyexpensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

abstract class BaseViewModel : ViewModel() {
    
    // Memory optimization: Use weak references and proper cleanup
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Coroutine management for memory leak prevention
    private val activeJobs = mutableSetOf<Job>()
    
    // Exception handler for better error management
    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }
    
    // Protected launch function with automatic cleanup
    protected fun launchWithCleanup(
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(exceptionHandler) {
            block()
        }.also { job ->
            activeJobs.add(job)
            job.invokeOnCompletion {
                activeJobs.remove(job)
            }
        }
    }
    
    // Supervisor scope for independent coroutines
    protected fun launchSupervisor(
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(exceptionHandler) {
            supervisorScope {
                block()
            }
        }.also { job ->
            activeJobs.add(job)
            job.invokeOnCompletion {
                activeJobs.remove(job)
            }
        }
    }
    
    // Loading state management
    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
    
    // Error handling
    protected fun handleError(throwable: Throwable) {
        _error.value = throwable.message ?: "An unexpected error occurred"
        setLoading(false)
    }
    
    // Clear error
    fun clearError() {
        _error.value = null
    }
    
    // Memory cleanup
    override fun onCleared() {
        super.onCleared()
        // Cancel all active jobs to prevent memory leaks
        activeJobs.forEach { it.cancel() }
        activeJobs.clear()
    }
    
    // Resource management
    protected fun <T> manageResource(
        resource: T,
        cleanup: (T) -> Unit
    ) {
        addCloseable { cleanup(resource) }
    }
}
