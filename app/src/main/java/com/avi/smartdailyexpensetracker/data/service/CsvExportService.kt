package com.avi.smartdailyexpensetracker.data.service

import android.content.Context
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseReport
import com.avi.smartdailyexpensetracker.domain.entity.CategoryTotal
import com.avi.smartdailyexpensetracker.domain.entity.DailyTotal
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileWriter
import java.time.format.DateTimeFormatter

class CsvExportService(private val context: Context) {
    
    fun exportExpenseReport(
        expenseReport: ExpenseReport,
        fileName: String = "expense_report_${System.currentTimeMillis()}.csv"
    ): File {
        val file = File(context.getExternalFilesDir(null), fileName)
        val writer = FileWriter(file)
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
            .withHeader("Date", "Category", "Title", "Amount", "Notes")
        )
        
        // Export daily totals
        csvPrinter.printRecord("")
        csvPrinter.printRecord("DAILY BREAKDOWN")
        csvPrinter.printRecord("Date", "Total Expenses", "Total Amount")
        
        expenseReport.dailyTotals.forEach { dailyTotal ->
            csvPrinter.printRecord(
                dailyTotal.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                dailyTotal.count,
                dailyTotal.amount
            )
        }
        
        // Export category totals
        csvPrinter.printRecord("")
        csvPrinter.printRecord("CATEGORY BREAKDOWN")
        csvPrinter.printRecord("Category", "Total Amount", "Percentage", "Count")
        
        expenseReport.categoryTotals.forEach { categoryTotal ->
            csvPrinter.printRecord(
                categoryTotal.category.displayName,
                categoryTotal.amount,
                "${String.format("%.1f", categoryTotal.percentage)}%",
                categoryTotal.count
            )
        }
        
        // Export summary
        csvPrinter.printRecord("")
        csvPrinter.printRecord("SUMMARY")
        csvPrinter.printRecord("Metric", "Value")
        csvPrinter.printRecord("Total Amount", expenseReport.totalAmount)
        csvPrinter.printRecord("Total Expenses", expenseReport.totalCount)
        csvPrinter.printRecord("Date Range", "${expenseReport.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))} to ${expenseReport.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
        csvPrinter.printRecord("Average per Day", expenseReport.totalAmount / expenseReport.totalCount)
        
        csvPrinter.close()
        writer.close()
        
        return file
    }
    
    fun exportDetailedExpenses(
        expenses: List<com.avi.smartdailyexpensetracker.domain.entity.Expense>,
        fileName: String = "detailed_expenses_${System.currentTimeMillis()}.csv"
    ): File {
        val file = File(context.getExternalFilesDir(null), fileName)
        val writer = FileWriter(file)
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
            .withHeader("Date", "Time", "Category", "Title", "Amount", "Notes", "Receipt")
        )
        
        expenses.forEach { expense ->
            csvPrinter.printRecord(
                expense.timestamp.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                expense.timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                expense.category.displayName,
                expense.title,
                expense.amount,
                expense.notes ?: "",
                if (expense.receiptImagePath != null) "Yes" else "No"
            )
        }
        
        csvPrinter.close()
        writer.close()
        
        return file
    }
    
    fun exportCategoryAnalysis(
        categoryTotals: List<CategoryTotal>,
        fileName: String = "category_analysis_${System.currentTimeMillis()}.csv"
    ): File {
        val file = File(context.getExternalFilesDir(null), fileName)
        val writer = FileWriter(file)
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
            .withHeader("Category", "Total Amount", "Percentage", "Count", "Average per Expense")
        )
        
        categoryTotals.forEach { categoryTotal ->
            val avgPerExpense = categoryTotal.amount / categoryTotal.count
            csvPrinter.printRecord(
                categoryTotal.category.displayName,
                categoryTotal.amount,
                "${String.format("%.1f", categoryTotal.percentage)}%",
                categoryTotal.count,
                avgPerExpense
            )
        }
        
        csvPrinter.close()
        writer.close()
        
        return file
    }
}
