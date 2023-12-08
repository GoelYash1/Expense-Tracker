package com.example.expensetracker.repository

import com.example.expensetracker.data.entities.Transaction
import com.example.expensetracker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TransactionRepository{
    suspend fun getAllTransactions(from:Long,to:Long): Flow<Resource<List<Transaction>>>
    suspend fun updateTransaction(transaction: Transaction)
    fun getCategoryTransactions(category: String): Resource<List<Transaction>>
    suspend fun updateTransactionsOfId(
        accountId: String,
        categoryName: String?,
        otherPartyName: String?
    )
}