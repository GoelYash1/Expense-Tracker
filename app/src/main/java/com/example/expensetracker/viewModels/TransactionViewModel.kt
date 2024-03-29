package com.example.expensetracker.viewModels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.entities.MonthTransaction
import com.example.expensetracker.data.entities.Transaction
import com.example.expensetracker.repository.TransactionRepository
import com.example.expensetracker.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.time.ZoneId
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class TransactionViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    private val _yearlyTransactions = MutableStateFlow<Resource<List<MonthTransaction>>>(Resource.Loading(emptyList()))
    val yearlyTransactions: StateFlow<Resource<List<MonthTransaction>>> = _yearlyTransactions

    fun fetchTransactions(year: Int = Year.now().value) {
        val (startDateTime, endDateTime) = getDateTime(year)
        val from = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val to = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        viewModelScope.launch(Dispatchers.IO) {
            _yearlyTransactions.value = Resource.Loading(emptyList())
            try {
                transactionRepository.getAllTransactions(
                    from = from,
                    to = to
                ).collect { result ->
                    _yearlyTransactions.value = when (result) {
                        is Resource.Success -> {
                            val yearlyDate = populateYearlyTransactions(year,result.data?: emptyList())
                            Resource.Success(yearlyDate)
                        }
                        is Resource.Error -> Resource.Error(result.error ?: Exception("Unknown error"), emptyList())
                        is Resource.Loading -> Resource.Loading(emptyList())
                    }
                    Log.d("TransactionViewModel","Inside storeTransactions${result.data}")
                }
            } catch (e: Exception) {
                _yearlyTransactions.value = Resource.Error(e, emptyList())
            }
        }
    }

    private fun populateYearlyTransactions(year: Int,localTransactions:List<Transaction>):List<MonthTransaction> {
        val yearlyData = mutableListOf<MonthTransaction>()
        val months = Month.entries.toTypedArray()
        viewModelScope.launch(Dispatchers.IO) {
            for (month in months) {
                val totalAmount = calculateTotalAmountForMonth(year, month)
                val filteredTransactions = localTransactions.filter {
                    val transactionDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(it.timestamp),
                        ZoneId.systemDefault()
                    )
                    transactionDateTime.year == year && transactionDateTime.month == month
                }
                yearlyData.add(
                    MonthTransaction(
                        month = month,
                        year = year,
                        transactions = filteredTransactions,
                        totalTransactionAmount = totalAmount
                    )
                )
            }
        }
        return yearlyData
    }

    private suspend fun calculateTotalAmountForMonth(year: Int, month: Month): Double {
        return transactionRepository.getTotalTransactionAmount(
            from = getDateTime(year, month).first.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            to = getDateTime(year, month).second.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
        }
    }


    private fun getDateTime(year: Int, month: Month? = null, dayOfMonth: Int? = null): Pair<LocalDateTime, LocalDateTime> {
        val startDateTime = when {
            month != null && dayOfMonth != null -> LocalDateTime.of(year, month, dayOfMonth, 0, 0, 0)
            month != null -> LocalDateTime.of(year, month, 1, 0, 0, 0)
            else -> LocalDateTime.of(year, 1, 1, 0, 0, 0)
        }
        val lastDayOfMonth = when {
            dayOfMonth != null -> dayOfMonth
            else -> month?.length(Year.isLeap(year.toLong())) ?: 31
        }
        val endDateTime = LocalDateTime.of(year, month ?: Month.DECEMBER, lastDayOfMonth, 23, 59, 59)

        return Pair(startDateTime, endDateTime)
    }
}
