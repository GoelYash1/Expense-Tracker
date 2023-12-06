package com.example.expensetracker.ui.screens.onBoardingScreen

import com.example.expensetracker.R

data class OnBoardingItem(
    val onBoardingImage: Int,
    val title: String,
    val description: String
)

fun getOnBoardingItemList(): List<OnBoardingItem> {
    return listOf(
        OnBoardingItem(
            onBoardingImage = R.drawable.onbg1,
            title = "Start Tracking",
            description = "Stay on top of your finances with ease"
        ),
        OnBoardingItem(
            onBoardingImage = R.drawable.onbg2,
            title = "Set Budgets",
            description = "Manage your spending by setting monthly budgets"
        ),
        OnBoardingItem(
            onBoardingImage = R.drawable.onbg3,
            title = "Categorize Expenses",
            description = "Organize your expenses into custom categories for better tracking"
        ),
        OnBoardingItem(
            onBoardingImage = R.drawable.onbg4,
            title = "Automatic Expense Tracking",
            description = "Effortlessly track your expenses by analyzing SMS messages"
        )
    )
}