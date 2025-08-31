package com.avi.smartdailyexpensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avi.smartdailyexpensetracker.data.service.IntelligentInsightsService
import com.avi.smartdailyexpensetracker.data.service.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class IntelligentInsightsViewModel(
    private val intelligentInsightsService: IntelligentInsightsService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(IntelligentInsightsUiState())
    val uiState: StateFlow<IntelligentInsightsUiState> = _uiState.asStateFlow()
    
    private val _actionableInsights = MutableStateFlow<ActionableInsights?>(null)
    val actionableInsights: StateFlow<ActionableInsights?> = _actionableInsights.asStateFlow()
    
    private val _periodicInsights = MutableStateFlow<List<PeriodicInsights>>(emptyList())
    val periodicInsights: StateFlow<List<PeriodicInsights>> = _periodicInsights.asStateFlow()
    
    private val _personalizedInsights = MutableStateFlow<PersonalizedInsights?>(null)
    val personalizedInsights: StateFlow<PersonalizedInsights?> = _personalizedInsights.asStateFlow()
    
    init {
        loadInsights()
    }
    
    fun loadInsights() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Load actionable insights for last 30 days
                val endDate = LocalDate.now()
                val startDate = endDate.minusDays(30)
                
                val actionable = intelligentInsightsService.generateActionableInsights(startDate, endDate)
                _actionableInsights.value = actionable
                
                // Load periodic insights for different time periods
                val periodic = listOf(
                    intelligentInsightsService.generatePeriodicInsights(InsightPeriod.WEEK),
                    intelligentInsightsService.generatePeriodicInsights(InsightPeriod.MONTH),
                    intelligentInsightsService.generatePeriodicInsights(InsightPeriod.QUARTER)
                )
                _periodicInsights.value = periodic
                
                // Load personalized insights
                val personalized = intelligentInsightsService.generatePersonalizedInsights()
                _personalizedInsights.value = personalized
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load insights"
                )
            }
        }
    }
    
    fun refreshInsights() {
        loadInsights()
    }
    
    fun onActionClick(action: ImmediateAction) {
        viewModelScope.launch {
            // Handle action click - could trigger notifications, reminders, etc.
            _uiState.value = _uiState.value.copy(
                lastActionTaken = action.title
            )
        }
    }
    
    fun onRecommendationClick(recommendation: BusinessRecommendation) {
        viewModelScope.launch {
            // Handle recommendation click - could show detailed implementation guide
            _uiState.value = _uiState.value.copy(
                lastRecommendationViewed = recommendation.title
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearLastAction() {
        _uiState.value = _uiState.value.copy(lastActionTaken = null)
    }
    
    fun clearLastRecommendation() {
        _uiState.value = _uiState.value.copy(lastRecommendationViewed = null)
    }
}

data class IntelligentInsightsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastActionTaken: String? = null,
    val lastRecommendationViewed: String? = null
)
