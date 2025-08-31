package com.avi.smartdailyexpensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.avi.smartdailyexpensetracker.data.database.AppDatabase
import com.avi.smartdailyexpensetracker.data.repository.ExpenseRepositoryImpl
import com.avi.smartdailyexpensetracker.domain.usecase.AddExpenseUseCase
import com.avi.smartdailyexpensetracker.domain.usecase.GetExpensesUseCase
import com.avi.smartdailyexpensetracker.domain.usecase.GenerateReportUseCase
import com.avi.smartdailyexpensetracker.ui.navigation.MainNavigation
import com.avi.smartdailyexpensetracker.ui.theme.SmartDailyExpenseTrackerTheme
import com.avi.smartdailyexpensetracker.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Room database
        val database = AppDatabase.getDatabase(this)
        val repository = ExpenseRepositoryImpl(database.expenseDao())
        
        // Initialize Use Cases
        val addExpenseUseCase = AddExpenseUseCase(repository)
        val getExpensesUseCase = GetExpensesUseCase(repository)
        val generateReportUseCase = GenerateReportUseCase(repository)
        
        setContent {
            val themeManager = remember { ThemeManager() }
            
            SmartDailyExpenseTrackerTheme(
                darkTheme = themeManager.isDarkMode()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExpenseTrackerApp(
                        addExpenseUseCase = addExpenseUseCase,
                        getExpensesUseCase = getExpensesUseCase,
                        generateReportUseCase = generateReportUseCase,
                        themeManager = themeManager,
                        database = database
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseTrackerApp(
    addExpenseUseCase: AddExpenseUseCase,
    getExpensesUseCase: GetExpensesUseCase,
    generateReportUseCase: GenerateReportUseCase,
    themeManager: ThemeManager,
    database: AppDatabase
) {
    MainNavigation(
        addExpenseUseCase = addExpenseUseCase,
        getExpensesUseCase = getExpensesUseCase,
        generateReportUseCase = generateReportUseCase,
        themeManager = themeManager,
        database = database
    )
}

