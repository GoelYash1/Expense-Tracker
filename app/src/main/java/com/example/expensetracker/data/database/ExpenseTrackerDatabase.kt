package com.example.expensetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.expensetracker.data.daos.TransactionDao
import com.example.expensetracker.data.entities.Transaction

@Database(entities = [Transaction::class],version = 1)
abstract class ExpenseTrackerDatabase: RoomDatabase() {
    abstract fun transactionDao() : TransactionDao
}