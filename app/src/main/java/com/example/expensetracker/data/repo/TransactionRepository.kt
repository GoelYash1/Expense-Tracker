package com.example.expensetracker.data.repo

import com.example.expensetracker.data.entities.Transaction
import com.example.expensetracker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TransactionRepository{
    suspend fun getAllTransactions(from:Long,to:Long): Flow<Resource<List<Transaction>>>
}