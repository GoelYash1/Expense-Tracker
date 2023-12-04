package com.example.expensetracker.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expensetracker.data.entities.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTransactions(transactions: List<Transaction>)

    @Query("SELECT * FROM transactions WHERE timestamp >= :from AND timestamp <=:to ORDER BY timestamp DESC")
    fun getTransactions(from: Long,to: Long): Flow<List<Transaction>>
}