package com.reverse.kmsunflower

import androidx.compose.runtime.Composable
import com.reverse.kmsunflower.utilities.Log
import io.github.aakira.napier.DebugAntilog
import io.ktor.client.*

interface Platform {
    val name: String
    val accessKey:String
}

expect fun getPlatform(): Platform
expect fun httpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient
 fun initLogger(){
     Log.base(DebugAntilog())
 }

