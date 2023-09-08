package com.reverse.kmsunflower.android

import android.app.Application
import android.util.Log
import com.reverse.kmsunflower.AndroidPlatform


class MyApplication: Application() {
    val TAG=MyApplication::class.simpleName


    override fun onCreate() {
        Log.i(TAG,"onCreate")
        super.onCreate()
        AndroidPlatform.init(this)
    }
}