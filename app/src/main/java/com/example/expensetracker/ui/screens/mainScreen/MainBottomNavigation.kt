package com.example.expensetracker.ui.screens.mainScreen

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.expensetracker.util.Home
import com.example.expensetracker.util.Transactions

@Composable
fun MainBottomNavigation(navController: NavHostController) {
    val destinationList = listOf(
        Home,
        Transactions
    )
    val selectedIndex = rememberSaveable {
        mutableIntStateOf(0)
    }
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        destinationList.forEachIndexed { index, destination ->
            NavigationBarItem(
                label = {
                    Text(
                        text = destination.title,
                        color = Color.White
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.icon),
                        contentDescription = destination.title,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                },
                selected = index == selectedIndex.intValue,
                onClick = {
                    selectedIndex.intValue = index
                    navController.navigate(destinationList[index].route) {
                        popUpTo(Home.route)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}