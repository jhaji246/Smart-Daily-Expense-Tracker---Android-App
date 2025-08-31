package com.avi.smartdailyexpensetracker.domain.exception

sealed class ExpenseException(message: String) : Exception(message)

class InvalidExpenseDataException(message: String) : ExpenseException(message)

class ExpenseNotFoundException(message: String) : ExpenseException(message)

class DatabaseOperationException(message: String) : ExpenseException(message)

class ValidationException(message: String) : ExpenseException(message)
