package com.example.expensetracker.ui.screens.mainScreen.transactionScreen

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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.entities.Transaction
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

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(transactionViewModel: TransactionViewModel) {
    val transactionResource by transactionViewModel.transactions.collectAsState()
    val months = Month.entries.map { item ->
        item.name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }.reversed()
    var selectedMonth by rememberSaveable {
        mutableStateOf(LocalDate.now().month.name)
    }
    var selectedYear by rememberSaveable {
        mutableIntStateOf(Year.now().value)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null, modifier = Modifier.size(32.dp).clickable { selectedYear-=1 })
            Text(text = selectedYear.toString(), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(32.dp).clickable { if (selectedYear < Year.now().value) selectedYear+=1 })
        }
        LazyRow{
            items(months){
                Box(
                    modifier = Modifier
                        .border(1.dp,Color.Black, RoundedCornerShape(5.dp))
                        .clickable {
                            selectedMonth = it.uppercase()
                            val month = Month.valueOf(selectedMonth)
                            transactionViewModel.getTransactions(
                                year = selectedYear,
                                month = month
                            )
                        }
                        .background(if (selectedMonth == it.uppercase()) MaterialTheme.colorScheme.inversePrimary else Color.White)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp
                        )
                    }
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
                    ) {
                        transactionsByDates?.forEach { (date, transactionsForDate) ->
                            stickyHeader {
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.inversePrimary)
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                            items(transactionsForDate) { transaction ->
                                Box(
                                    modifier = Modifier
                                        .border(0.2.dp, Color.Black)
                                ) {
                                    TransactionItemUI(transaction = transaction, transactionViewModel = transactionViewModel,date = date)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

