package com.reverse.kmsunflower

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import platform.UIKit.UIDevice

private class IOSPlatform: Platform {
    companion object{
        private val instance =  IOSPlatform()
        fun getInstance():Platform= instance
    }
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val accessKey: String //TODO should get from info.plist
        get() = "iLUMv3gYqzJ21-56XgGI1lok5mOU8_cI36PUxQwfOPs"
}

actual fun getPlatform(): Platform = IOSPlatform.getInstance()
actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(Darwin) {
    config(this)

    engine {
        configureRequest {
            setAllowsCellularAccess(true)
        }
    }
}
