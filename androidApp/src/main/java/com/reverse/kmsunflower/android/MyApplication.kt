package com.reverse.kmsunflower.android

import android.app.Application
import android.util.Log
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.reverse.kmsunflower.AndroidPlatform
import com.reverse.kmsunflower.db.DBHelper
import com.reverse.kmsunflower.db.Schema


class MyApplication: Application() {
    val TAG=MyApplication::class.simpleName

    companion object{
        lateinit var database:DBHelper
    }


    override fun onCreate() {
        super.onCreate()
         database= DBHelper.getInstance(AndroidSqliteDriver(Schema,this,"Plants.db"))
        AndroidPlatform.init(this)
    }
}