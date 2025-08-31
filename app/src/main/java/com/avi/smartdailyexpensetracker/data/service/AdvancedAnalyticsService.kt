package com.avi.smartdailyexpensetracker.data.service

import com.avi.smartdailyexpensetracker.data.dao.ExpenseDao
import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity
import com.avi.smartdailyexpensetracker.data.dao.CategorySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.sqrt

class AdvancedAnalyticsService(
    private val expenseDao: ExpenseDao
) {
    
    // Spending Pattern Analysis
    suspend fun analyzeSpendingPatterns(
        startDate: LocalDate,
        endDate: LocalDate
    ): SpendingPatternAnalysis {
        val expenses = expenseDao.getExpensesByDateRange(startDate.toString(), endDate.toString())
            .first()
        
        return SpendingPatternAnalysis(
            totalSpent = expenses.sumOf { it.amount },
            averageDailySpending = expenses.groupBy { it.date }
                .mapValues { it.value.sumOf { expense -> expense.amount } }
                .values.toList().average(),
            spendingTrend = calculateSpendingTrend(expenses),
            peakSpendingDays = findPeakSpendingDays(expenses),
            lowSpendingDays = findLowSpendingDays(expenses),
            spendingVolatility = calculateSpendingVolatility(expenses),
            categoryDistribution = analyzeCategoryDistribution(expenses),
            timeBasedPatterns = analyzeTimeBasedPatterns(expenses),
            anomalyDetection = detectSpendingAnomalies(expenses)
        )
    }
    
    // Business Intelligence Insights
    suspend fun generateBusinessInsights(
        startDate: LocalDate,
        endDate: LocalDate
    ): BusinessInsights {
        val expenses = expenseDao.getExpensesByDateRange(startDate.toString(), endDate.toString())
            .first()
        
        return BusinessInsights(
            cashFlowAnalysis = analyzeCashFlow(expenses),
            costOptimization = generateCostOptimizationTips(expenses),
            budgetRecommendations = generateBudgetRecommendations(expenses),
            seasonalTrends = analyzeSeasonalTrends(expenses),
            vendorAnalysis = analyzeVendorPatterns(expenses),
            roiInsights = calculateROIInsights(expenses),
            riskAssessment = assessFinancialRisk(expenses),
            growthProjections = projectGrowthTrends(expenses)
        )
    }
    
    // Predictive Analytics
    suspend fun generatePredictiveInsights(
        historicalDays: Int = 90
    ): PredictiveInsights {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(historicalDays.toLong())
        
        val expenses = expenseDao.getExpensesByDateRange(startDate.toString(), endDate.toString())
            .first()
        
        return PredictiveInsights(
            nextMonthProjection = projectNextMonthSpending(expenses),
            categoryForecasts = forecastCategorySpending(expenses),
            seasonalPredictions = predictSeasonalPatterns(expenses),
            budgetAlerts = generateBudgetAlerts(expenses),
            trendPredictions = predictTrends(expenses),
            anomalyPredictions = predictAnomalies(expenses)
        )
    }
    
    // Advanced Statistical Analysis
    private fun calculateSpendingTrend(expenses: List<ExpenseEntity>): SpendingTrend {
        if (expenses.size < 2) return SpendingTrend.STABLE
        
        val dailyTotals = expenses.groupBy { it.date }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedBy { it.first }
        
        val values = dailyTotals.map { it.second }
        val trend = calculateLinearTrend(values)
        
        return when {
            trend > 0.1 -> SpendingTrend.INCREASING
            trend < -0.1 -> SpendingTrend.DECREASING
            else -> SpendingTrend.STABLE
        }
    }
    
    private fun calculateLinearTrend(values: List<Double>): Double {
        if (values.size < 2) return 0.0
        
        val n = values.size
        val sumX = (0 until n).sum()
        val sumY = values.sum()
        val sumXY = (0 until n).mapIndexed { index, _ -> index * values[index] }.sum()
        val sumXX = (0 until n).map { it * it }.sum()
        
        val slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)
        return slope
    }
    
    private fun findPeakSpendingDays(expenses: List<ExpenseEntity>): List<PeakSpendingDay> {
        val dailyTotals = expenses.groupBy { it.date }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedByDescending { it.second }
        
        val average = dailyTotals.map { it.second }.average()
        val threshold = average * 1.5
        
        return dailyTotals.take(5)
            .filter { it.second > threshold }
            .map { PeakSpendingDay(date = it.first, amount = it.second, reason = analyzePeakReason(it.first, expenses)) }
    }
    
    private fun findLowSpendingDays(expenses: List<ExpenseEntity>): List<LowSpendingDay> {
        val dailyTotals = expenses.groupBy { it.date }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedBy { it.second }
        
        val average = dailyTotals.map { it.second }.average()
        val threshold = average * 0.5
        
        return dailyTotals.take(5)
            .filter { it.second < threshold }
            .map { LowSpendingDay(date = it.first, amount = it.second, reason = analyzeLowReason(it.first, expenses)) }
    }
    
    private fun calculateSpendingVolatility(expenses: List<ExpenseEntity>): Double {
        val dailyTotals = expenses.groupBy { it.date }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .values.toList()
        
        if (dailyTotals.size < 2) return 0.0
        
        val mean = dailyTotals.average()
        val variance = dailyTotals.map { (it - mean) * (it - mean) }.average()
        return sqrt(variance)
    }
    
    private fun analyzeCategoryDistribution(expenses: List<ExpenseEntity>): CategoryDistributionAnalysis {
        val categoryTotals = expenses.groupBy { it.category }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedByDescending { it.second }
        
        val totalSpent = categoryTotals.sumOf { it.second }
        
        return CategoryDistributionAnalysis(
            topCategories = categoryTotals.take(3).map { 
                CategoryInsight(
                    category = it.first,
                    amount = it.second,
                    percentage = (it.second / totalSpent) * 100,
                    trend = calculateCategoryTrend(it.first, expenses)
                )
            },
            categoryEfficiency = calculateCategoryEfficiency(categoryTotals),
            categoryCorrelations = findCategoryCorrelations(expenses)
        )
    }
    
    private fun analyzeTimeBasedPatterns(expenses: List<ExpenseEntity>): TimeBasedPatterns {
        val hourlyPatterns = expenses.groupBy { it.timestamp.hour }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedBy { it.first }
        
        val weeklyPatterns = expenses.groupBy { it.timestamp.dayOfWeek }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedBy { it.first.value }
        
        val monthlyPatterns = expenses.groupBy { it.timestamp.month }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedBy { it.first.value }
        
        return TimeBasedPatterns(
            peakHours = hourlyPatterns.maxByOrNull { it.second }?.first ?: 0,
            peakDays = weeklyPatterns.maxByOrNull { it.second }?.first ?: null,
            peakMonths = monthlyPatterns.maxByOrNull { it.second }?.first ?: null,
            hourlyDistribution = hourlyPatterns,
            weeklyDistribution = weeklyPatterns,
            monthlyDistribution = monthlyPatterns
        )
    }
    
    private fun detectSpendingAnomalies(expenses: List<ExpenseEntity>): List<SpendingAnomaly> {
        val anomalies = mutableListOf<SpendingAnomaly>()
        
        val dailyTotals = expenses.groupBy { it.date }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedBy { it.first }
        
        if (dailyTotals.size < 3) return anomalies
        
        val amounts = dailyTotals.map { it.second }
        val mean = amounts.average()
        val stdDev = sqrt(amounts.map { (it - mean) * (it - mean) }.average())
        
        dailyTotals.forEach { (date, amount) ->
            val zScore = abs((amount - mean) / stdDev)
            if (zScore > 2.0) { // 2 standard deviations
                anomalies.add(
                    SpendingAnomaly(
                        date = date,
                        amount = amount,
                        severity = when {
                            zScore > 3.0 -> AnomalySeverity.HIGH
                            zScore > 2.5 -> AnomalySeverity.MEDIUM
                            else -> AnomalySeverity.LOW
                        },
                        description = generateAnomalyDescription(date, amount, mean, zScore)
                    )
                )
            }
        }
        
        return anomalies
    }
    
    // Helper methods for analysis
    private fun analyzePeakReason(date: String, expenses: List<ExpenseEntity>): String {
        val dayExpenses = expenses.filter { it.date == date }
        val topCategory = dayExpenses.groupBy { it.category }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .maxByOrNull { it.value }
        
        return when {
            topCategory?.value ?: 0.0 > 1000 -> "High-value ${topCategory?.key?.lowercase()} expenses"
            dayExpenses.size > 10 -> "Multiple small expenses"
            else -> "Unusual spending pattern"
        }
    }
    
    private fun analyzeLowReason(date: String, expenses: List<ExpenseEntity>): String {
        val dayExpenses = expenses.filter { it.date == date }
        return when {
            dayExpenses.isEmpty() -> "No expenses recorded"
            dayExpenses.size == 1 -> "Single minimal expense"
            else -> "Below-average spending"
        }
    }
    
    private fun calculateCategoryTrend(category: String, expenses: List<ExpenseEntity>): CategoryTrend {
        val categoryExpenses = expenses.filter { it.category == category }
        if (categoryExpenses.size < 2) return CategoryTrend.STABLE
        
        val dailyTotals = categoryExpenses.groupBy { it.date }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedBy { it.first }
        
        val trend = calculateLinearTrend(dailyTotals.map { it.second })
        
        return when {
            trend > 0.05 -> CategoryTrend.INCREASING
            trend < -0.05 -> CategoryTrend.DECREASING
            else -> CategoryTrend.STABLE
        }
    }
    
    private fun calculateCategoryEfficiency(categoryTotals: List<Pair<String, Double>>): Double {
        if (categoryTotals.size < 2) return 1.0
        
        val total = categoryTotals.sumOf { it.second }
        val idealDistribution = total / categoryTotals.size
        
        val variance = categoryTotals.map { (it.second - idealDistribution) * (it.second - idealDistribution) }.average()
        val efficiency = 1.0 - (sqrt(variance) / idealDistribution)
        
        return efficiency.coerceIn(0.0, 1.0)
    }
    
    private fun findCategoryCorrelations(expenses: List<ExpenseEntity>): List<CategoryCorrelation> {
        val correlations = mutableListOf<CategoryCorrelation>()
        val categories = expenses.map { it.category }.distinct()
        
        for (i in 0 until categories.size - 1) {
            for (j in i + 1 until categories.size) {
                val cat1 = categories[i]
                val cat2 = categories[j]
                
                val correlation = calculateCorrelation(
                    expenses.filter { it.category == cat1 }.map { it.amount },
                    expenses.filter { it.category == cat2 }.map { it.amount }
                )
                
                if (abs(correlation) > 0.3) {
                    correlations.add(
                        CategoryCorrelation(
                            category1 = cat1,
                            category2 = cat2,
                            correlation = correlation,
                            strength = when {
                                abs(correlation) > 0.7 -> CorrelationStrength.STRONG
                                abs(correlation) > 0.5 -> CorrelationStrength.MEDIUM
                                else -> CorrelationStrength.WEAK
                            }
                        )
                    )
                }
            }
        }
        
        return correlations
    }
    
    private fun calculateCorrelation(values1: List<Double>, values2: List<Double>): Double {
        if (values1.size != values2.size || values1.size < 2) return 0.0
        
        val mean1 = values1.average()
        val mean2 = values2.average()
        
        val numerator = values1.zip(values2).sumOf { (v1, v2) -> (v1 - mean1) * (v2 - mean2) }
        val denominator1 = sqrt(values1.sumOf { (it - mean1) * (it - mean1) })
        val denominator2 = sqrt(values2.sumOf { (it - mean2) * (it - mean2) })
        
        return if (denominator1 * denominator2 == 0.0) 0.0 else numerator / (denominator1 * denominator2)
    }
    
    // Additional analysis methods
    private fun analyzeCashFlow(expenses: List<ExpenseEntity>): CashFlowAnalysis {
        // Implementation for cash flow analysis
        return CashFlowAnalysis(
            positiveFlow = 0.0, // Placeholder
            negativeFlow = expenses.sumOf { it.amount },
            netFlow = -expenses.sumOf { it.amount },
            cashFlowTrend = CashFlowTrend.DECREASING
        )
    }
    
    private fun generateCostOptimizationTips(expenses: List<ExpenseEntity>): List<CostOptimizationTip> {
        val tips = mutableListOf<CostOptimizationTip>()
        
        // Analyze high-spending categories
        val categoryTotals = expenses.groupBy { it.category }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedByDescending { it.second }
        
        categoryTotals.take(3).forEach { (category, amount) ->
            tips.add(
                CostOptimizationTip(
                    category = category,
                    currentSpending = amount,
                    potentialSavings = amount * 0.15, // Assume 15% savings potential
                    recommendation = "Review ${category.lowercase()} expenses for optimization opportunities"
                )
            )
        }
        
        return tips
    }
    
    private fun generateBudgetRecommendations(expenses: List<ExpenseEntity>): List<BudgetRecommendation> {
        val recommendations = mutableListOf<BudgetRecommendation>()
        
        val totalSpent = expenses.sumOf { it.amount }
        val averageDaily = expenses.groupBy { it.date }.mapValues { it.value.sumOf { expense -> expense.amount } }.values.average()
        
        recommendations.add(
            BudgetRecommendation(
                category = "Overall",
                currentBudget = totalSpent,
                recommendedBudget = totalSpent * 0.9, // 10% reduction
                reasoning = "Based on historical spending patterns and optimization opportunities"
            )
        )
        
        return recommendations
    }
    
    private fun analyzeSeasonalTrends(expenses: List<ExpenseEntity>): SeasonalTrends {
        // Implementation for seasonal analysis
        return SeasonalTrends(
            peakSeason = "Q4",
            lowSeason = "Q1",
            seasonalVariation = 0.25
        )
    }
    
    private fun analyzeVendorPatterns(expenses: List<ExpenseEntity>): VendorAnalysis {
        // Implementation for vendor analysis
        return VendorAnalysis(
            topVendors = emptyList(),
            vendorConcentration = 0.0
        )
    }
    
    private fun calculateROIInsights(expenses: List<ExpenseEntity>): ROIInsights {
        // Implementation for ROI analysis
        return ROIInsights(
            totalInvestment = expenses.sumOf { it.amount },
            estimatedReturns = 0.0,
            roiPercentage = 0.0
        )
    }
    
    private fun assessFinancialRisk(expenses: List<ExpenseEntity>): RiskAssessment {
        val volatility = calculateSpendingVolatility(expenses)
        val riskLevel = when {
            volatility > 1000 -> RiskLevel.HIGH
            volatility > 500 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
        
        return RiskAssessment(
            riskLevel = riskLevel,
            volatility = volatility,
            riskFactors = listOf("High spending volatility", "Unpredictable patterns")
        )
    }
    
    private fun projectGrowthTrends(expenses: List<ExpenseEntity>): GrowthProjections {
        // Implementation for growth projections
        return GrowthProjections(
            nextMonthProjection = 0.0,
            growthRate = 0.0,
            confidence = 0.0
        )
    }
    
    // Predictive analytics methods
    private fun projectNextMonthSpending(expenses: List<ExpenseEntity>): Double {
        val dailyTotals = expenses.groupBy { it.date }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .values.toList()
        
        if (dailyTotals.size < 7) return 0.0
        
        val trend = calculateLinearTrend(dailyTotals)
        val average = dailyTotals.average()
        
        return average * 30 * (1 + trend)
    }
    
    private fun forecastCategorySpending(expenses: List<ExpenseEntity>): List<CategoryForecast> {
        val forecasts = mutableListOf<CategoryForecast>()
        
        val categories = expenses.map { it.category }.distinct()
        categories.forEach { category ->
            val categoryExpenses = expenses.filter { it.category == category }
            val trend = calculateCategoryTrend(category, categoryExpenses)
            val currentAverage = categoryExpenses.map { it.amount }.average()
            
            val projectedAmount = when (trend) {
                CategoryTrend.INCREASING -> currentAverage * 1.1
                CategoryTrend.DECREASING -> currentAverage * 0.9
                CategoryTrend.STABLE -> currentAverage
            }
            
            forecasts.add(
                CategoryForecast(
                    category = category,
                    currentAverage = currentAverage,
                    projectedAmount = projectedAmount,
                    trend = trend,
                    confidence = 0.8
                )
            )
        }
        
        return forecasts
    }
    
    private fun predictSeasonalPatterns(expenses: List<ExpenseEntity>): SeasonalPredictions {
        // Implementation for seasonal predictions
        return SeasonalPredictions(
            nextQuarterProjection = 0.0,
            seasonalFactors = emptyList()
        )
    }
    
    private fun generateBudgetAlerts(expenses: List<ExpenseEntity>): List<BudgetAlert> {
        val alerts = mutableListOf<BudgetAlert>()
        
        val totalSpent = expenses.sumOf { it.amount }
        val averageDaily = expenses.groupBy { it.date }.mapValues { it.value.sumOf { expense -> expense.amount } }.values.average()
        
        if (totalSpent > 10000) {
            alerts.add(
                BudgetAlert(
                    type = AlertType.BUDGET_EXCEEDED,
                    message = "Total spending has exceeded ₹10,000",
                    severity = AlertSeverity.HIGH,
                    recommendation = "Review expenses and implement cost controls"
                )
            )
        }
        
        if (averageDaily > 500) {
            alerts.add(
                BudgetAlert(
                    type = AlertType.HIGH_DAILY_SPENDING,
                    message = "Daily spending average is ₹${String.format("%.2f", averageDaily)}",
                    severity = AlertSeverity.MEDIUM,
                    recommendation = "Monitor daily spending patterns"
                )
            )
        }
        
        return alerts
    }
    
    private fun predictTrends(expenses: List<ExpenseEntity>): List<TrendPrediction> {
        val predictions = mutableListOf<TrendPrediction>()
        
        val spendingTrend = calculateSpendingTrend(expenses)
        predictions.add(
            TrendPrediction(
                metric = "Overall Spending",
                currentValue = expenses.sumOf { it.amount },
                predictedValue = projectNextMonthSpending(expenses),
                trend = spendingTrend,
                confidence = 0.75
            )
        )
        
        return predictions
    }
    
    private fun predictAnomalies(expenses: List<ExpenseEntity>): List<AnomalyPrediction> {
        val predictions = mutableListOf<AnomalyPrediction>()
        
        val volatility = calculateSpendingVolatility(expenses)
        if (volatility > 800) {
            predictions.add(
                AnomalyPrediction(
                    type = "High Volatility",
                    probability = 0.7,
                    impact = "Unpredictable spending patterns",
                    recommendation = "Implement stricter budget controls"
                )
            )
        }
        
        return predictions
    }
    
    private fun generateAnomalyDescription(date: String, amount: Double, mean: Double, zScore: Double): String {
        return when {
            amount > mean * 2 -> "Extremely high spending day"
            amount > mean * 1.5 -> "High spending day"
            amount < mean * 0.5 -> "Unusually low spending day"
            else -> "Spending deviation from normal pattern"
        }
    }
}

// Data classes for analytics results
data class SpendingPatternAnalysis(
    val totalSpent: Double,
    val averageDailySpending: Double,
    val spendingTrend: SpendingTrend,
    val peakSpendingDays: List<PeakSpendingDay>,
    val lowSpendingDays: List<LowSpendingDay>,
    val spendingVolatility: Double,
    val categoryDistribution: CategoryDistributionAnalysis,
    val timeBasedPatterns: TimeBasedPatterns,
    val anomalyDetection: List<SpendingAnomaly>
)

data class BusinessInsights(
    val cashFlowAnalysis: CashFlowAnalysis,
    val costOptimization: List<CostOptimizationTip>,
    val budgetRecommendations: List<BudgetRecommendation>,
    val seasonalTrends: SeasonalTrends,
    val vendorAnalysis: VendorAnalysis,
    val roiInsights: ROIInsights,
    val riskAssessment: RiskAssessment,
    val growthProjections: GrowthProjections
)

data class PredictiveInsights(
    val nextMonthProjection: Double,
    val categoryForecasts: List<CategoryForecast>,
    val seasonalPredictions: SeasonalPredictions,
    val budgetAlerts: List<BudgetAlert>,
    val trendPredictions: List<TrendPrediction>,
    val anomalyPredictions: List<AnomalyPrediction>
)

// Supporting data classes
enum class SpendingTrend { INCREASING, DECREASING, STABLE }
enum class CategoryTrend { INCREASING, DECREASING, STABLE }
enum class CorrelationStrength { WEAK, MEDIUM, STRONG }
enum class AnomalySeverity { LOW, MEDIUM, HIGH }
enum class RiskLevel { LOW, MEDIUM, HIGH }
enum class AlertType { BUDGET_EXCEEDED, HIGH_DAILY_SPENDING, CATEGORY_OVERSPENDING }
enum class AlertSeverity { LOW, MEDIUM, HIGH }
enum class CashFlowTrend { INCREASING, DECREASING, STABLE }

data class PeakSpendingDay(
    val date: String,
    val amount: Double,
    val reason: String
)

data class LowSpendingDay(
    val date: String,
    val amount: Double,
    val reason: String
)

data class SpendingAnomaly(
    val date: String,
    val amount: Double,
    val severity: AnomalySeverity,
    val description: String
)

data class CategoryDistributionAnalysis(
    val topCategories: List<CategoryInsight>,
    val categoryEfficiency: Double,
    val categoryCorrelations: List<CategoryCorrelation>
)

data class CategoryInsight(
    val category: String,
    val amount: Double,
    val percentage: Double,
    val trend: CategoryTrend
)

data class CategoryCorrelation(
    val category1: String,
    val category2: String,
    val correlation: Double,
    val strength: CorrelationStrength
)

data class TimeBasedPatterns(
    val peakHours: Int,
    val peakDays: java.time.DayOfWeek?,
    val peakMonths: java.time.Month?,
    val hourlyDistribution: List<Pair<Int, Double>>,
    val weeklyDistribution: List<Pair<java.time.DayOfWeek, Double>>,
    val monthlyDistribution: List<Pair<java.time.Month, Double>>
)

data class CashFlowAnalysis(
    val positiveFlow: Double,
    val negativeFlow: Double,
    val netFlow: Double,
    val cashFlowTrend: CashFlowTrend
)

data class CostOptimizationTip(
    val category: String,
    val currentSpending: Double,
    val potentialSavings: Double,
    val recommendation: String
)

data class BudgetRecommendation(
    val category: String,
    val currentBudget: Double,
    val recommendedBudget: Double,
    val reasoning: String
)

data class SeasonalTrends(
    val peakSeason: String,
    val lowSeason: String,
    val seasonalVariation: Double
)

data class VendorAnalysis(
    val topVendors: List<String>,
    val vendorConcentration: Double
)

data class ROIInsights(
    val totalInvestment: Double,
    val estimatedReturns: Double,
    val roiPercentage: Double
)

data class RiskAssessment(
    val riskLevel: RiskLevel,
    val volatility: Double,
    val riskFactors: List<String>
)

data class GrowthProjections(
    val nextMonthProjection: Double,
    val growthRate: Double,
    val confidence: Double
)

data class CategoryForecast(
    val category: String,
    val currentAverage: Double,
    val projectedAmount: Double,
    val trend: CategoryTrend,
    val confidence: Double
)

data class SeasonalPredictions(
    val nextQuarterProjection: Double,
    val seasonalFactors: List<String>
)

data class BudgetAlert(
    val type: AlertType,
    val message: String,
    val severity: AlertSeverity,
    val recommendation: String
)

data class TrendPrediction(
    val metric: String,
    val currentValue: Double,
    val predictedValue: Double,
    val trend: SpendingTrend,
    val confidence: Double
)

data class AnomalyPrediction(
    val type: String,
    val probability: Double,
    val impact: String,
    val recommendation: String
)
