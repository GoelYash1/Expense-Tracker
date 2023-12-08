package com.example.expensetracker.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    val timestamp: Long,
    val title: String,
    var otherPartyName: String,
    val amount: Double,
    val type: String,
    var categoryName: String?,
    val accountId: String?
)

