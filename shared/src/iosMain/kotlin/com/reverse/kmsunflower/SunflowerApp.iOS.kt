package com.reverse.kmsunflower

import androidx.compose.runtime.*
import com.reverse.kmsunflower.api.UnsplashService
import com.reverse.kmsunflower.compose.SunflowerAppThemed
import com.reverse.kmsunflower.data.AppDatabase
import com.reverse.kmsunflower.data.DatabaseDriverFactory
import com.reverse.kmsunflower.data.GardenPlantingRepository
import com.reverse.kmsunflower.data.PlantRepository
import com.reverse.kmsunflower.data.UnsplashPhoto
import com.reverse.kmsunflower.data.UnsplashRepository
import com.reverse.kmsunflower.viewmodels.GalleryViewModel
import com.reverse.kmsunflower.viewmodels.GardenPlantingListViewModel
import com.reverse.kmsunflower.viewmodels.PlantDetailViewModel
import com.reverse.kmsunflower.viewmodels.PlantListViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIWindow

//fun share(context: PlatformContext, picture: PictureData) {
//    ioScope.launch {
//        val imageStorage: IosImageStorage = IosImageStorage(pictures, ioScope)
//        val data = imageStorage.getNSDataToShare(picture)
//        withContext(Dispatchers.Main) {
//            val window = UIApplication.sharedApplication.windows.last() as? UIWindow
//            val currentViewController = window?.rootViewController
//            val activityViewController = UIActivityViewController(
//                activityItems = listOf(
//                    UIImage(data = data),
//                    picture.description
//                ),
//                applicationActivities = null
//            )
//            currentViewController?.presentViewController(
//                viewControllerToPresent = activityViewController,
//                animated = true,
//                completion = null,
//            )
//        }
//    }
//}


fun getGalleryViewModel(): GalleryViewModel {
    val unsplashRepository = UnsplashRepository(UnsplashService.create())
    return GalleryViewModel(unsplashRepository)
}
private fun gardenPlantingListViewModel(database: AppDatabase): GardenPlantingListViewModel {
    val gardenPlantingRepository = GardenPlantingRepository(database.GardenPlantingDao())
    return GardenPlantingListViewModel(gardenPlantingRepository)
}
private fun plantDetailViewModel(database: AppDatabase): PlantDetailViewModel {
    val plantRepository = PlantRepository.getInstance(database.PlantDao())
    val gardenPlantingRepository = GardenPlantingRepository(database.GardenPlantingDao())
    return PlantDetailViewModel(plantRepository, gardenPlantingRepository)
}
private fun plantListViewModel(database: AppDatabase): PlantListViewModel {
      val plantRepository = PlantRepository.getInstance(database.plantDao())
    return PlantListViewModel(plantRepository)
}

@Composable
internal fun SunflowerAppIOS(
    onShareClick: (String) -> Unit,
    onPhotoClick: (UnsplashPhoto) -> Unit,
) {
    val database=AppDatabase.getInstance(DatabaseDriverFactory(),Dispatchers.Default)
    SunflowerAppThemed(
        onShareClick = onShareClick,
        onPhotoClick = onPhotoClick,
        galleryViewModel = getGalleryViewModel(),
        plantListViewModel = plantListViewModel(database),
        gardenPlantingListViewModel = gardenPlantingListViewModel(database),
        plantDetailsViewModel = plantDetailViewModel(database)
    )
}