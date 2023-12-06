package com.example.expensetracker.screens.mainScreen.transactionScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.entities.Transaction
import com.example.expensetracker.data.entities.TransactionCategories
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionItemUI(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
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
    }
}
