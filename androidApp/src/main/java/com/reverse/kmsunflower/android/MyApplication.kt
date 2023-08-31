package com.reverse.kmsunflower.android

import android.app.Application
import android.util.Log
import com.reverse.kmsunflower.data.AppDatabase
import com.reverse.kmsunflower.data.DatabaseDriverFactory
import com.reverse.kmsunflower.workers.ResourceReader
import com.reverse.kmsunflower.workers.SeedDatabaseWorker
import kotlinx.coroutines.Dispatchers

class MyApplication: Application() {
    val TAG=MyApplication::class.simpleName

    companion object{
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        Log.i(TAG,"onCreate")
        super.onCreate()
        database=AppDatabase.getInstance(DatabaseDriverFactory(this), Dispatchers.Default)
        val seedWorker= SeedDatabaseWorker(database)
        seedWorker.doWork()
    }
}