package com.avi.smartdailyexpensetracker.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Issue tracking and bug reporting utility
 */
object IssueTracker {
    
    private const val TAG = "IssueTracker"
    
    // Issue categories
    enum class IssueCategory {
        BUG, FEATURE_REQUEST, PERFORMANCE, UI_UX, CRASH, DATA_LOSS, SECURITY, OTHER
    }
    
    // Issue severity levels
    enum class IssueSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    // Issue status
    enum class IssueStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED, DUPLICATE, WONT_FIX
    }
    
    // Store issues in memory (in a real app, this would be persisted)
    private val issues = mutableListOf<Issue>()
    
    /**
     * Report a new issue
     */
    fun reportIssue(
        title: String,
        description: String,
        category: IssueCategory,
        severity: IssueSeverity,
        stepsToReproduce: String? = null,
        expectedBehavior: String? = null,
        actualBehavior: String? = null,
        deviceInfo: String? = null,
        appVersion: String? = null
    ): Issue {
        val issue = Issue(
            id = generateIssueId(),
            title = title,
            description = description,
            category = category,
            severity = severity,
            status = IssueStatus.OPEN,
            stepsToReproduce = stepsToReproduce,
            expectedBehavior = expectedBehavior,
            actualBehavior = actualBehavior,
            deviceInfo = deviceInfo,
            appVersion = appVersion,
            reportedAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        issues.add(issue)
        Log.i(TAG, "Issue reported: ${issue.id} - ${issue.title}")
        
        return issue
    }
    
    /**
     * Report a crash
     */
    fun reportCrash(
        errorMessage: String,
        stackTrace: String? = null,
        userAction: String? = null
    ): Issue {
        return reportIssue(
            title = "App Crash: $errorMessage",
            description = "The app crashed unexpectedly",
            category = IssueCategory.CRASH,
            severity = IssueSeverity.CRITICAL,
            stepsToReproduce = userAction,
            actualBehavior = "App crashed with error: $errorMessage",
            deviceInfo = getDeviceInfo()
        )
    }
    
    /**
     * Report a performance issue
     */
    fun reportPerformanceIssue(
        operation: String,
        executionTime: Long,
        threshold: Long,
        context: String? = null
    ): Issue {
        val severity = when {
            executionTime >= threshold * 3 -> IssueSeverity.HIGH
            executionTime >= threshold * 2 -> IssueSeverity.MEDIUM
            else -> IssueSeverity.LOW
        }
        
        return reportIssue(
            title = "Performance Issue: $operation",
            description = "Operation '$operation' is performing below expected threshold",
            category = IssueCategory.PERFORMANCE,
            severity = severity,
            actualBehavior = "Operation took ${executionTime}ms (threshold: ${threshold}ms)",
            expectedBehavior = "Operation should complete within ${threshold}ms",
            stepsToReproduce = context
        )
    }
    
    /**
     * Report a UI/UX issue
     */
    fun reportUIUXIssue(
        screen: String,
        component: String,
        description: String,
        severity: IssueSeverity = IssueSeverity.MEDIUM
    ): Issue {
        return reportIssue(
            title = "UI/UX Issue: $screen - $component",
            description = description,
            category = IssueCategory.UI_UX,
            severity = severity,
            stepsToReproduce = "Navigate to $screen and interact with $component"
        )
    }
    
    /**
     * Report a data-related issue
     */
    fun reportDataIssue(
        operation: String,
        description: String,
        severity: IssueSeverity = IssueSeverity.HIGH
    ): Issue {
        return reportIssue(
            title = "Data Issue: $operation",
            description = description,
            category = IssueCategory.DATA_LOSS,
            severity = severity,
            stepsToReproduce = "Perform operation: $operation"
        )
    }
    
    /**
     * Update issue status
     */
    fun updateIssueStatus(issueId: String, status: IssueStatus, notes: String? = null): Issue? {
        val issue = issues.find { it.id == issueId }
        issue?.let {
            it.status = status
            it.notes = notes
            it.updatedAt = LocalDateTime.now()
            Log.i(TAG, "Issue ${issueId} status updated to: $status")
        }
        return issue
    }
    
    /**
     * Add comment to issue
     */
    fun addComment(issueId: String, comment: String, author: String = "User"): Issue? {
        val issue = issues.find { it.id == issueId }
        issue?.let {
            it.comments.add(IssueComment(comment, author, LocalDateTime.now()))
            it.updatedAt = LocalDateTime.now()
            Log.i(TAG, "Comment added to issue ${issueId}")
        }
        return issue
    }
    
    /**
     * Get all issues
     */
    fun getAllIssues(): List<Issue> = issues.toList()
    
    /**
     * Get issues by status
     */
    fun getIssuesByStatus(status: IssueStatus): List<Issue> = issues.filter { it.status == status }
    
    /**
     * Get issues by category
     */
    fun getIssuesByCategory(category: IssueCategory): List<Issue> = issues.filter { it.category == category }
    
    /**
     * Get issues by severity
     */
    fun getIssuesBySeverity(severity: IssueSeverity): List<Issue> = issues.filter { it.severity == severity }
    
    /**
     * Search issues
     */
    fun searchIssues(query: String): List<Issue> {
        val lowercaseQuery = query.lowercase()
        return issues.filter {
            it.title.lowercase().contains(lowercaseQuery) ||
            it.description.lowercase().contains(lowercaseQuery) ||
            it.category.name.lowercase().contains(lowercaseQuery)
        }
    }
    
    /**
     * Get issue statistics
     */
    fun getIssueStatistics(): IssueStatistics {
        val totalIssues = issues.size
        val openIssues = issues.count { it.status == IssueStatus.OPEN }
        val resolvedIssues = issues.count { it.status == IssueStatus.RESOLVED }
        val criticalIssues = issues.count { it.severity == IssueSeverity.CRITICAL }
        
        val categoryBreakdown = IssueCategory.values().associateWith { category ->
            issues.count { it.category == category }
        }
        
        val severityBreakdown = IssueSeverity.values().associateWith { severity ->
            issues.count { it.severity == severity }
        }
        
        return IssueStatistics(
            totalIssues = totalIssues,
            openIssues = openIssues,
            resolvedIssues = resolvedIssues,
            criticalIssues = criticalIssues,
            categoryBreakdown = categoryBreakdown,
            severityBreakdown = severityBreakdown
        )
    }
    
    /**
     * Export issues to file
     */
    fun exportIssuesToFile(context: Context): File? {
        return try {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val fileName = "issues_report_$timestamp.txt"
            val file = File(context.cacheDir, fileName)
            
            FileWriter(file).use { writer ->
                writer.write("=== ISSUES REPORT ===\n")
                writer.write("Generated at: ${LocalDateTime.now()}\n")
                writer.write("Total Issues: ${issues.size}\n\n")
                
                issues.forEach { issue ->
                    writer.write("Issue ID: ${issue.id}\n")
                    writer.write("Title: ${issue.title}\n")
                    writer.write("Description: ${issue.description}\n")
                    writer.write("Category: ${issue.category}\n")
                    writer.write("Severity: ${issue.severity}\n")
                    writer.write("Status: ${issue.status}\n")
                    writer.write("Reported: ${issue.reportedAt}\n")
                    writer.write("Updated: ${issue.updatedAt}\n")
                    if (issue.stepsToReproduce != null) {
                        writer.write("Steps to Reproduce: ${issue.stepsToReproduce}\n")
                    }
                    if (issue.expectedBehavior != null) {
                        writer.write("Expected Behavior: ${issue.expectedBehavior}\n")
                    }
                    if (issue.actualBehavior != null) {
                        writer.write("Actual Behavior: ${issue.actualBehavior}\n")
                    }
                    writer.write("\n")
                }
            }
            
            Log.i(TAG, "Issues exported to: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export issues", e)
            null
        }
    }
    
    /**
     * Share issues report
     */
    fun shareIssuesReport(context: Context) {
        val file = exportIssuesToFile(context)
        file?.let {
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                it
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Issues Report")
                putExtra(Intent.EXTRA_TEXT, "Please find attached the issues report for the Smart Daily Expense Tracker app.")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "Share Issues Report"))
        }
    }
    
    /**
     * Clear all issues
     */
    fun clearAllIssues() {
        issues.clear()
        Log.i(TAG, "All issues cleared")
    }
    
    /**
     * Generate unique issue ID
     */
    private fun generateIssueId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (Math.random() * 1000).toInt()
        return "ISSUE-${timestamp}-${random}"
    }
    
    /**
     * Get device information
     */
    private fun getDeviceInfo(): String {
        return try {
            "Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})"
        } catch (e: Exception) {
            "Unknown device"
        }
    }
}

/**
 * Data class for an issue
 */
data class Issue(
    val id: String,
    val title: String,
    val description: String,
    val category: IssueTracker.IssueCategory,
    val severity: IssueTracker.IssueSeverity,
    var status: IssueTracker.IssueStatus,
    val stepsToReproduce: String? = null,
    val expectedBehavior: String? = null,
    val actualBehavior: String? = null,
    val deviceInfo: String? = null,
    val appVersion: String? = null,
    val reportedAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var notes: String? = null,
    val comments: MutableList<IssueComment> = mutableListOf()
)

/**
 * Data class for issue comments
 */
data class IssueComment(
    val comment: String,
    val author: String,
    val timestamp: LocalDateTime
)

/**
 * Data class for issue statistics
 */
data class IssueStatistics(
    val totalIssues: Int,
    val openIssues: Int,
    val resolvedIssues: Int,
    val criticalIssues: Int,
    val categoryBreakdown: Map<IssueTracker.IssueCategory, Int>,
    val severityBreakdown: Map<IssueTracker.IssueSeverity, Int>
)
