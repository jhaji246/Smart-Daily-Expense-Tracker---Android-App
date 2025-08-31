package com.avi.smartdailyexpensetracker.data.service

import com.avi.smartdailyexpensetracker.data.dao.ExpenseDao
import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.first

class IntelligentInsightsService(
    private val expenseDao: ExpenseDao
) {
    
    // Generate actionable insights
    suspend fun generateActionableInsights(
        startDate: LocalDate,
        endDate: LocalDate
    ): ActionableInsights {
        val expenses = expenseDao.getExpensesByDateRange(startDate.toString(), endDate.toString())
            .first()
        
        return ActionableInsights(
            immediateActions = generateImmediateActions(expenses),
            costSavings = calculateCostSavings(expenses),
            budgetOptimization = generateBudgetOptimization(expenses),
            spendingHabits = analyzeSpendingHabits(expenses),
            businessRecommendations = generateBusinessRecommendations(expenses),
            riskMitigation = identifyRiskMitigation(expenses),
            growthOpportunities = identifyGrowthOpportunities(expenses),
            efficiencyImprovements = suggestEfficiencyImprovements(expenses)
        )
    }
    
    // Generate insights for specific time periods
    suspend fun generatePeriodicInsights(
        period: InsightPeriod
    ): PeriodicInsights {
        val endDate = LocalDate.now()
        val startDate = when (period) {
            InsightPeriod.WEEK -> endDate.minusWeeks(1)
            InsightPeriod.MONTH -> endDate.minusMonths(1)
            InsightPeriod.QUARTER -> endDate.minusMonths(3)
            InsightPeriod.YEAR -> endDate.minusYears(1)
        }
        
        val expenses = expenseDao.getExpensesByDateRange(startDate.toString(), endDate.toString())
            .first()
        
        return PeriodicInsights(
            period = period,
            startDate = startDate,
            endDate = endDate,
            keyMetrics = calculateKeyMetrics(expenses),
            trends = analyzeTrends(expenses),
            insights = generatePeriodicInsights(expenses, period),
            recommendations = generatePeriodicRecommendations(expenses, period)
        )
    }
    
    // Generate personalized insights based on spending patterns
    suspend fun generatePersonalizedInsights(
        userId: String? = null
    ): PersonalizedInsights {
        val endDate = LocalDate.now()
        val startDate = endDate.minusMonths(3)
        
        val expenses = expenseDao.getExpensesByDateRange(startDate.toString(), endDate.toString())
            .first()
        
        return PersonalizedInsights(
            spendingPersonality = analyzeSpendingPersonality(expenses),
            personalizedTips = generatePersonalizedTips(expenses),
            habitFormation = suggestHabitFormation(expenses),
            goalSetting = generateGoalRecommendations(expenses),
            motivationInsights = generateMotivationInsights(expenses)
        )
    }
    
    // Private helper methods - simplified implementations
    private fun generateImmediateActions(expenses: List<ExpenseEntity>): List<ImmediateAction> {
        val actions = mutableListOf<ImmediateAction>()
        
        // Check for high-spending days
        val dailyTotals = expenses.groupBy { it.date }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
        
        val averageDaily = dailyTotals.values.average()
        val highSpendingDays = dailyTotals.filter { it.value > averageDaily * 1.5 }
        
        if (highSpendingDays.isNotEmpty()) {
            actions.add(
                ImmediateAction(
                    type = ActionType.REVIEW_HIGH_SPENDING,
                    priority = ActionPriority.HIGH,
                    title = "Review High Spending Days",
                    description = "You have ${highSpendingDays.size} days with unusually high spending",
                    impact = "Potential savings: ₹${String.format("%.2f", highSpendingDays.values.sum() - (averageDaily * highSpendingDays.size))}",
                    timeToComplete = "15 minutes",
                    steps = listOf(
                        "Review expenses from ${highSpendingDays.keys.first()}",
                        "Identify unnecessary purchases",
                        "Set daily spending limits"
                    )
                )
            )
        }
        
        return actions
    }
    
    private fun calculateCostSavings(expenses: List<ExpenseEntity>): CostSavingsAnalysis {
        val categoryTotals = expenses.groupBy { it.category }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
        
        val totalSpent = categoryTotals.values.sum()
        val potentialSavings = mutableListOf<PotentialSaving>()
        
        // Analyze each category for savings opportunities
        categoryTotals.forEach { (category, amount) ->
            val percentage = (amount / totalSpent) * 100
            val savingsOpportunity = when {
                percentage > 40 -> amount * 0.25 // 25% savings potential
                percentage > 25 -> amount * 0.20 // 20% savings potential
                percentage > 15 -> amount * 0.15 // 15% savings potential
                else -> amount * 0.10 // 10% savings potential
            }
            
            potentialSavings.add(
                PotentialSaving(
                    category = category,
                    currentSpending = amount,
                    potentialSavings = savingsOpportunity,
                    savingsPercentage = (savingsOpportunity / amount) * 100,
                    recommendations = generateCategorySavingsRecommendations(category, amount, percentage)
                )
            )
        }
        
        val totalPotentialSavings = potentialSavings.sumOf { it.potentialSavings }
        
        return CostSavingsAnalysis(
            totalCurrentSpending = totalSpent,
            totalPotentialSavings = totalPotentialSavings,
            savingsPercentage = (totalPotentialSavings / totalSpent) * 100,
            categorySavings = potentialSavings.sortedByDescending { it.potentialSavings }
        )
    }
    
    private fun generateBudgetOptimization(expenses: List<ExpenseEntity>): BudgetOptimization {
        val dailyTotals = expenses.groupBy { it.date }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
        
        val averageDaily = dailyTotals.values.average()
        val recommendedDailyBudget = averageDaily * 0.8 // 20% reduction
        val weeklyBudget = recommendedDailyBudget * 7
        val monthlyBudget = recommendedDailyBudget * 30
        
        return BudgetOptimization(
            currentDailyAverage = averageDaily,
            recommendedDailyBudget = recommendedDailyBudget,
            weeklyBudget = weeklyBudget,
            monthlyBudget = monthlyBudget,
            budgetEfficiency = 0.75, // Placeholder
            optimizationTips = generateBudgetOptimizationTips(expenses, averageDaily, recommendedDailyBudget)
        )
    }
    
    private fun analyzeSpendingHabits(expenses: List<ExpenseEntity>): SpendingHabitsAnalysis {
        return SpendingHabitsAnalysis(
            timePatterns = TimePatterns(14, null, null, emptyList(), emptyList(), emptyList()),
            categoryPatterns = CategoryPatterns(emptyList(), emptyList(), emptyList()),
            amountPatterns = AmountPatterns(expenses.map { it.amount }.average(), emptyList(), emptyList()),
            habitScore = 0.8, // Placeholder
            improvementAreas = listOf("Budget planning", "Expense tracking", "Category management"),
            positiveHabits = listOf("Regular expense recording", "Category organization")
        )
    }
    
    private fun generateBusinessRecommendations(expenses: List<ExpenseEntity>): List<BusinessRecommendation> {
        val recommendations = mutableListOf<BusinessRecommendation>()
        
        val totalSpent = expenses.sumOf { it.amount }
        val expenseCount = expenses.size
        val averageExpense = totalSpent / expenseCount
        
        if (averageExpense > 1000) {
            recommendations.add(
                BusinessRecommendation(
                    type = RecommendationType.COST_CONTROL,
                    priority = RecommendationPriority.HIGH,
                    title = "Implement Cost Control Measures",
                    description = "Average expense amount is ₹${String.format("%.2f", averageExpense)}",
                    rationale = "High average expenses may indicate lack of cost controls",
                    implementation = "Set expense approval workflows and spending limits",
                    expectedOutcome = "20-30% reduction in average expense amount",
                    timeline = "2-4 weeks"
                )
            )
        }
        
        return recommendations
    }
    
    private fun identifyRiskMitigation(expenses: List<ExpenseEntity>): RiskMitigation {
        val risks = mutableListOf<IdentifiedRisk>()
        
        // High spending volatility risk
        val volatility = calculateSpendingVolatility(expenses)
        if (volatility > 1000) {
            risks.add(
                IdentifiedRisk(
                    type = RiskType.HIGH_VOLATILITY,
                    severity = RiskSeverity.HIGH,
                    description = "Spending volatility of ₹${String.format("%.2f", volatility)}",
                    impact = "Unpredictable cash flow and budget overruns",
                    mitigation = "Implement spending controls and regular reviews",
                    probability = 0.8
                )
            )
        }
        
        return RiskMitigation(
            identifiedRisks = risks,
            overallRiskScore = risks.map { it.severity.ordinal * it.probability }.average(),
            mitigationStrategies = risks.map { it.mitigation }
        )
    }
    
    private fun identifyGrowthOpportunities(expenses: List<ExpenseEntity>): GrowthOpportunities {
        val opportunities = mutableListOf<GrowthOpportunity>()
        
        val totalSpent = expenses.sumOf { it.amount }
        val expenseCount = expenses.size
        val averageExpense = totalSpent / expenseCount
        
        if (averageExpense < 500) {
            opportunities.add(
                GrowthOpportunity(
                    type = OpportunityType.EFFICIENCY_IMPROVEMENT,
                    potential = OpportunityPotential.HIGH,
                    title = "Optimize Small Expenses",
                    description = "Average expense amount is ₹${String.format("%.2f", averageExpense)}",
                    rationale = "Many small expenses may indicate inefficiency",
                    implementation = "Consolidate small purchases and negotiate bulk discounts",
                    expectedOutcome = "15-25% reduction in total expenses",
                    timeline = "1-2 months"
                )
            )
        }
        
        return GrowthOpportunities(
            opportunities = opportunities,
            overallPotential = opportunities.map { it.potential.ordinal }.average(),
            implementationRoadmap = opportunities.map { "Implement ${it.title} within ${it.timeline}" }
        )
    }
    
    private fun suggestEfficiencyImprovements(expenses: List<ExpenseEntity>): EfficiencyImprovements {
        val improvements = mutableListOf<EfficiencyImprovement>()
        
        val dailyExpenseCounts = expenses.groupBy { it.date }
            .mapValues { it.value.size }
        
        val averageDailyExpenses = dailyExpenseCounts.values.average()
        if (averageDailyExpenses > 5) {
            improvements.add(
                EfficiencyImprovement(
                    type = ImprovementType.EXPENSE_CONSOLIDATION,
                    impact = ImprovementImpact.HIGH,
                    title = "Consolidate Daily Expenses",
                    description = "Average of ${String.format("%.1f", averageDailyExpenses)} expenses per day",
                    currentState = "Multiple small daily expenses",
                    targetState = "Consolidated weekly or monthly expenses",
                    implementation = "Batch small purchases and reduce transaction frequency",
                    expectedSavings = "10-20% reduction in transaction costs"
                )
            )
        }
        
        return EfficiencyImprovements(
            improvements = improvements,
            overallEfficiency = 0.75, // Placeholder
            priorityOrder = improvements.sortedByDescending { it.impact.ordinal }
        )
    }
    
    // Helper methods
    private fun generateCategorySavingsRecommendations(category: String, amount: Double, percentage: Double): List<String> {
        return when {
            percentage > 40 -> listOf(
                "Negotiate bulk discounts",
                "Review supplier contracts",
                "Implement approval workflows"
            )
            percentage > 25 -> listOf(
                "Set category-specific budgets",
                "Regular cost reviews",
                "Explore alternative suppliers"
            )
            else -> listOf(
                "Monitor spending trends",
                "Set spending alerts",
                "Regular category reviews"
            )
        }
    }
    
    private fun generateBudgetOptimizationTips(expenses: List<ExpenseEntity>, current: Double, recommended: Double): List<String> {
        return listOf(
            "Set daily spending limit of ₹${String.format("%.2f", recommended)}",
            "Review expenses weekly",
            "Use spending alerts",
            "Plan major purchases in advance"
        )
    }
    
    private fun calculateSpendingVolatility(expenses: List<ExpenseEntity>): Double {
        val dailyTotals = expenses.groupBy { it.date }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .values.toList()
        
        if (dailyTotals.size < 2) return 0.0
        
        val mean = dailyTotals.average()
        val variance = dailyTotals.map { (it - mean) * (it - mean) }.average()
        return kotlin.math.sqrt(variance)
    }
    
    private fun calculateKeyMetrics(expenses: List<ExpenseEntity>): KeyMetrics {
        return KeyMetrics(
            totalSpent = expenses.sumOf { it.amount },
            averageDaily = expenses.groupBy { it.date }.mapValues { it.value.sumOf { expense -> expense.amount } }.values.average(),
            expenseCount = expenses.size,
            categoryCount = expenses.map { it.category }.distinct().size
        )
    }
    
    private fun analyzeTrends(expenses: List<ExpenseEntity>): List<Trend> {
        return listOf(
            Trend(
                metric = "Daily Spending",
                direction = TrendDirection.DECREASING,
                strength = TrendStrength.MEDIUM,
                confidence = 0.8
            )
        )
    }
    
    private fun generatePeriodicInsights(expenses: List<ExpenseEntity>, period: InsightPeriod): List<String> {
        return listOf("Spending patterns", "Cost trends", "Budget performance")
    }
    
    private fun generatePeriodicRecommendations(expenses: List<ExpenseEntity>, period: InsightPeriod): List<String> {
        return listOf("Review budget", "Optimize categories", "Plan ahead")
    }
    
    private fun analyzeSpendingPersonality(expenses: List<ExpenseEntity>): SpendingPersonality {
        return SpendingPersonality(
            type = PersonalityType.CAUTIOUS,
            characteristics = listOf("Budget-conscious", "Planned spending"),
            strengths = listOf("Good tracking", "Consistent patterns"),
            areasForImprovement = listOf("Flexibility", "Opportunity recognition")
        )
    }
    
    private fun generatePersonalizedTips(expenses: List<ExpenseEntity>): List<String> {
        return listOf("Set realistic goals", "Track progress", "Celebrate achievements")
    }
    
    private fun suggestHabitFormation(expenses: List<ExpenseEntity>): List<String> {
        return listOf("Daily expense review", "Weekly budget check", "Monthly analysis")
    }
    
    private fun generateGoalRecommendations(expenses: List<ExpenseEntity>): List<String> {
        return listOf("Reduce daily average by 20%", "Optimize top 3 categories", "Improve budget efficiency")
    }
    
    private fun generateMotivationInsights(expenses: List<ExpenseEntity>): List<String> {
        return listOf("You're making progress", "Consistency is key", "Small changes add up")
    }
}

// Data classes for insights
data class ActionableInsights(
    val immediateActions: List<ImmediateAction>,
    val costSavings: CostSavingsAnalysis,
    val budgetOptimization: BudgetOptimization,
    val spendingHabits: SpendingHabitsAnalysis,
    val businessRecommendations: List<BusinessRecommendation>,
    val riskMitigation: RiskMitigation,
    val growthOpportunities: GrowthOpportunities,
    val efficiencyImprovements: EfficiencyImprovements
)

data class PeriodicInsights(
    val period: InsightPeriod,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val keyMetrics: KeyMetrics,
    val trends: List<Trend>,
    val insights: List<String>,
    val recommendations: List<String>
)

data class PersonalizedInsights(
    val spendingPersonality: SpendingPersonality,
    val personalizedTips: List<String>,
    val habitFormation: List<String>,
    val goalSetting: List<String>,
    val motivationInsights: List<String>
)

// Supporting data classes
enum class InsightPeriod { WEEK, MONTH, QUARTER, YEAR }
enum class ActionType { REVIEW_HIGH_SPENDING, OPTIMIZE_CATEGORY, STABILIZE_SPENDING }
enum class ActionPriority { HIGH, MEDIUM, LOW }
enum class RecommendationType { COST_CONTROL, CATEGORY_DIVERSIFICATION, SPENDING_PREDICTABILITY }
enum class RecommendationPriority { HIGH, MEDIUM, LOW }
enum class RiskType { HIGH_VOLATILITY, CATEGORY_CONCENTRATION, HIGH_DAILY_SPENDING }
enum class RiskSeverity { HIGH, MEDIUM, LOW }
enum class OpportunityType { EFFICIENCY_IMPROVEMENT, CATEGORY_EXPANSION }
enum class OpportunityPotential { HIGH, MEDIUM, LOW }
enum class ImprovementType { EXPENSE_CONSOLIDATION, CATEGORY_OPTIMIZATION }
enum class ImprovementImpact { HIGH, MEDIUM, LOW }
enum class PersonalityType { CAUTIOUS, SPONTANEOUS, STRATEGIC, REACTIVE }
enum class TrendDirection { INCREASING, DECREASING, STABLE }
enum class TrendStrength { STRONG, MEDIUM, WEAK }

data class ImmediateAction(
    val type: ActionType,
    val priority: ActionPriority,
    val title: String,
    val description: String,
    val impact: String,
    val timeToComplete: String,
    val steps: List<String>
)

data class CostSavingsAnalysis(
    val totalCurrentSpending: Double,
    val totalPotentialSavings: Double,
    val savingsPercentage: Double,
    val categorySavings: List<PotentialSaving>
)

data class PotentialSaving(
    val category: String,
    val currentSpending: Double,
    val potentialSavings: Double,
    val savingsPercentage: Double,
    val recommendations: List<String>
)

data class BudgetOptimization(
    val currentDailyAverage: Double,
    val recommendedDailyBudget: Double,
    val weeklyBudget: Double,
    val monthlyBudget: Double,
    val budgetEfficiency: Double,
    val optimizationTips: List<String>
)

data class SpendingHabitsAnalysis(
    val timePatterns: TimePatterns,
    val categoryPatterns: CategoryPatterns,
    val amountPatterns: AmountPatterns,
    val habitScore: Double,
    val improvementAreas: List<String>,
    val positiveHabits: List<String>
)

data class BusinessRecommendation(
    val type: RecommendationType,
    val priority: RecommendationPriority,
    val title: String,
    val description: String,
    val rationale: String,
    val implementation: String,
    val expectedOutcome: String,
    val timeline: String
)

data class RiskMitigation(
    val identifiedRisks: List<IdentifiedRisk>,
    val overallRiskScore: Double,
    val mitigationStrategies: List<String>
)

data class IdentifiedRisk(
    val type: RiskType,
    val severity: RiskSeverity,
    val description: String,
    val impact: String,
    val mitigation: String,
    val probability: Double
)

data class GrowthOpportunities(
    val opportunities: List<GrowthOpportunity>,
    val overallPotential: Double,
    val implementationRoadmap: List<String>
)

data class GrowthOpportunity(
    val type: OpportunityType,
    val potential: OpportunityPotential,
    val title: String,
    val description: String,
    val rationale: String,
    val implementation: String,
    val expectedOutcome: String,
    val timeline: String
)

data class EfficiencyImprovements(
    val improvements: List<EfficiencyImprovement>,
    val overallEfficiency: Double,
    val priorityOrder: List<EfficiencyImprovement>
)

data class EfficiencyImprovement(
    val type: ImprovementType,
    val impact: ImprovementImpact,
    val title: String,
    val description: String,
    val currentState: String,
    val targetState: String,
    val implementation: String,
    val expectedSavings: String
)

data class TimePatterns(
    val peakHours: Int,
    val peakDays: java.time.DayOfWeek?,
    val peakMonths: java.time.Month?,
    val hourlyDistribution: List<Pair<Int, Double>>,
    val weeklyDistribution: List<Pair<java.time.DayOfWeek, Double>>,
    val monthlyDistribution: List<Pair<java.time.Month, Double>>
)

data class CategoryPatterns(
    val topCategories: List<String>,
    val categoryTrends: List<String>,
    val correlations: List<String>
)

data class AmountPatterns(
    val averageAmount: Double,
    val amountDistribution: List<String>,
    val outliers: List<String>
)

data class SpendingPersonality(
    val type: PersonalityType,
    val characteristics: List<String>,
    val strengths: List<String>,
    val areasForImprovement: List<String>
)

data class KeyMetrics(
    val totalSpent: Double,
    val averageDaily: Double,
    val expenseCount: Int,
    val categoryCount: Int
)

data class Trend(
    val metric: String,
    val direction: TrendDirection,
    val strength: TrendStrength,
    val confidence: Double
)
