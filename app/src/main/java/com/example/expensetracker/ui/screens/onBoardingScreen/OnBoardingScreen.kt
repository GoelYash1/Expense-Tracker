package com.example.expensetracker.ui.screens.onBoardingScreen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun OnBoardingScreen(
    mainNavController: NavHostController
) {
    val onBoardingItems = getOnBoardingItemList()
    var currentItemIndex by remember { mutableIntStateOf(0) }
    val sharedPreferences = LocalContext.current.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
    val onBoardingCompleted = remember {
        mutableStateOf(
            sharedPreferences.getBoolean("onBoardingCompleted", false)
        )
    }
    Column(
        Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        if (onBoardingCompleted.value) {
            SMSPermissionHandling(mainNavController)
        } else {
            UpperOnBoardingPanel(onBoardingItems, currentItemIndex)
            LowerOnBoardingPanel(
                onBoardingItems,
                currentItemIndex,
                setOnBoardingCompleted = { completed ->
                    onBoardingCompleted.value = completed
                    sharedPreferences.edit().putBoolean("onBoardingCompleted",completed).apply()
                },
                onNextClick = { currentItemIndex++ },
                onPreviousClick = { currentItemIndex-- }
            )
        }
    }
}

@Composable
fun UpperOnBoardingPanel(onBoardingItems: List<OnBoardingItem>, currentItemIndex: Int) {
    Box(
        modifier = Modifier
            .fillMaxHeight(0.6f)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = onBoardingItems[currentItemIndex].onBoardingImage),
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LowerOnBoardingPanel(
    onBoardingItems: List<OnBoardingItem>,
    currentItemIndex: Int,
    setOnBoardingCompleted: (Boolean) -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = onBoardingItems[currentItemIndex].title,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.padding(15.dp))
            Text(
                text = onBoardingItems[currentItemIndex].description,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if(currentItemIndex>0){
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        modifier = Modifier
                            .clickable { onPreviousClick() }
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "",
                    modifier = Modifier
                        .clickable {
                            if (currentItemIndex == onBoardingItems.lastIndex) {
                                setOnBoardingCompleted(true)
                            } else {
                                onNextClick()
                            }
                        }
                )
            }
            Spacer(
                modifier = Modifier.padding(10.dp)
            )
            Button(
                onClick = {
                    setOnBoardingCompleted(true)
                }
            ) {
                Text(text = "Skip")
            }
        }
    }
}