package com.example.expensetracker.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.expensetracker.data.entities.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllTransactions(transactions: List<Transaction>)
    @Update
    suspend fun updateTransaction(transaction: Transaction)
    @Update
    suspend fun updateTransactionsOfId(transactions: List<Transaction>)
    @Query("SELECT * FROM transactions WHERE timestamp >= :from AND timestamp <=:to ORDER BY timestamp DESC")
    fun getTransactions(from: Long,to: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE categoryName = :categoryName ORDER BY timestamp DESC")
    fun getTransactionsByCategory(categoryName: String): List<Transaction>

    @Query("SELECT * FROM transactions WHERE categoryName = :accountId ORDER BY timestamp DESC")
    fun getTransactionsByAccountId(accountId: String): List<Transaction>

    @Query("SELECT SUM(CASE WHEN type='Expense' THEN -1 * amount ELSE amount END) FROM transactions WHERE timestamp>=:from AND timestamp<=:to")
    suspend fun getTotalTransactionAmount(from: Long, to: Long): Double
}