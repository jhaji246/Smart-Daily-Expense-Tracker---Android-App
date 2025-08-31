package com.avi.smartdailyexpensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseReport
import com.avi.smartdailyexpensetracker.domain.usecase.GenerateReportUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ExpenseReportState(
    val report: ExpenseReport? = null,
    val startDate: LocalDate = LocalDate.now().minusDays(6L),
    val endDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ExpenseReportEvent {
    data class StartDateChanged(val date: LocalDate) : ExpenseReportEvent()
    data class EndDateChanged(val date: LocalDate) : ExpenseReportEvent()
    object GenerateReport : ExpenseReportEvent()
    object ExportReport : ExpenseReportEvent()
}

class ExpenseReportViewModel(
    private val generateReportUseCase: GenerateReportUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ExpenseReportState())
    val state: StateFlow<ExpenseReportState> = _state.asStateFlow()
    
    init {
        generateReport()
    }
    
    fun onEvent(event: ExpenseReportEvent) {
        when (event) {
            is ExpenseReportEvent.StartDateChanged -> {
                _state.value = _state.value.copy(startDate = event.date)
                if (event.date.isAfter(_state.value.endDate)) {
                    _state.value = _state.value.copy(endDate = event.date)
                }
                generateReport()
            }
            is ExpenseReportEvent.EndDateChanged -> {
                _state.value = _state.value.copy(endDate = event.date)
                if (event.date.isBefore(_state.value.startDate)) {
                    _state.value = _state.value.copy(startDate = event.date)
                }
                generateReport()
            }
            is ExpenseReportEvent.GenerateReport -> {
                generateReport()
            }
            is ExpenseReportEvent.ExportReport -> {
                exportReport()
            }
        }
    }
    
    private fun generateReport() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val currentState = _state.value
                val result = generateReportUseCase(
                    startDate = currentState.startDate,
                    endDate = currentState.endDate
                )
                
                result.fold(
                    onSuccess = { report ->
                        _state.value = currentState.copy(
                            report = report,
                            isLoading = false
                        )
                    },
                    onFailure = { exception ->
                        _state.value = currentState.copy(
                            isLoading = false,
                            error = "Failed to generate report: ${exception.message}"
                        )
                    }
                )
                
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to generate report: ${e.message}"
                )
            }
        }
    }
    
    private fun exportReport() {
        // TODO: Implement export functionality
        // This would typically involve creating a PDF or CSV file
        // and sharing it via Intent
    }
    
    fun getChartData(): List<Pair<String, Double>> {
        val report = _state.value.report ?: return emptyList()
        
        return report.dailyTotals.map { dailyTotal ->
            dailyTotal.date.toString() to dailyTotal.amount
        }
    }
    
    fun getCategoryChartData(): List<Pair<String, Double>> {
        val report = _state.value.report ?: return emptyList()
        
        return report.categoryTotals.map { categoryTotal ->
            categoryTotal.category.displayName to categoryTotal.amount
        }
    }
}
