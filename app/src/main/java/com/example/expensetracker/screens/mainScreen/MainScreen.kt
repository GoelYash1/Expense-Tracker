package com.example.expensetracker.screens.mainScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.Home
import com.example.expensetracker.Transactions
import com.example.expensetracker.screens.mainScreen.homeScreen.HomeScreen
import com.example.expensetracker.screens.mainScreen.transactionScreen.TransactionScreen
import com.example.expensetracker.viewModels.TransactionViewModel
import org.koin.androidx.compose.getViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()
    Scaffold(
        topBar = {
             MainTopBar(mainNavController)
        },
        bottomBar = {
            MainBottomNavigation(mainNavController)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            HomeNavigation(mainNavController)
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeNavigation(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Home.route){
        composable(Home.route){
            HomeScreen()
        }
        composable(Transactions.route){
            val transactionViewModel = getViewModel<TransactionViewModel>()
            TransactionScreen(transactionViewModel)
        }
    }
}


