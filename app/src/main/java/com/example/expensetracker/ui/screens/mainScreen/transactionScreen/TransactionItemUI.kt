package com.example.expensetracker.ui.screens.mainScreen.transactionScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.toColor
import com.example.expensetracker.R
import com.example.expensetracker.data.entities.Transaction
import com.example.expensetracker.data.entities.TransactionCategories
import com.example.expensetracker.viewModels.TransactionViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionItemUI(
    transaction: Transaction,
    transactionViewModel: TransactionViewModel,
    date: LocalDate
) {
    val transactionCategories = TransactionCategories.categories
    var currCategory by remember {
        mutableStateOf(transaction.categoryName)
    }
    var otherPartyName by remember {
        mutableStateOf(transaction.otherPartyName)
    }
    var currTitle by remember {
        mutableStateOf(transaction.title)
    }
    var showCategories by remember {
        mutableStateOf(false)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    var categoryIcon by remember{
        mutableIntStateOf( transactionCategories.find {
            currCategory == it.name
        }?.iconResId!!)
    }
    var rotationAngle by remember {
        mutableFloatStateOf(0f)
    }
    var transactionColor by remember {
        mutableStateOf(Color.Green.copy(green = 0.7f))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { showDialog = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // First Column
        val category =
            TransactionCategories.categories.find { it.name == transaction.categoryName }
        category?.let {
            Icon(
                painter = painterResource(id = it.iconResId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
        // Second Column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = transaction.otherPartyName,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = transaction.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Third Column
        Column {
            Text(
                text = transaction.amount.toString(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = if (transaction.amount >= 0) Color.Green else Color.Red
            )
            Text(
                text = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(transaction.timestamp),
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ofPattern("hh:mm a")),
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            )
        }

        // Fourth Column
        if (transaction.type == "Income"){
            transactionColor = Color.Red
            rotationAngle = 180f
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_expense),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .rotate(rotationAngle),
            tint = transactionColor
        )


        if (showDialog) {

            AlertDialog(
                onDismissRequest = { showDialog = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                title = {
                    Text(
                        text = "Transaction Details",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                                fontSize = 16.sp
                            )
                            Text(
                                text = LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(transaction.timestamp),
                                    ZoneId.systemDefault()
                                ).format(DateTimeFormatter.ofPattern("hh:mm a")),
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.padding(10.dp))

                        TextField(
                            value = otherPartyName,
                            onValueChange = { otherPartyName = it },
                            label = {
                                Text(
                                    text = if (transaction.type == "Expense") "To" else "From",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.padding(10.dp))
                        Text(
                            text = "Of",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Rs ${transaction.amount}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic
                        )

                        Spacer(modifier = Modifier.padding(10.dp))
                        TextField(
                            value = currTitle,
                            onValueChange = {currTitle = it},
                            label = {
                                Text(
                                    text = "For",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.padding(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Category: ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(5.dp))
                            IconButton(
                                onClick = {
                                    showCategories = !showCategories
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Gray.copy(0.2f))
                            ) {
                                Row {
                                    Icon(
                                        painter = painterResource(id = categoryIcon),
                                        contentDescription = ""
                                    )
                                    Spacer(modifier = Modifier.padding(5.dp))
                                    Text(text = currCategory, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                        if (showCategories){
                            Spacer(modifier = Modifier.height(5.dp))
                            LazyRow{
                                items(transactionCategories){category->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
                                            .padding(4.dp)
                                            .clickable {
                                                currCategory = category.name
                                                categoryIcon = category.iconResId
                                                showCategories = false
                                            }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = category.iconResId),
                                            contentDescription = ""
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = category.name, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.padding(5.dp))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newTransaction = transaction.copy(
                                timestamp = transaction.timestamp,
                                title = currTitle,
                                categoryName = currCategory,
                                amount = transaction.amount,
                                otherPartyName = otherPartyName,
                                type = transaction.type,
                                accountId = transaction.accountId
                            )
                            transactionViewModel.updateTransaction(newTransaction)
                            showDialog = false
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(text = "Apply")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}
