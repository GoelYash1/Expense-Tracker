package com.example.expensetracker.messageReader

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.expensetracker.data.dtos.SMSMessageDTO
import com.example.expensetracker.util.TransactionSMSFilter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class TransactionSMSReader(private val contentResolver: ContentResolver) {

    private val smsFilter = TransactionSMSFilter()

    private companion object {
        const val ID_COLUMN = "_id"
        const val ADDRESS_COLUMN = "address"
        const val BODY_COLUMN = "body"
        const val PERSON_COLUMN = "person"
        const val DATE_COLUMN = "date"
    }

    private fun readTransactionSMS(cursor: Cursor): SMSMessageDTO? {
        val address = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS_COLUMN))
        val body = cursor.getString(cursor.getColumnIndexOrThrow(BODY_COLUMN))
        val time = cursor.getLong(cursor.getColumnIndexOrThrow(DATE_COLUMN))

        val amountSpent = smsFilter.getAmountSpent(body)
        val isExpense = smsFilter.isExpense(body)
        val isIncome = smsFilter.isIncome(body)

        return if (amountSpent != null && (isExpense || isIncome)) {
            SMSMessageDTO(address, body, time)
        } else {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getFormattedDate(timeStamp: Long): String {
        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault())
        return localDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
    }

    private fun queryTransactionMessages(from: Long?, to: Long?): List<SMSMessageDTO> {
        val transactionMessages = mutableListOf<SMSMessageDTO>()

        val projections = arrayOf(ID_COLUMN, ADDRESS_COLUMN, BODY_COLUMN, PERSON_COLUMN, DATE_COLUMN)
        val selection = if (from != null && to != null) "$DATE_COLUMN >= ? AND $DATE_COLUMN <= ?" else null
        val selectionArgs = if (from != null && to != null) arrayOf(from.toString(), to.toString()) else null
        val sortOrder = "$DATE_COLUMN DESC"

        contentResolver.query(
            Uri.parse("content://sms/inbox"),
            projections,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                readTransactionSMS(cursor)?.let { transactionMessages.add(it) }
            }
        }

        return transactionMessages
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTransactionSMS(startMillis:Long, endMillis: Long): Map<String, List<SMSMessageDTO>> {
        val allSMSMessages = queryTransactionMessages(startMillis, endMillis)

        return allSMSMessages.groupBy { sms -> getFormattedDate(sms.time) }
    }
}
