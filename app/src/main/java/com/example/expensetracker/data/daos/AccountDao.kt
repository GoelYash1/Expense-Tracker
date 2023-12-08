package com.example.expensetracker.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.expensetracker.data.entities.Account

@Dao
interface AccountDao {
    @Query("SELECT * FROM Account WHERE accountId = :accountId")
    suspend fun getAccount(accountId: String):Account?

    @Upsert
    suspend fun addAccount(account: Account)
}