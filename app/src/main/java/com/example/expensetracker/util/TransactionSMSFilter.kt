package com.example.expensetracker.util

import java.util.regex.Pattern

class TransactionSMSFilter {
    companion object {
        private const val DEBIT_PATTERN = "debited|debit|deducted|transfer"
        private const val MISC_PATTERN = "spent|paying|sent"
        private const val CREDIT_PATTERN = "credited"

        private val IGNORED_WORDS = listOf("redeem", "offer", "rewards", "voucher", "win", "congratulations", "getting","congrats","refunded","otp","declined","disabled", "customer","upcoming","approving")
        private const val ACCOUNT_PATTERN = "[Aa]ccount|/[Cc]|\\b[Cc][Aa][Rr][Dd]\\b"
        private const val ACCOUNT_ID_PATTERN = "(?i)\\bVPA\\s*(\\S+?)\\s*\\(UPI Ref No\\b"
        private const val UPI_PATTERN = "(UPI:\\s*([\\d\\s]+))"

    }

    fun isExpense(message: String): Boolean {
        val regex =
            "(?=.*$ACCOUNT_PATTERN)(?=.*$DEBIT_PATTERN)(?=.*[Ii][Nn][Rr].*|.*[Rr][Ss].*)"
        return !containsIgnoredWords(message)
                && (Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(message).find()
                || DEBIT_PATTERN.toRegex(RegexOption.IGNORE_CASE).containsMatchIn(message)
                || MISC_PATTERN.toRegex(RegexOption.IGNORE_CASE).containsMatchIn(message))
    }

    fun getAmountSpent(message: String): Double? {
        val regex = "(?i)(?:RS|INR|MRP)\\.?\\s?(\\d{1,10}(?:,\\d{3})*(?:\\.\\d{1,2})?)"
        val matchResult = regex.toRegex().find(message)
        val matchValue = matchResult?.groupValues?.getOrNull(1)
        return matchValue?.replace(",", "")?.toDoubleOrNull()
    }


    fun isIncome(message: String): Boolean {
        val regex =
            "(?=.*$ACCOUNT_PATTERN)(?=.*$CREDIT_PATTERN)(?=.*[Ii][Nn][Rr].*|.*[Rr][Ss].*)"
        return !containsIgnoredWords(message)
                && (Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(message).find()
                || CREDIT_PATTERN.toRegex(RegexOption.IGNORE_CASE).containsMatchIn(message))
    }

    fun extractAccount(message: String): String? {
        val accountRegex = ACCOUNT_ID_PATTERN.toRegex()
        val accountMatchResult = accountRegex.find(message)
        var account = accountMatchResult?.groupValues?.getOrNull(1)

        if (!account.isNullOrEmpty()) {
            return account
        }

        val toUpiRegex = "(?i)To\\s+(.*?)\\s+UPI".toRegex()
        val toUpiMatchResult = toUpiRegex.find(message)
        account = toUpiMatchResult?.groupValues?.getOrNull(1)?.trim()

        if (!account.isNullOrEmpty()) {
            return account
        }

        val toOnRegex = "(?i)To\\s+(.*?)\\s+On".toRegex()
        val toOnMatchResult = toOnRegex.find(message)
        account = toOnMatchResult?.groupValues?.getOrNull(1)?.trim()
        return account
    }



    private fun containsIgnoredWords(message: String): Boolean {
        val lowercaseMessage = message.lowercase()
        return IGNORED_WORDS.any { lowercaseMessage.contains(it) }
    }
}