package com.example.expensetracker.data.entities

import com.example.expensetracker.R

data class Category(
    val name: String,
    val iconResId: Int
)
object TransactionCategories {
    const val FOOD = "Food"
    const val BEVERAGES = "Beverages"
    const val RESTAURANTS = "Restaurant"
    const val TRAVEL = "Travel"
    const val HOSPITAL = "Hospital"
    const val EDUCATION = "Education"
    const val BILLS = "Bills"
    const val GROCERY = "Grocery"
    const val CLOTHES = "Clothes"
    const val MOVIES = "Movies"
    const val SPORTS = "Sports"
    const val ELECTRONICS = "Electronics"
    const val OTHERS = "Others"
    const val ALL = "Unknown"

    val categories = listOf(
        Category(ALL,R.drawable.ic_question),
        Category(FOOD, R.drawable.ic_food),
        Category(BEVERAGES, R.drawable.ic_beverages),
        Category(RESTAURANTS, R.drawable.ic_restaurant),
        Category(TRAVEL, R.drawable.ic_travel),
        Category(HOSPITAL, R.drawable.ic_hospital),
        Category(EDUCATION, R.drawable.ic_education),
        Category(BILLS, R.drawable.ic_bills),
        Category(GROCERY, R.drawable.ic_grocery),
        Category(CLOTHES, R.drawable.ic_clothes),
        Category(MOVIES, R.drawable.ic_movies),
        Category(SPORTS, R.drawable.ic_sports),
        Category(ELECTRONICS, R.drawable.ic_electronic),
        Category(OTHERS, R.drawable.ic_others),
    )
}

