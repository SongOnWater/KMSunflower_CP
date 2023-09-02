package com.reverse.kmsunflower

import androidx.compose.runtime.Composable
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import platform.UIKit.UIDevice
import platform.UIKit.UIScreen
import com.reverse.kmsunflower.utilities.Log
import platform.UIKit.UIApplication
private class IOSPlatform: Platform {
    companion object{
        private val instance =  IOSPlatform()
        fun getInstance():Platform= instance
    }
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val accessKey: String //TODO should get from info.plist
        get() = "iLUMv3gYqzJ21-56XgGI1lok5mOU8_cI36PUxQwfOPs"
    override val screenDensity = UIScreen.mainScreen.scale.toFloat()
    override val topSafeAreaHeight: Float= 64f
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
