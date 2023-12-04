package com.example.expensetracker.data.repo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.expensetracker.data.daos.TransactionDao
import com.example.expensetracker.data.entities.Transaction
import com.example.expensetracker.data.entities.TransactionCategories
import com.example.expensetracker.messageReader.TransactionSMSReader
import com.example.expensetracker.util.Resource
import com.example.expensetracker.util.SMSBoundResource
import com.example.expensetracker.util.TransactionSMSFilter
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val transactionSMSReader: TransactionSMSReader,
    private val smsBoundResource: SMSBoundResource
) : TransactionRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAllTransactions(from: Long, to: Long): Flow<Resource<List<Transaction>>> {
        Log.d("Get All Transactions Called","")
        return smsBoundResource.bind(
            query = {
                transactionDao.getTransactions(from, to)
            },
            fetch = {
                val transactions = mutableListOf<Transaction>()
                val smsMessageList = transactionSMSReader.getTransactionSMS(from, to).values.flatten()
                smsMessageList.forEach {
                    val amount = TransactionSMSFilter().getAmountSpent(it.body) ?: 0.0
                    val type = if (TransactionSMSFilter().isExpense(it.body)) "Expense" else "Income"
                    val otherPartyName = TransactionSMSFilter().extractAccount(it.body) ?: "N/A"
                    val title = "What For?"
                    val categoryName = "All"
                    val accountId = ""
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
                Log.d("transactions","${transactions.size}")
                transactions
            }
        ) {
            transactionDao.insertTransactions(it)
        }
    }
}
