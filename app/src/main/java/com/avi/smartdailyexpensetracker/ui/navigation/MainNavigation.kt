package com.avi.smartdailyexpensetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.avi.smartdailyexpensetracker.ui.screens.ExpenseEntryScreen
import com.avi.smartdailyexpensetracker.ui.screens.ExpenseListScreen
import com.avi.smartdailyexpensetracker.ui.screens.ExpenseReportScreen
import com.avi.smartdailyexpensetracker.ui.screen.IntelligentInsightsScreen
import com.avi.smartdailyexpensetracker.ui.components.ThemeToggle
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseEntryViewModel
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseListViewModel
import com.avi.smartdailyexpensetracker.ui.viewmodel.ExpenseReportViewModel
import com.avi.smartdailyexpensetracker.ui.viewmodel.IntelligentInsightsViewModel
import com.avi.smartdailyexpensetracker.domain.usecase.AddExpenseUseCase
import com.avi.smartdailyexpensetracker.data.service.IntelligentInsightsService
import com.avi.smartdailyexpensetracker.domain.usecase.GetExpensesUseCase
import com.avi.smartdailyexpensetracker.domain.usecase.GenerateReportUseCase
import com.avi.smartdailyexpensetracker.ui.theme.ThemeManager
import com.avi.smartdailyexpensetracker.data.database.AppDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    addExpenseUseCase: AddExpenseUseCase,
    getExpensesUseCase: GetExpensesUseCase,
    generateReportUseCase: GenerateReportUseCase,
    themeManager: ThemeManager,
    database: AppDatabase,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    
    // Create ViewModels using the proper pattern
    val expenseEntryViewModel = remember { ExpenseEntryViewModel(addExpenseUseCase) }
    val expenseListViewModel = remember { ExpenseListViewModel(getExpensesUseCase) }
    val expenseReportViewModel = remember { ExpenseReportViewModel(generateReportUseCase) }
    
    // Create Intelligent Insights service and ViewModel
    val intelligentInsightsService = remember { IntelligentInsightsService(database.expenseDao()) }
    val intelligentInsightsViewModel = remember { IntelligentInsightsViewModel(intelligentInsightsService) }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main content area
            NavHost(
                navController = navController,
                startDestination = Screen.ExpenseEntry.route,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                composable(Screen.ExpenseEntry.route) {
                    ExpenseEntryScreen(
                        viewModel = expenseEntryViewModel,
                        onExpenseAdded = {
                            // Refresh the expense list when a new expense is added
                            expenseListViewModel.refreshExpenses()
                        }
                    )
                }
                
                composable(Screen.ExpenseList.route) {
                    ExpenseListScreen(
                        viewModel = expenseListViewModel
                    )
                }
                
                composable(Screen.ExpenseReport.route) {
                    ExpenseReportScreen(
                        viewModel = expenseReportViewModel
                    )
                }
                
                composable(Screen.IntelligentInsights.route) {
                    IntelligentInsightsScreen(
                        viewModel = intelligentInsightsViewModel,
                        onNavigateBack = {
                            navController.navigateUp()
                        }
                    )
                }
            }
            
            // Bottom navigation bar
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Expense") },
                    label = { Text("Add") },
                    selected = currentRoute == Screen.ExpenseEntry.route,
                    onClick = {
                        navController.navigate(Screen.ExpenseEntry.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Expense List") },
                    label = { Text("List") },
                    selected = currentRoute == Screen.ExpenseList.route,
                    onClick = {
                        navController.navigate(Screen.ExpenseList.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Reports") },
                    label = { Text("Reports") },
                    selected = currentRoute == Screen.ExpenseReport.route,
                    onClick = {
                        navController.navigate(Screen.ExpenseReport.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Intelligent Insights") },
                    label = { Text("Insights") },
                    selected = currentRoute == Screen.IntelligentInsights.route,
                    onClick = {
                        navController.navigate(Screen.IntelligentInsights.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
        
        // Theme toggle floating action button
        ThemeToggle(
            themeManager = themeManager,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
    }
}
