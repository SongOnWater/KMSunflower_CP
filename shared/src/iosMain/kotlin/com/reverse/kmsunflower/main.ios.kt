package com.reverse.kmsunflower

import platform.UIKit.UIViewController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.decompose.DefaultComponentContext
import androidx.compose.runtime.CompositionLocalProvider
import io.github.xxfast.decompose.LocalComponentContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.reverse.kmsunflower.db.DBHelper
import com.reverse.kmsunflower.data.UnsplashPhoto
import com.reverse.kmsunflower.compose.utils.commonConfig
import com.reverse.kmsunflower.workers.SeedDatabaseWorker
import com.seiko.imageloader.DefaultIOS
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import com.reverse.kmsunflower.db.Schema
import androidx.compose.ui.window.ComposeUIViewController
import app.cash.sqldelight.driver.native.NativeSqliteDriver

@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController =
    ComposeUIViewController {
        //val database= DBHelper.getInstance(NativeSqliteDriver(Schema,"Plants.db",100))
        val database= initDatabase()
        val lifecycle = LifecycleRegistry()
        val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)
        CompositionLocalProvider(LocalComponentContext provides rootComponentContext,
            LocalImageLoader provides remember { generateImageLoader() }
            ) {
            val ioScope: CoroutineScope = rememberCoroutineScope {  Dispatchers.IO }
            SunflowerAppIOS(
                database,
                onShareClick ={ sharePlant(ioScope,it)},
                ::openPhotoInBrowser)
        }
    }
private fun generateImageLoader(): ImageLoader {
    return ImageLoader {
        takeFrom(ImageLoader.DefaultIOS)
        commonConfig()
    }
//     return ImageLoader {
//         commonConfig()
//         components {
//             setupDefaultComponents()
//         }
//         interceptor {
//             memoryCacheConfig {
//                 maxSizeBytes(32 * 1024 * 1024) // 32MB
//             }
//             diskCacheConfig {
//                 directory(getCacheDir().toPath().resolve("image_cache"))
//                 maxSizeBytes(512L * 1024 * 1024) // 512MB
//             }
//         }
//     }
}
 private fun getCacheDir(): String {
     return NSSearchPathForDirectoriesInDomains(
         NSCachesDirectory,
         NSUserDomainMask,
         true,
     ).first() as String
 }
fun initDatabase():DBHelper{
    val database= DBHelper.getInstance(NativeSqliteDriver(Schema,"Plants.db",100))
    if(!database.PlantDao().hasPopulatedData()){
        val seedWorker=SeedDatabaseWorker(database)
        seedWorker.doWork()
    }
    return database
}

 fun sharePlant(ioScope: CoroutineScope ,plant:String){
     ioScope.launch {
         withContext(Dispatchers.Main){
             val window = UIApplication.sharedApplication.windows.last() as? UIWindow
             val currentViewController = window?.rootViewController
             val activityViewController = UIActivityViewController(
                 activityItems = listOf(plant),
                 applicationActivities = null
             )
             currentViewController?.presentViewController(
                 viewControllerToPresent = activityViewController,
                 animated = true,
                 completion = null
             )
         }
     }
}
private fun openPhotoInBrowser(photo: UnsplashPhoto){
    val url = NSURL(string =photo.user.attributionUrl)
    url.let {
        UIApplication.sharedApplication.openURL(it)
    }

}
