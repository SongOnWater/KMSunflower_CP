package com.reverse.kmsunflower.android

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.core.app.ShareCompat
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.reverse.kmsunflower.SunflowerAppAndroid
import com.reverse.kmsunflower.android.MyApplication.Companion.database
import com.reverse.kmsunflower.compose.KMSunflowerTheme
import com.reverse.kmsunflower.data.UnsplashPhoto
import com.reverse.kmsunflower.compose.utils.commonConfig
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.option.androidContext
import io.github.xxfast.decompose.LocalComponentContext
import okio.Path.Companion.toOkioPath
import com.google.accompanist.themeadapter.material.MdcTheme
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // WindowCompat.setDecorFitsSystemWindows(window, false)
        val rootComponentContext: DefaultComponentContext = defaultComponentContext()
        setContentView(ComposeView(this).apply {
            consumeWindowInsets = false
            setContent {
                KMSunflowerTheme {
                    CompositionLocalProvider(LocalComponentContext provides rootComponentContext,
                        LocalImageLoader provides remember { generateImageLoader() },
                    ) {
                        SunflowerAppAndroid(
                            database= database,
                            owner=this@MainActivity,
                            onShareClick = ::sharePlant,
                            onPhotoClick = ::openPhotoInBrowser
                        )
                    }
                }

            }
        })

    }
    private fun generateImageLoader(): ImageLoader {
//        return ImageLoader {
//            takeFrom(ImageLoader.DefaultAndroid(applicationContext).config)
//            commonConfig()
//        }
         return ImageLoader {
             commonConfig()
             options {
                 androidContext(applicationContext)
             }
             components {
                 setupDefaultComponents()
             }
             interceptor {
                 memoryCacheConfig {
                     // Set the max size to 25% of the app's available memory.
                     maxSizePercent(applicationContext, 0.25)
                 }
                 diskCacheConfig {
                     directory(cacheDir.resolve("image_cache").toOkioPath())
                     maxSizeBytes(512L * 1024 * 1024) // 512MB
                 }
             }
         }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun sharePlant(plantName: String) {
        val shareText = getString( R.string.app_share_text_plant,plantName)
        val shareIntent = ShareCompat.IntentBuilder(this@MainActivity)
            .setText(shareText)
            .setType("text/plain")
            .createChooserIntent()
            .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            startActivity(shareIntent)
    }
    private fun openPhotoInBrowser(photo: UnsplashPhoto){
        val uri = Uri.parse(photo.user.attributionUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}