package com.example.expensetracker.di

import android.content.ContentResolver
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.example.expensetracker.data.daos.TransactionDao
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.messageReader.TransactionSMSReader
import com.example.expensetracker.repository.TransactionRepository
import com.example.expensetracker.repository.TransactionRepositoryImpl
import com.example.expensetracker.util.SMSBoundResource
import com.example.expensetracker.viewModels.TransactionViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@RequiresApi(Build.VERSION_CODES.O)
val appModule = module{
    single<ContentResolver>{androidContext().contentResolver}

    single {
        TransactionSMSReader(get())
    }
    single {
        Room.databaseBuilder(
            androidApplication(),
            ExpenseTrackerDatabase::class.java,"expense_tracker_database"
        ).build()
    }

    single<SMSBoundResource> {SMSBoundResource(get())  }
    single<TransactionDao> {
        get<ExpenseTrackerDatabase>().transactionDao()
    }
    single<TransactionRepository> {
        TransactionRepositoryImpl(get(),get(),get())
    }
    viewModel {
        TransactionViewModel(get())
    }
}