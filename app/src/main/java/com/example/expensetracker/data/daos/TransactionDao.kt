package com.example.expensetracker.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.expensetracker.data.entities.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllTransactions(transactions: List<Transaction>)
    @Upsert
    suspend fun updateTransaction(transaction: Transaction)
    @Query("SELECT * FROM transactions WHERE timestamp >= :from AND timestamp <=:to ORDER BY timestamp DESC")
    fun getTransactions(from: Long,to: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE categoryName = :categoryName ORDER BY timestamp DESC")
    fun getCategoryTransactions(categoryName: String): List<Transaction>
}