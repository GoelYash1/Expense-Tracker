package com.example.expensetracker.ui.screens.mainScreen.transactionScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
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
    var showDialog by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { showDialog = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        if (showDialog){
            var otherPartyName by remember {
                mutableStateOf(transaction.otherPartyName)
            }
            AlertDialog(
                onDismissRequest = {showDialog = false},
                properties = DialogProperties(dismissOnBackPress = true,dismissOnClickOutside = true),
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
                            onValueChange = {otherPartyName = it},
                            label = {
                                Text(
                                    text = "To",
                                    fontSize = 16.sp
                                )
                            }
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        Text(
                            text = "Rs ${transaction.amount}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
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
