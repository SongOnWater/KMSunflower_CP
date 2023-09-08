package com.reverse.kmsunflower

import androidx.compose.runtime.*
import com.reverse.kmsunflower.api.UnsplashService
import com.reverse.kmsunflower.compose.SunflowerAppThemed
import com.reverse.kmsunflower.db.DBHelper
import com.reverse.kmsunflower.data.GardenPlantingRepository
import com.reverse.kmsunflower.data.PlantRepository
import com.reverse.kmsunflower.data.UnsplashPhoto
import com.reverse.kmsunflower.data.UnsplashRepository
import com.reverse.kmsunflower.viewmodels.GalleryViewModel
import com.reverse.kmsunflower.viewmodels.GardenPlantingListViewModel
import com.reverse.kmsunflower.viewmodels.PlantDetailViewModel
import com.reverse.kmsunflower.viewmodels.PlantListViewModel
import kotlinx.coroutines.Dispatchers

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
private fun gardenPlantingListViewModel(database: DBHelper): GardenPlantingListViewModel {
    val gardenPlantingRepository = GardenPlantingRepository(database.GardenPlantingDao())
    return GardenPlantingListViewModel(gardenPlantingRepository)
}
private fun plantDetailViewModel(database: DBHelper): PlantDetailViewModel {
    val plantRepository = PlantRepository.getInstance(database.PlantDao())
    val gardenPlantingRepository = GardenPlantingRepository(database.GardenPlantingDao())
    return PlantDetailViewModel(plantRepository, gardenPlantingRepository)
}
private fun plantListViewModel(database: DBHelper): PlantListViewModel {
      val plantRepository = PlantRepository.getInstance(database.plantDao())
    return PlantListViewModel(plantRepository)
}

@Composable
internal fun SunflowerAppIOS(
    database :DBHelper,
    onShareClick: (String) -> Unit,
    onPhotoClick: (UnsplashPhoto) -> Unit,
) {
    SunflowerAppThemed(
        onShareClick = onShareClick,
        onPhotoClick = onPhotoClick,
        galleryViewModel = getGalleryViewModel(),
        plantListViewModel = plantListViewModel(database),
        gardenPlantingListViewModel = gardenPlantingListViewModel(database),
        plantDetailsViewModel = plantDetailViewModel(database)
    )
}