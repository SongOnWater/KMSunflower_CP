package com.reverse.kmsunflower

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.reverse.kmsunflower.utilities.Log
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import java.util.concurrent.TimeUnit

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
     override val accessKey: String
         get() =  BuildConfig.UNSPLASH_ACCESS_KEY

    override fun init() {

    }
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(OkHttp) {
    config(this)

    engine {
        config {
            retryOnConnectionFailure(true)
            connectTimeout(5, TimeUnit.SECONDS)
        }
    }
}
// 在 androidMain 中
@Composable
actual fun getScreenDensity(): Float {
    Log.i("Android ScreenDensity")
    val context = LocalContext.current // 获取当前的 Android Context
    return context.resources.displayMetrics.density
}

//actual fun initLogger() {
//    Napier.base(DebugAntilog())
//}
