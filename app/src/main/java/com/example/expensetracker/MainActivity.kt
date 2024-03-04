package com.example.expensetracker

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.screens.MainScreen
import com.example.expensetracker.ui.screens.onBoardingScreen.OnBoardingScreen
import com.example.expensetracker.util.Main
import com.example.expensetracker.util.OnBoarding

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val onBoardingCompleted = remember {
                mutableStateOf(
                    getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
                        .getBoolean("onBoardingCompleted", false)
                )
            }

            val mainNavController = rememberNavController()

            LaunchedEffect(key1 = onBoardingCompleted.value) {
                if (onBoardingCompleted.value) {
                    mainNavController.navigate(Main.route) {
                        popUpTo(OnBoarding.route) { inclusive = true }
                    }
                }
            }

            MainNavigation(mainNavController)
        }

        // Set onBoardingCompleted to false if it's the first launch
        val sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("firstLaunch")) {
            sharedPreferences.edit().putBoolean("onBoardingCompleted", false).apply()
            sharedPreferences.edit().putBoolean("firstLaunch", true).apply()
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigation(mainNavController: NavHostController){
    val backStackEntry = mainNavController.currentBackStackEntryAsState()
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    DisposableEffect(backStackEntry.value) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backStackEntry.value?.destination?.route == Main.route) {
                    // Block back press when on MainScreen
                } else {
                    isEnabled = false
                    mainNavController.popBackStack()
                }
            }
        }

        onBackPressedDispatcher?.addCallback(callback)

        onDispose {
            callback.remove()
        }
    }
    NavHost(navController = mainNavController, startDestination = OnBoarding.route){
        composable(OnBoarding.route){
            OnBoardingScreen(mainNavController)
        }
        composable(Main.route){
            MainScreen()
        }
    }
}