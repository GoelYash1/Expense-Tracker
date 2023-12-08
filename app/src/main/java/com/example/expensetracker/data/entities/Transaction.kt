package com.example.expensetracker.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    val timestamp: Long,
    val title: String,
    val otherPartyName: String,
    val amount: Double,
    val type: String,
    val categoryName: String,
    val accountId: String?
)

