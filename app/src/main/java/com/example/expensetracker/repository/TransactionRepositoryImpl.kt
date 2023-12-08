package com.example.expensetracker.repository

import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.expensetracker.data.daos.AccountDao
import com.example.expensetracker.data.daos.TransactionDao
import com.example.expensetracker.data.entities.Account
import com.example.expensetracker.data.entities.Transaction
import com.example.expensetracker.data.entities.TransactionCategories
import com.example.expensetracker.messageReader.TransactionSMSReader
import com.example.expensetracker.util.Resource
import com.example.expensetracker.util.SMSBoundResource
import com.example.expensetracker.util.TransactionSMSFilter
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val transactionSMSReader: TransactionSMSReader,
    private val smsBoundResource: SMSBoundResource
) : TransactionRepository {
    private val smsFilter = TransactionSMSFilter()
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAllTransactions(from: Long, to: Long): Flow<Resource<List<Transaction>>> {
        return smsBoundResource.bind(
            query = {
                transactionDao.getTransactions(from, to)
            },
            fetch = {
                Log.e("TransactionRepositoryImpl","In Fetch")
                val transactions = mutableListOf<Transaction>()
                val smsMessageList = transactionSMSReader.getTransactionSMS(from, to).values.flatten()
                smsMessageList.forEach {
                    val amount = smsFilter.getAmountSpent(it.body) ?: 0.0
                    val type = if (smsFilter.isExpense(it.body)) "Expense" else "Income"
                    val accountId = smsFilter.extractAccount(it.body)?.lowercase()
                        ?.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        } ?: "N/A"
                    var account: Account? = null
                    if(accountId!="N/A"){
                        account = accountId.let { it1 -> accountDao.getAccount(it1) }
                    }
                    val categoryName = account?.defaultCategory ?:TransactionCategories.UNKNOWN
                    val otherPartyName = account?.accountName ?: accountId
                    val title = "What For?"

                    transactions.add(
                        Transaction(
                            timestamp = it.time,
                            title = title,
                            otherPartyName = otherPartyName,
                            amount = amount,
                            type = type,
                            categoryName = categoryName,
                            accountId = accountId
                        )
                    )
                }
                transactions
            },
            saveFetchResult = {
                transactionDao.insertAllTransactions(it)
            }
        )
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        try {
            transactionDao.updateTransaction(transaction)
        } catch (e: SQLiteConstraintException) {
            // Handle the exception (e.g., log the error, show a message)
        }
    }


    override suspend fun updateTransactionsOfId(accountId: String, categoryName: String, otherPartyName: String) {
        try {
            val transactionsToUpdate = transactionDao.getTransactionsByAccountId(accountId)
            val updatedTransactions = transactionsToUpdate.map { transaction ->
                transaction.copy(
                    categoryName = categoryName,
                    otherPartyName = otherPartyName
                )
            }
            transactionDao.updateTransactionsOfId(updatedTransactions)
        } catch (e: SQLiteConstraintException) {
            // Handle the exception (e.g., log the error, show a message)
        }
    }



    override fun getCategoryTransactions(category: String): Resource<List<Transaction>> {
        return try {
            Resource.Success(transactionDao.getTransactionsByCategory(category))
        } catch (e: Exception) {
            Resource.Error(e, emptyList())
        }
    }
}
