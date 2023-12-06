package com.example.expensetracker.screens.mainScreen.transactionScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.entities.Transaction
import com.example.expensetracker.util.DropDownMenuUI
import com.example.expensetracker.util.Resource
import com.example.expensetracker.viewModels.TransactionViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(transactionViewModel: TransactionViewModel) {
    val transactionResource by transactionViewModel.transactions.collectAsState()
    val months = Month.entries.map { it.name }
    var currMonth by remember {
        mutableStateOf(LocalDate.now().month.name)
    }
    Log.d("Month name",LocalDate.now().month.name)
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inversePrimary)
                .padding(vertical = 10.dp, horizontal = 16.dp)
                .fillMaxWidth()

        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.45f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Month",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                DropDownMenuUI(
                    list = months,
                    title = "Months",
                    currValue = currMonth
                ) {
                    currMonth = it
                    val month = Month.valueOf(currMonth)
                    transactionViewModel.getTransactions(month = month)
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            when (transactionResource) {
                is Resource.Error -> {
                    val error = (transactionResource as Resource.Error).error
                    Text(
                        text = "Error occurred: ${error?.message ?: "Unknown error"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Red
                    )
                }

                is Resource.Loading -> {
                    CircularProgressIndicator()
                }

                is Resource.Success -> {
                    val transactions =
                        (transactionResource as Resource.Success<List<Transaction>>).data

                    val transactionsByDates = transactions?.groupBy { transaction ->
                        val localDateTime = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(transaction.timestamp),
                            ZoneId.systemDefault()
                        )
                        localDateTime.toLocalDate()
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        transactionsByDates?.forEach { (date, transactionsForDate) ->
                            stickyHeader {
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(horizontal = 16.dp)
                                )
                            }
                            items(transactionsForDate) { transaction ->
                                val transactionTypeColor =
                                    if (transaction.type == "Expense") Color.Red else Color.Green
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    Text(
                                        text = transaction.type,
                                        modifier = Modifier
                                            .background(
                                                transactionTypeColor,
                                                RoundedCornerShape(topStart = 25.dp)
                                            )
                                            .padding(8.dp),
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    border = BorderStroke(0.2.dp, Color.Black)
                                ) {
                                    TransactionItemUI(transaction = transaction)
                                }
                                Spacer(modifier = Modifier.padding(6.dp))
                            }
                        }
                    }
                }

            }
        }
    }
}

