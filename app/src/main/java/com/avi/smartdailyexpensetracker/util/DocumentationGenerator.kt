package com.avi.smartdailyexpensetracker.util

import android.content.Context
import android.content.Intent
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Documentation generator for the Smart Daily Expense Tracker app
 */
object DocumentationGenerator {
    
    /**
     * Generate basic app documentation
     */
    fun generateAppDocumentation(context: Context): File? {
        return try {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val fileName = "app_documentation_$timestamp.txt"
            val file = File(context.cacheDir, fileName)
            
            FileWriter(file).use { writer ->
                writer.write(generateBasicDocumentation())
            }
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Share documentation
     */
    fun shareDocumentation(context: Context) {
        val file = generateAppDocumentation(context)
        
        file?.let {
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                it
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "App Documentation")
                putExtra(Intent.EXTRA_TEXT, "Please find attached the documentation for the Smart Daily Expense Tracker app.")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "Share Documentation"))
        }
    }
    
    private fun generateBasicDocumentation(): String {
        return buildString {
            appendLine("=== SMART DAILY EXPENSE TRACKER - APP DOCUMENTATION ===")
            appendLine("Generated on: ${LocalDateTime.now()}")
            appendLine("Version: 1.0.0")
            appendLine()
            
            appendLine("OVERVIEW:")
            appendLine("The Smart Daily Expense Tracker is a comprehensive Android application designed for small business owners to manage their daily expenses efficiently.")
            appendLine()
            
            appendLine("FEATURES:")
            appendLine("- Expense Entry with validation")
            appendLine("- Expense listing and management")
            appendLine("- Category-based organization")
            appendLine("- Real-time expense totals")
            appendLine("- Expense reporting and analytics")
            appendLine("- Theme switching (Light/Dark)")
            appendLine("- Offline data persistence")
            appendLine("- Intelligent insights and recommendations")
            appendLine("- Performance optimization")
            appendLine("- Advanced search and filtering")
            appendLine("- Enhanced charts and analytics")
            appendLine("- Data export (PDF/CSV)")
            appendLine("- Comprehensive testing tools")
            appendLine()
            
            appendLine("ARCHITECTURE:")
            appendLine("- Clean Architecture with MVVM pattern")
            appendLine("- Domain Layer: Business logic and use cases")
            appendLine("- Data Layer: Repository implementations")
            appendLine("- UI Layer: Jetpack Compose screens")
            appendLine()
            
            appendLine("SCREENS:")
            appendLine("1. Expense Entry Screen - Add new expenses")
            appendLine("2. Expense List Screen - View and manage expenses")
            appendLine("3. Expense Report Screen - Generate reports")
            appendLine("4. Intelligent Insights Screen - AI-powered insights")
            appendLine("5. Performance Optimized Screen - High-performance management")
            appendLine("6. Testing Screen - Quality assurance tools")
            appendLine()
            
            appendLine("TECHNOLOGIES:")
            appendLine("- Jetpack Compose for UI")
            appendLine("- Room Database for local storage")
            appendLine("- Coroutines for async operations")
            appendLine("- StateFlow for state management")
            appendLine("- Material3 for design system")
            appendLine()
            
            appendLine("TESTING:")
            appendLine("- Performance testing utilities")
            appendLine("- UI consistency checking")
            appendLine("- Issue tracking and reporting")
            appendLine("- Memory usage monitoring")
            appendLine()
            
            appendLine("PERFORMANCE FEATURES:")
            appendLine("- Lazy loading and pagination")
            appendLine("- Memory optimization")
            appendLine("- Background processing")
            appendLine("- Efficient data rendering")
            appendLine()
            
            appendLine("SECURITY:")
            appendLine("- Local data storage only")
            appendLine("- Input validation")
            appendLine("- Secure error handling")
            appendLine("- Minimal permissions required")
            appendLine()
            
            appendLine("---")
            appendLine("Documentation generated on ${LocalDateTime.now()}")
        }
    }
}
