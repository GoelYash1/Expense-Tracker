package com.example.expensetracker.screens.mainScreen.transactionScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.expensetracker.data.entities.Transaction
import com.example.expensetracker.util.Resource
import com.example.expensetracker.viewModels.TransactionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(transactionViewModel: TransactionViewModel) {
    val transactionResource by transactionViewModel.transactions.collectAsState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        when(transactionResource){
            is Resource.Error -> {
                
            }
            is Resource.Loading -> {
                CircularProgressIndicator()
            }
            is Resource.Success -> {
                val transactionList = (transactionResource as Resource.Success<List<Transaction>>).data
                Log.d("TransactionListIn Screen", transactionList?.size.toString())
                if (!transactionList.isNullOrEmpty()) {
                    Text(text = "${transactionList.first().amount}")
                }
            }

        }
    }
}
