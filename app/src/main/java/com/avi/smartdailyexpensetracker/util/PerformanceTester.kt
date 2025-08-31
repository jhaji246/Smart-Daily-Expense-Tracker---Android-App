package com.avi.smartdailyexpensetracker.util

import android.util.Log
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Performance testing utility to measure and monitor app performance metrics
 */
object PerformanceTester {
    
    private const val TAG = "PerformanceTester"
    
    // Performance metrics storage
    private val performanceMetrics = mutableMapOf<String, MutableList<Long>>()
    
    // Performance thresholds
    private const val SLOW_OPERATION_THRESHOLD = 100L // 100ms
    private const val VERY_SLOW_OPERATION_THRESHOLD = 500L // 500ms
    
    /**
     * Measure execution time of a block of code
     */
    inline fun <T> measureTime(
        operationName: String,
        crossinline operation: suspend () -> T
    ): suspend () -> T {
        return {
            val executionTime = measureTimeMillis {
                operation()
            }
            
            recordMetric(operationName, executionTime)
            logPerformance(operationName, executionTime)
            
            operation()
        }
    }
    
    /**
     * Measure execution time of a synchronous block
     */
    inline fun <T> measureTimeSync(
        operationName: String,
        crossinline operation: () -> T
    ): T {
        val executionTime = measureTimeMillis {
            operation()
        }
        
        recordMetric(operationName, executionTime)
        logPerformance(operationName, executionTime)
        
        return operation()
    }
    
    /**
     * Measure database operation performance
     */
    suspend fun <T> measureDatabaseOperation(
        operationName: String,
        operation: suspend () -> T
    ): T {
        return measureTime(operationName) {
            operation()
        }()
    }
    
    /**
     * Measure UI operation performance
     */
    fun <T> measureUIOperation(
        operationName: String,
        operation: () -> T
    ): T {
        return measureTimeSync(operationName) {
            operation()
        }
    }
    
    /**
     * Measure network operation performance (simulated)
     */
    suspend fun <T> measureNetworkOperation(
        operationName: String,
        operation: suspend () -> T
    ): T {
        return measureTime(operationName) {
            // Simulate network delay for testing
            delay(100)
            operation()
        }()
    }
    
    /**
     * Measure memory usage
     */
    fun measureMemoryUsage(operationName: String): Long {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        
        recordMetric("${operationName}_Memory", usedMemory)
        Log.d(TAG, "$operationName Memory Usage: ${usedMemory / 1024 / 1024} MB")
        
        return usedMemory
    }
    
    /**
     * Measure list rendering performance
     */
    fun measureListRendering(
        listSize: Int,
        operation: () -> Unit
    ): Long {
        val startTime = System.currentTimeMillis()
        operation()
        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        
        recordMetric("ListRendering_${listSize}Items", executionTime)
        logPerformance("ListRendering_${listSize}Items", executionTime)
        
        return executionTime
    }
    
    /**
     * Measure search operation performance
     */
    suspend fun <T> measureSearchOperation(
        query: String,
        resultCount: Int,
        operation: suspend () -> T
    ): T {
        return measureTime("Search_${query}_${resultCount}Results") {
            operation()
        }()
    }
    
    /**
     * Measure filter operation performance
     */
    suspend fun <T> measureFilterOperation(
        filterType: String,
        resultCount: Int,
        operation: suspend () -> T
    ): T {
        return measureTime("Filter_${filterType}_${resultCount}Results") {
            operation()
        }()
    }
    
    /**
     * Measure pagination performance
     */
    suspend fun <T> measurePaginationOperation(
        pageNumber: Int,
        pageSize: Int,
        operation: suspend () -> T
    ): T {
        return measureTime("Pagination_Page${pageNumber}_Size${pageSize}") {
            operation()
        }()
    }
    
    /**
     * Record performance metric
     */
    fun recordMetric(operationName: String, executionTime: Long) {
        if (!performanceMetrics.containsKey(operationName)) {
            performanceMetrics[operationName] = mutableListOf()
        }
        performanceMetrics[operationName]?.add(executionTime)
    }
    
    /**
     * Log performance information
     */
    fun logPerformance(operationName: String, executionTime: Long) {
        when {
            executionTime >= VERY_SLOW_OPERATION_THRESHOLD -> {
                Log.w(TAG, "⚠️ VERY SLOW: $operationName took ${executionTime}ms")
            }
            executionTime >= SLOW_OPERATION_THRESHOLD -> {
                Log.w(TAG, "⚠️ SLOW: $operationName took ${executionTime}ms")
            }
            else -> {
                Log.d(TAG, "✅ FAST: $operationName took ${executionTime}ms")
            }
        }
    }
    
    /**
     * Get performance statistics for an operation
     */
    fun getPerformanceStats(operationName: String): PerformanceStats? {
        val metrics = performanceMetrics[operationName] ?: return null
        
        if (metrics.isEmpty()) return null
        
        val sortedMetrics = metrics.sorted()
        val count = metrics.size
        val average = metrics.average()
        val median = if (count % 2 == 0) {
            (sortedMetrics[count / 2 - 1] + sortedMetrics[count / 2]) / 2.0
        } else {
            sortedMetrics[count / 2].toDouble()
        }
        val min = sortedMetrics.first()
        val max = sortedMetrics.last()
        val p95 = sortedMetrics[(count * 0.95).toInt()]
        val p99 = sortedMetrics[(count * 0.99).toInt()]
        
        return PerformanceStats(
            operationName = operationName,
            count = count,
            average = average,
            median = median,
            min = min,
            max = max,
            p95 = p95,
            p99 = p99
        )
    }
    
    /**
     * Get all performance statistics
     */
    fun getAllPerformanceStats(): List<PerformanceStats> {
        return performanceMetrics.keys.mapNotNull { getPerformanceStats(it) }
    }
    
    /**
     * Clear performance metrics
     */
    fun clearMetrics() {
        performanceMetrics.clear()
        Log.d(TAG, "Performance metrics cleared")
    }
    
    /**
     * Generate performance report
     */
    fun generatePerformanceReport(): String {
        val stats = getAllPerformanceStats()
        if (stats.isEmpty()) {
            return "No performance data available"
        }
        
        val report = StringBuilder()
        report.appendLine("=== PERFORMANCE REPORT ===")
        report.appendLine("Generated at: ${java.time.LocalDateTime.now()}")
        report.appendLine()
        
        stats.forEach { stat ->
            report.appendLine("Operation: ${stat.operationName}")
            report.appendLine("  Count: ${stat.count}")
            report.appendLine("  Average: ${stat.average.toLong()}ms")
            report.appendLine("  Median: ${stat.median.toLong()}ms")
            report.appendLine("  Min: ${stat.min}ms")
            report.appendLine("  Max: ${stat.max}ms")
            report.appendLine("  P95: ${stat.p95}ms")
            report.appendLine("  P99: ${stat.p99}ms")
            report.appendLine()
        }
        
        return report.toString()
    }
    
    /**
     * Check if performance is acceptable
     */
    fun isPerformanceAcceptable(operationName: String): Boolean {
        val stats = getPerformanceStats(operationName) ?: return true
        return stats.average <= SLOW_OPERATION_THRESHOLD
    }
    
    /**
     * Get performance recommendations
     */
    fun getPerformanceRecommendations(): List<String> {
        val recommendations = mutableListOf<String>()
        val stats = getAllPerformanceStats()
        
        stats.forEach { stat ->
            when {
                stat.average >= VERY_SLOW_OPERATION_THRESHOLD -> {
                    recommendations.add("🚨 ${stat.operationName}: Very slow (${stat.average.toLong()}ms avg). Consider optimization or caching.")
                }
                stat.average >= SLOW_OPERATION_THRESHOLD -> {
                    recommendations.add("⚠️ ${stat.operationName}: Slow (${stat.average.toLong()}ms avg). Monitor and optimize if needed.")
                }
                else -> {
                    recommendations.add("✅ ${stat.operationName}: Good performance (${stat.average.toLong()}ms avg)")
                }
            }
        }
        
        return recommendations
    }
}

/**
 * Data class for performance statistics
 */
data class PerformanceStats(
    val operationName: String,
    val count: Int,
    val average: Double,
    val median: Double,
    val min: Long,
    val max: Long,
    val p95: Long,
    val p99: Long
)
