package com.avi.smartdailyexpensetracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * Test configuration utilities for the Smart Daily Expense Tracker
 */
object TestConfig {

    /**
     * Sets up the main dispatcher for testing
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun setupTestDispatcher() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    /**
     * Resets the main dispatcher after testing
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun resetTestDispatcher() {
        Dispatchers.resetMain()
    }

    /**
     * Creates a test dispatcher for controlled testing
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun createTestDispatcher(): TestDispatcher {
        return UnconfinedTestDispatcher()
    }

    /**
     * Common test data constants
     */
    object TestData {
        const val SAMPLE_TITLE = "Test Expense"
        const val SAMPLE_CATEGORY = "Food"
        const val SAMPLE_NOTES = "Test notes"
        const val SAMPLE_AMOUNT = "100.00"
        const val SAMPLE_IMAGE_PATH = "/test/path.jpg"
        const val SAMPLE_ID = 1L
    }

    /**
     * Test validation constants
     */
    object Validation {
        const val MAX_NOTES_LENGTH = 100
        const val MIN_AMOUNT = 0.01
        const val MAX_AMOUNT = 999999.99
    }
}
