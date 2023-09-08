package com.reverse.kmsunflower.android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.core.app.ShareCompat
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.reverse.kmsunflower.SunflowerAppAndroid
import com.reverse.kmsunflower.data.UnsplashPhoto
import com.reverse.kmsunflower.compose.utils.commonConfig
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.option.androidContext
import io.github.xxfast.decompose.LocalComponentContext
import okio.Path.Companion.toOkioPath
import androidx.core.view.WindowCompat
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.reverse.kmsunflower.db.DBHelper
import com.reverse.kmsunflower.db.Schema

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
       val database= DBHelper.getInstance(AndroidSqliteDriver(Schema,this,"Plants.db"))
        val rootComponentContext: DefaultComponentContext = defaultComponentContext()
        setContent {
            SetSystemUI()
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
@Composable
private fun SetSystemUI(){
    val view = LocalView.current
    if (!view.isInEditMode) {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = !isSystemInDarkTheme()
        val window = (view.context as Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        DisposableEffect(systemUiController, useDarkIcons) {
            // Update all of the system bar colors to be transparent, and use
            // dark icons if we're in light theme
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
            onDispose {}
        }
    }
}