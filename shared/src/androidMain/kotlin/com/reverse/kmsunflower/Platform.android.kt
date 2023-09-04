package com.reverse.kmsunflower
import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import java.util.concurrent.TimeUnit

class AndroidPlatform : Platform {
    private lateinit var context:Context
    companion object{
         val instance=AndroidPlatform()
        fun init(context: Context) {
            instance.context=context
        }
    }
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
     override val accessKey: String
         get() =  BuildConfig.UNSPLASH_ACCESS_KEY
}

actual fun getPlatform(): Platform = AndroidPlatform.instance
actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(OkHttp) {
    config(this)

    engine {
        config {
            retryOnConnectionFailure(true)
            connectTimeout(5, TimeUnit.SECONDS)
        }
    }
}



