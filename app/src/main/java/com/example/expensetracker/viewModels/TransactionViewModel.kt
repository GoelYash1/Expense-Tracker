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
    private val _transactions = MutableStateFlow<Resource<List<Transaction>>>(Resource.Loading(emptyList()))
    val transactions : StateFlow<Resource<List<Transaction>>> = _transactions
    private val _yearlyTransactions = MutableStateFlow<List<MonthTransaction>>(emptyList())
    val yearlyTransactions: StateFlow<List<MonthTransaction>> = _yearlyTransactions

    init {
        storeTransactions()
    }

    private fun storeTransactions(year: Int = Year.now().value,month: Month?=null) {
        val (startDateTime, endDateTime) = getDateTime(year)
        val from = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val to = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        viewModelScope.launch {
            _transactions.value = Resource.Loading(emptyList())
            try {
                transactionRepository.getAllTransactions(
                    from = from,
                    to = to
                ).collect { result ->
                    _transactions.value = when (result) {
                        is Resource.Success -> {
                            val transactionsOfYear = result.data?: emptyList()
                            populateYearlyTransactions(year,transactionsOfYear)
                            Resource.Success(result.data ?: emptyList())
                        }
                        is Resource.Error -> Resource.Error(result.error ?: Exception("Unknown error"), emptyList())
                        is Resource.Loading -> Resource.Loading(emptyList())
                    }
                    Log.d("TransactionViewModel","Inside storeTransactions${result.data}")
                }
            } catch (e: Exception) {
                _transactions.value = Resource.Error(e, emptyList())
            }
        }
    }

    private fun populateYearlyTransactions(year: Int,localTransactions:List<Transaction>) {
        _yearlyTransactions.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            val months = Month.entries.toTypedArray()
            val yearlyData = mutableListOf<MonthTransaction>()
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
            _yearlyTransactions.value = yearlyData
        }
    }

    fun fetchTransactions(year: Int = Year.now().value, month: Month? = null, dayOfMonth: Int? = null) {
        Log.d("TransactionViewModel","${_yearlyTransactions.value[0]}")
        viewModelScope.launch {
            try {
                if(_yearlyTransactions.value.isEmpty() || _yearlyTransactions.value[0].year!=year){
                    storeTransactions(year)
                }
                _transactions.value = Resource.Loading(emptyList())
                val transactionsForMonth = _yearlyTransactions.value
                    .find { it.year == year && it.month == month }
                    ?.transactions ?: emptyList()

                _transactions.value = Resource.Success(transactionsForMonth)
            } catch (e: Exception) {
                _transactions.value = Resource.Error(e, emptyList())
            }
        }
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
