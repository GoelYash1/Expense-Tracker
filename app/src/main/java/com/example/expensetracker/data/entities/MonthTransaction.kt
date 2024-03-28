package com.example.expensetracker.data.entities

import java.time.Month

data class MonthTransaction(
    val month: Month,
    val year: Int,
    val transactions: List<Transaction>,
    val totalTransactionAmount:Double
)
