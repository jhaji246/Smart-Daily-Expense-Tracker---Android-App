package com.avi.smartdailyexpensetracker.data.service

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.core.content.ContextCompat
import com.avi.smartdailyexpensetracker.domain.entity.ExpenseReport
import com.avi.smartdailyexpensetracker.domain.entity.CategoryTotal
import com.avi.smartdailyexpensetracker.domain.entity.DailyTotal
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter
import java.util.*

class PdfReportGenerator(private val context: Context) {
    
    fun generateExpenseReport(
        expenseReport: ExpenseReport,
        fileName: String = "expense_report_${System.currentTimeMillis()}.pdf"
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        
        // Set background
        canvas.drawColor(android.graphics.Color.WHITE)
        
        var yPosition = 50f
        val leftMargin = 50f
        val rightMargin = 545f
        val pageWidth = rightMargin - leftMargin
        
        // Title
        val titlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(33, 33, 33)
            textSize = 24f
            isFakeBoldText = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
        
        canvas.drawText(
            "Expense Report",
            (leftMargin + pageWidth / 2),
            yPosition,
            titlePaint
        )
        
        yPosition += 40f
        
        // Date range
        val subtitlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(117, 117, 117)
            textSize = 14f
            textAlign = android.graphics.Paint.Align.CENTER
        }
        
        val dateRange = "${expenseReport.startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))} - " +
                expenseReport.endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        
        canvas.drawText(
            dateRange,
            (leftMargin + pageWidth / 2),
            yPosition,
            subtitlePaint
        )
        
        yPosition += 60f
        
        // Summary section
        drawSectionHeader(canvas, "Summary", leftMargin, yPosition)
        yPosition += 30f
        
        // Summary details
        val summaryPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(33, 33, 33)
            textSize = 12f
        }
        
        val labelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(117, 117, 117)
            textSize = 12f
            isFakeBoldText = true
        }
        
        // Total Amount
        canvas.drawText("Total Amount:", leftMargin, yPosition, labelPaint)
        canvas.drawText("₹${String.format("%.2f", expenseReport.totalAmount)}", leftMargin + 200, yPosition, summaryPaint)
        yPosition += 20f
        
        // Total Expenses
        canvas.drawText("Total Expenses:", leftMargin, yPosition, labelPaint)
        canvas.drawText("${expenseReport.totalCount}", leftMargin + 200, yPosition, summaryPaint)
        yPosition += 20f
        
        // Average per day
        val avgPerDay = expenseReport.totalAmount / expenseReport.totalCount
        canvas.drawText("Average per Day:", leftMargin, yPosition, labelPaint)
        canvas.drawText("₹${String.format("%.2f", avgPerDay)}", leftMargin + 200, yPosition, summaryPaint)
        
        yPosition += 40f
        
        // Daily breakdown section
        if (expenseReport.dailyTotals.isNotEmpty()) {
            drawSectionHeader(canvas, "Daily Breakdown", leftMargin, yPosition)
            yPosition += 30f
            
            // Table header
            drawTableHeader(canvas, leftMargin, yPosition)
            yPosition += 25f
            
            // Daily totals
            expenseReport.dailyTotals.forEach { dailyTotal ->
                if (yPosition > 750f) {
                    // Start new page if needed
                    pdfDocument.finishPage(page)
                    val newPage = pdfDocument.startPage(pageInfo)
                    canvas = newPage.canvas
                    canvas.drawColor(android.graphics.Color.WHITE)
                    yPosition = 50f
                }
                
                drawDailyTotalRow(canvas, dailyTotal, leftMargin, yPosition)
                yPosition += 20f
            }
            
            yPosition += 20f
        }
        
        // Category breakdown section
        if (expenseReport.categoryTotals.isNotEmpty()) {
            drawSectionHeader(canvas, "Category Breakdown", leftMargin, yPosition)
            yPosition += 30f
            
            // Category totals
            expenseReport.categoryTotals.forEach { categoryTotal ->
                if (yPosition > 750f) {
                    // Start new page if needed
                    pdfDocument.finishPage(page)
                    val newPage = pdfDocument.startPage(pageInfo)
                    canvas = newPage.canvas
                    canvas.drawColor(android.graphics.Color.WHITE)
                    yPosition = 50f
                }
                
                drawCategoryTotalRow(canvas, categoryTotal, leftMargin, yPosition)
                yPosition += 20f
            }
        }
        
        // Footer
        yPosition = 800f
        val footerPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(158, 158, 158)
            textSize = 10f
            textAlign = android.graphics.Paint.Align.CENTER
        }
        
        canvas.drawText(
            "Generated on ${Date()} by Smart Daily Expense Tracker",
            (leftMargin + pageWidth / 2),
            yPosition,
            footerPaint
        )
        
        pdfDocument.finishPage(page)
        
        // Save to file
        val file = File(context.getExternalFilesDir(null), fileName)
        val outputStream = FileOutputStream(file)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
        outputStream.close()
        
        return file
    }
    
    private fun drawSectionHeader(canvas: android.graphics.Canvas, title: String, x: Float, y: Float) {
        val headerPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(33, 150, 243)
            textSize = 16f
            isFakeBoldText = true
        }
        
        canvas.drawText(title, x, y, headerPaint)
        
        // Underline
        val linePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(33, 150, 243)
            strokeWidth = 2f
        }
        
        canvas.drawLine(x, y + 5, x + 200, y + 5, linePaint)
    }
    
    private fun drawTableHeader(canvas: android.graphics.Canvas, x: Float, y: Float) {
        val headerPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(33, 33, 33)
            textSize = 12f
            isFakeBoldText = true
        }
        
        canvas.drawText("Date", x, y, headerPaint)
        canvas.drawText("Expenses", x + 150, y, headerPaint)
        canvas.drawText("Amount", x + 250, y, headerPaint)
        
        // Header line
        val linePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(189, 189, 189)
            strokeWidth = 1f
        }
        
        canvas.drawLine(x, y + 5, x + 350, y + 5, linePaint)
    }
    
    private fun drawDailyTotalRow(canvas: android.graphics.Canvas, dailyTotal: DailyTotal, x: Float, y: Float) {
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(33, 33, 33)
            textSize = 11f
        }
        
        val date = dailyTotal.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        canvas.drawText(date, x, y, textPaint)
        canvas.drawText("${dailyTotal.count}", x + 150, y, textPaint)
        canvas.drawText("₹${String.format("%.2f", dailyTotal.amount)}", x + 250, y, textPaint)
    }
    
    private fun drawCategoryTotalRow(canvas: android.graphics.Canvas, categoryTotal: CategoryTotal, x: Float, y: Float) {
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(33, 33, 33)
            textSize = 11f
        }
        
        val labelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(117, 117, 117)
            textSize = 11f
        }
        
        canvas.drawText("${categoryTotal.category.displayName}:", x, y, labelPaint)
        canvas.drawText("₹${String.format("%.2f", categoryTotal.amount)}", x + 200, y, textPaint)
        canvas.drawText("(${String.format("%.1f", categoryTotal.percentage)}%)", x + 300, y, textPaint)
    }
}
