package com.example.expensetracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Account")
data class Account(
    @PrimaryKey
    val accountId: String,
    val accountName: String?,
    val defaultCategory: String?,
    var totalTransaction: Int
)
