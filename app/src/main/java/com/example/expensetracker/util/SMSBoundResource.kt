package com.example.expensetracker.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline fun <ResultType,RequestType> smsBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline isPermissionGranted: () -> Boolean
) = flow {
    val data = query().first()

    val flow = if (isPermissionGranted()) {
        emit(Resource.Loading(data))

        try {
            saveFetchResult(fetch())
            query().map {
                Resource.Success(it)
            }
        } catch (throwable: Throwable) {
            query().map {
                Resource.Error(throwable, it)
            }
        }
    } else {
        query().map {
            Resource.Success(it)
        }
    }

    emitAll(flow)
}

class SMSBoundResource(private val context: Context) {
    private fun isSmsPermissionGranted(): Boolean {
        val permission = "android.permission.READ_SMS"
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun <ResultType,RequestType> bind(
        query: () -> Flow<ResultType>,
        fetch: suspend () -> RequestType,
        saveFetchResult: suspend (RequestType) -> Unit
    ) = smsBoundResource(query, fetch, saveFetchResult) { isSmsPermissionGranted() }
}

