package com.example.expensetracker.ui.screens.transactionScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R
import com.example.expensetracker.util.Resource
import com.example.expensetracker.viewModels.TransactionViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(transactionViewModel: TransactionViewModel) {
    val transactionResource by transactionViewModel.yearlyTransactions.collectAsState()
    val months = Month.entries.map { item ->
        item
            .name
            .lowercase()
            .replaceFirstChar{
                it.titlecase(Locale.getDefault())
            }
    }.reversed()
    var selectedTime by rememberSaveable { mutableStateOf(LocalDate.now().month.name to Year.now().value) }
    var selectedYear by rememberSaveable { mutableIntStateOf(Year.now().value) }
    var isFetching by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = selectedTime.second) {
        if(!isFetching)
        {
            isFetching = true
            transactionViewModel.fetchTransactions(selectedTime.second)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { if (selectedYear > Year.now().value - 10 && !isFetching) selectedYear-- })
            Text(text = selectedYear.toString(), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { if (selectedYear < Year.now().value && !isFetching) selectedYear++ })
        }

        LazyRow {
            items(listOf(selectedTime.first.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }) + months) { month ->
                val isSelected = selectedTime.first == month.uppercase() && selectedTime.second == selectedYear
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Black, RoundedCornerShape(5.dp))
                        .clickable(enabled = !isSelected && !isFetching) {
                            selectedTime = month.uppercase() to selectedYear
                        }
                        .background(if (isSelected) MaterialTheme.colorScheme.inversePrimary else Color.White)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = month, fontSize = 16.sp)
                        val transactionAmount = transactionResource.data?.find {
                            it.month == Month.valueOf(month.uppercase()) && it.year == selectedYear
                        }?.totalTransactionAmount ?: 0.00
                        val totalExpense = "%.2f".format(transactionAmount)
                        val rotationAngle = if (totalExpense.toDouble() >= 0.0) 180f else 0f
                        val transactionColor = if (totalExpense.toDouble() >= 0.0) Color.Green.copy(green = 0.7f) else Color.Red
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "₹ ${totalExpense.toDouble().absoluteValue}", fontSize = 12.sp)
                            Icon(
                                painter = painterResource(id = R.drawable.ic_expense),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(18.dp)
                                    .rotate(rotationAngle),
                                tint = transactionColor
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val resource = transactionResource) {
                is Resource.Error -> {
                    val error = resource.error
                    Text(
                        text = "Error occurred: ${error?.message ?: "Unknown error"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }

                is Resource.Success -> {
                    isFetching = false
                    val transactions = resource.data
                    val transactionsByDates = transactions?.find { it.year == selectedTime.second && it.month == Month.valueOf(selectedTime.first.uppercase()) }?.transactions
                        ?.groupBy { transaction ->
                            val localDateTime = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(transaction.timestamp),
                                ZoneId.systemDefault()
                            )
                            localDateTime.toLocalDate()
                        }
                    if (transactionsByDates.isNullOrEmpty()) {
                        Text(
                            text = "No transactions have been made this month",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            transactionsByDates.forEach { (date, transactionsForDate) ->
                                val totalExpense = "%.2f".format(
                                    transactionsForDate.sumOf {
                                        when (it.type) {
                                            "Expense" -> -it.amount
                                            else -> it.amount
                                        }
                                    }
                                )
                                val rotationAngle = if (totalExpense.toDouble() >= 0.0) 180f else 0f
                                val transactionColor = if (totalExpense.toDouble() >= 0.0) Color.Green.copy(green = 0.7f) else Color.Red
                                stickyHeader {
                                    Row(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.inversePrimary)
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "₹ ${totalExpense.toDouble().absoluteValue}",
                                                fontSize = 18.sp,
                                                fontStyle = FontStyle.Italic
                                            )
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_expense),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .rotate(rotationAngle),
                                                tint = transactionColor
                                            )
                                        }
                                    }

                                }
                                items(transactionsForDate) { transaction ->
                                    Box(
                                        modifier = Modifier.border(0.2.dp, Color.Black)
                                    ) {
                                        TransactionItemUI(transaction = transaction, transactionViewModel = transactionViewModel, date = date)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
