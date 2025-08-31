package com.avi.smartdailyexpensetracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avi.smartdailyexpensetracker.ui.components.IntelligentInsightsDashboard
import com.avi.smartdailyexpensetracker.ui.viewmodel.IntelligentInsightsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntelligentInsightsScreen(
    viewModel: IntelligentInsightsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionableInsights by viewModel.actionableInsights.collectAsStateWithLifecycle()
    val periodicInsights by viewModel.periodicInsights.collectAsStateWithLifecycle()
    val personalizedInsights by viewModel.personalizedInsights.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadInsights()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Intelligent Insights",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshInsights() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh insights"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                uiState.error != null -> {
                    ErrorState(
                        error = uiState.error!!,
                        onRetry = { viewModel.refreshInsights() },
                        onDismiss = { viewModel.clearError() }
                    )
                }
                else -> {
                    IntelligentInsightsDashboard(
                        actionableInsights = actionableInsights,
                        periodicInsights = periodicInsights,
                        personalizedInsights = personalizedInsights,
                        onActionClick = { action ->
                            viewModel.onActionClick(action)
                        },
                        onRecommendationClick = { recommendation ->
                            viewModel.onRecommendationClick(recommendation)
                        }
                    )
                }
            }
            
            // Success messages
            uiState.lastActionTaken?.let { action ->
                LaunchedEffect(action) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearLastAction()
                }
                ActionTakenSnackbar(
                    action = action,
                    onDismiss = { viewModel.clearLastAction() }
                )
            }
            
            uiState.lastRecommendationViewed?.let { recommendation ->
                LaunchedEffect(recommendation) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearLastRecommendation()
                }
                RecommendationViewedSnackbar(
                    recommendation = recommendation,
                    onDismiss = { viewModel.clearLastRecommendation() }
                )
            }
        }
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Generating Intelligent Insights...",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Analyzing your spending patterns and business data",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
                    Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Failed to Load Insights",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
            Button(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Dismiss")
            }
        }
    }
}

@Composable
fun ActionTakenSnackbar(
    action: String,
    onDismiss: () -> Unit
) {
    Snackbar(
        modifier = Modifier.padding(16.dp),
        action = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Action taken: $action")
        }
    }
}

@Composable
fun RecommendationViewedSnackbar(
    recommendation: String,
    onDismiss: () -> Unit
) {
    Snackbar(
        modifier = Modifier.padding(16.dp),
        action = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Recommendation viewed: $recommendation")
        }
    }
}
