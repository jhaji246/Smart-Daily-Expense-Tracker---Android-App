package com.avi.smartdailyexpensetracker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avi.smartdailyexpensetracker.data.service.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun IntelligentInsightsDashboard(
    actionableInsights: ActionableInsights?,
    periodicInsights: List<PeriodicInsights>?,
    personalizedInsights: PersonalizedInsights?,
    onActionClick: (ImmediateAction) -> Unit,
    onRecommendationClick: (BusinessRecommendation) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Actionable", "Periodic", "Personalized")
    
    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Intelligent Insights",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "AI-powered business recommendations",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
        
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Content
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { it }
                ) + fadeIn(
                    animationSpec = tween(300)
                ) with slideOutVertically(
                    animationSpec = tween(300),
                    targetOffsetY = { -it }
                ) + fadeOut(
                    animationSpec = tween(300)
                )
            }
        ) { tabIndex ->
            when (tabIndex) {
                0 -> ActionableInsightsTab(
                    insights = actionableInsights,
                    onActionClick = onActionClick,
                    onRecommendationClick = onRecommendationClick
                )
                1 -> PeriodicInsightsTab(
                    insights = periodicInsights
                )
                2 -> PersonalizedInsightsTab(
                    insights = personalizedInsights
                )
            }
        }
    }
}

@Composable
fun ActionableInsightsTab(
    insights: ActionableInsights?,
    onActionClick: (ImmediateAction) -> Unit,
    onRecommendationClick: (BusinessRecommendation) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (insights == null) {
            item {
                EmptyInsightsState()
            }
            return@LazyColumn
        }
        
        // Immediate Actions
        item {
            Text(
                text = "Immediate Actions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        items(insights.immediateActions) { action ->
            ImmediateActionCard(
                action = action,
                onClick = { onActionClick(action) }
            )
        }
        
        // Cost Savings
        item {
            CostSavingsCard(insights.costSavings)
        }
        
        // Budget Optimization
        item {
            BudgetOptimizationCard(insights.budgetOptimization)
        }
        
        // Business Recommendations
        item {
            Text(
                text = "Business Recommendations",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        items(insights.businessRecommendations) { recommendation ->
            BusinessRecommendationCard(
                recommendation = recommendation,
                onClick = { onRecommendationClick(recommendation) }
            )
        }
        
        // Risk Mitigation
        item {
            RiskMitigationCard(insights.riskMitigation)
        }
        
        // Growth Opportunities
        item {
            GrowthOpportunitiesCard(insights.growthOpportunities)
        }
        
        // Efficiency Improvements
        item {
            EfficiencyImprovementsCard(insights.efficiencyImprovements)
        }
    }
}

@Composable
fun PeriodicInsightsTab(
    insights: List<PeriodicInsights>?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (insights.isNullOrEmpty()) {
            item {
                EmptyInsightsState()
            }
            return@LazyColumn
        }
        
        items(insights) { periodicInsight ->
            PeriodicInsightCard(periodicInsight)
        }
    }
}

@Composable
fun PersonalizedInsightsTab(
    insights: PersonalizedInsights?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (insights == null) {
            item {
                EmptyInsightsState()
            }
            return@LazyColumn
        }
        
        // Spending Personality
        item {
            SpendingPersonalityCard(insights.spendingPersonality)
        }
        
        // Personalized Tips
        item {
            PersonalizedTipsCard(insights.personalizedTips)
        }
        
        // Habit Formation
        item {
            HabitFormationCard(insights.habitFormation)
        }
        
        // Goal Setting
        item {
            GoalSettingCard(insights.goalSetting)
        }
        
        // Motivation Insights
        item {
            MotivationInsightsCard(insights.motivationInsights)
        }
    }
}

@Composable
fun ImmediateActionCard(
    action: ImmediateAction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (action.priority) {
                ActionPriority.HIGH -> MaterialTheme.colorScheme.errorContainer
                ActionPriority.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
                ActionPriority.LOW -> MaterialTheme.colorScheme.secondaryContainer
            }
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                PriorityBadge(action.priority)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = action.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = action.impact,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = action.timeToComplete,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            action.steps.forEach { step ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun CostSavingsCard(costSavings: CostSavingsAnalysis) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cost Savings Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    label = "Current Spending",
                    value = "₹${String.format("%.2f", costSavings.totalCurrentSpending)}",
                    color = MaterialTheme.colorScheme.error
                )
                MetricItem(
                    label = "Potential Savings",
                    value = "₹${String.format("%.2f", costSavings.totalPotentialSavings)}",
                    color = MaterialTheme.colorScheme.primary
                )
                MetricItem(
                    label = "Savings %",
                    value = "${String.format("%.1f", costSavings.savingsPercentage)}%",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Top Savings Categories:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            costSavings.categorySavings.take(3).forEach { saving ->
                CategorySavingsItem(saving)
            }
        }
    }
}

@Composable
fun BudgetOptimizationCard(budgetOptimization: BudgetOptimization) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Budget Optimization",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    label = "Current Daily",
                    value = "₹${String.format("%.2f", budgetOptimization.currentDailyAverage)}",
                    color = MaterialTheme.colorScheme.error
                )
                MetricItem(
                    label = "Recommended",
                    value = "₹${String.format("%.2f", budgetOptimization.recommendedDailyBudget)}",
                    color = MaterialTheme.colorScheme.primary
                )
                MetricItem(
                    label = "Efficiency",
                    value = "${String.format("%.1f", budgetOptimization.budgetEfficiency * 100)}%",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Budget Breakdown:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            BudgetBreakdownItem("Weekly", budgetOptimization.weeklyBudget)
            BudgetBreakdownItem("Monthly", budgetOptimization.monthlyBudget)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Optimization Tips:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            budgetOptimization.optimizationTips.forEach { tip ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                                    Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun BusinessRecommendationCard(
    recommendation: BusinessRecommendation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (recommendation.priority) {
                RecommendationPriority.HIGH -> MaterialTheme.colorScheme.errorContainer
                RecommendationPriority.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
                RecommendationPriority.LOW -> MaterialTheme.colorScheme.secondaryContainer
            }
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                PriorityBadge(
                    priority = when (recommendation.priority) {
                        RecommendationPriority.HIGH -> ActionPriority.HIGH
                        RecommendationPriority.MEDIUM -> ActionPriority.MEDIUM
                        RecommendationPriority.LOW -> ActionPriority.LOW
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = recommendation.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Rationale: ${recommendation.rationale}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Implementation: ${recommendation.implementation}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Expected: ${recommendation.expectedOutcome}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Timeline: ${recommendation.timeline}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun RiskMitigationCard(riskMitigation: RiskMitigation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Risk Mitigation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    label = "Risks Identified",
                    value = "${riskMitigation.identifiedRisks.size}",
                    color = MaterialTheme.colorScheme.error
                )
                MetricItem(
                    label = "Risk Score",
                    value = String.format("%.1f", riskMitigation.overallRiskScore),
                    color = MaterialTheme.colorScheme.primary
                )
                MetricItem(
                    label = "Mitigation Strategies",
                    value = "${riskMitigation.mitigationStrategies.size}",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            if (riskMitigation.identifiedRisks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Key Risks:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                riskMitigation.identifiedRisks.take(3).forEach { risk ->
                    RiskItem(risk)
                }
            }
        }
    }
}

@Composable
fun GrowthOpportunitiesCard(growthOpportunities: GrowthOpportunities) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Growth Opportunities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    label = "Opportunities",
                    value = "${growthOpportunities.opportunities.size}",
                    color = MaterialTheme.colorScheme.primary
                )
                MetricItem(
                    label = "Overall Potential",
                    value = String.format("%.1f", growthOpportunities.overallPotential),
                    color = MaterialTheme.colorScheme.tertiary
                )
                MetricItem(
                    label = "Implementation Steps",
                    value = "${growthOpportunities.implementationRoadmap.size}",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            if (growthOpportunities.opportunities.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Key Opportunities:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                growthOpportunities.opportunities.take(3).forEach { opportunity ->
                    OpportunityItem(opportunity)
                }
            }
        }
    }
}

@Composable
fun EfficiencyImprovementsCard(efficiencyImprovements: EfficiencyImprovements) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Efficiency Improvements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    label = "Improvements",
                    value = "${efficiencyImprovements.improvements.size}",
                    color = MaterialTheme.colorScheme.primary
                )
                MetricItem(
                    label = "Overall Efficiency",
                    value = "${String.format("%.1f", efficiencyImprovements.overallEfficiency * 100)}%",
                    color = MaterialTheme.colorScheme.secondary
                )
                MetricItem(
                    label = "Priority Items",
                    value = "${efficiencyImprovements.priorityOrder.size}",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            if (efficiencyImprovements.improvements.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Key Improvements:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                efficiencyImprovements.improvements.take(3).forEach { improvement ->
                    EfficiencyImprovementItem(improvement)
                }
            }
        }
    }
}

// Supporting composables
@Composable
fun PriorityBadge(priority: ActionPriority) {
    val backgroundColor = when (priority) {
        ActionPriority.HIGH -> MaterialTheme.colorScheme.error
        ActionPriority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        ActionPriority.LOW -> MaterialTheme.colorScheme.secondary
    }
    
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = priority.name,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MetricItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun CategorySavingsItem(saving: PotentialSaving) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = saving.category,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "₹${String.format("%.2f", saving.potentialSavings)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun BudgetBreakdownItem(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = "₹${String.format("%.2f", amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun RiskItem(risk: IdentifiedRisk) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = risk.description,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = risk.mitigation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun OpportunityItem(opportunity: GrowthOpportunity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
                        Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = opportunity.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
                text = opportunity.expectedOutcome,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun EfficiencyImprovementItem(improvement: EfficiencyImprovement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
                        Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = improvement.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = improvement.expectedSavings,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun EmptyInsightsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Insights Available",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "Add some expenses to generate intelligent insights",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

// Additional composables for other tabs
@Composable
fun PeriodicInsightCard(periodicInsight: PeriodicInsights) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${periodicInsight.period.name} Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Add more content as needed
        }
    }
}

@Composable
fun SpendingPersonalityCard(spendingPersonality: SpendingPersonality) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Spending Personality",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Add more content as needed
        }
    }
}

@Composable
fun PersonalizedTipsCard(tips: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Personalized Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Add more content as needed
        }
    }
}

@Composable
fun HabitFormationCard(habits: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Habit Formation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Add more content as needed
        }
    }
}

@Composable
fun GoalSettingCard(goals: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Goal Setting",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Add more content as needed
        }
    }
}

@Composable
fun MotivationInsightsCard(insights: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Motivation Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Add more content as needed
        }
    }
}
