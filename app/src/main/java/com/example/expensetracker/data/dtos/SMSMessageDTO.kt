package com.example.expensetracker.data.dtos

data class SMSMessageDTO(
    val address: String,
    val body: String,
    val time: Long
)
