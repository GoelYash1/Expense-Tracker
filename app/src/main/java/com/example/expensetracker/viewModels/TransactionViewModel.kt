package com.example.expensetracker.viewModels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.entities.Transaction
import com.example.expensetracker.repository.TransactionRepository
import com.example.expensetracker.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.time.ZoneId
import java.util.Calendar
import kotlin.coroutines.cancellation.CancellationException

@RequiresApi(Build.VERSION_CODES.O)
class TransactionViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    private val _transactions = MutableStateFlow<Resource<List<Transaction>>>(Resource.Loading(emptyList()))
    val transactions: StateFlow<Resource<List<Transaction>>> = _transactions
    init {
        getTransactions(month = LocalDate.now().month)
    }

    fun getTransactions(year: Int = Year.now().value, month: Month?=null, dayOfMonth: Int? = null) {
        val (startDateTime, endDateTime) = getDateTime(year, month, dayOfMonth)
        val from = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val to = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        viewModelScope.launch {
            _transactions.value = Resource.Loading(emptyList())
            try {
                transactionRepository.getAllTransactions(from, to).collect { result ->
                    _transactions.value = when (result) {
                        is Resource.Success -> Resource.Success(result.data ?: emptyList())
                        is Resource.Error -> result.error?.let { Resource.Error(it, emptyList()) }!!
                        is Resource.Loading -> Resource.Loading(emptyList())
                    }
                }
            } catch (_: CancellationException) {
                //
            } catch (e: Exception) {
                _transactions.value = Resource.Error(e, emptyList())
            }
        }
    }


    private fun getDateTime(year: Int, month: Month?, dayOfMonth: Int?): Pair<LocalDateTime, LocalDateTime> {
        val startDateTime = when {
            month != null && dayOfMonth != null -> LocalDateTime.of(year, month, dayOfMonth, 0, 0, 0)
            month != null -> LocalDateTime.of(year, month, 1, 0, 0, 0)
            else -> LocalDateTime.of(year, 1, 1, 0, 0, 0)
        }
        val lastDayOfMonth = when{
            dayOfMonth!=null->dayOfMonth
            else->month?.length(Year.isLeap(year.toLong())) ?: 31
        }
        val endDateTime = LocalDateTime.of(year, month ?: Month.DECEMBER, lastDayOfMonth, 23, 59, 59)

        return Pair(startDateTime, endDateTime)
    }
}
