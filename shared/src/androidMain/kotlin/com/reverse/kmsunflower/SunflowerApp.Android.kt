package com.reverse.kmsunflower

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.reverse.kmsunflower.api.UnsplashService
import com.reverse.kmsunflower.compose.SunflowerApp
import com.reverse.kmsunflower.data.AppDatabase
import com.reverse.kmsunflower.data.GardenPlantingRepository
import com.reverse.kmsunflower.data.PlantRepository
import com.reverse.kmsunflower.data.UnsplashPhoto
import com.reverse.kmsunflower.data.UnsplashRepository
import com.reverse.kmsunflower.viewmodels.GalleryViewModel
import com.reverse.kmsunflower.viewmodels.GardenPlantingListViewModel
import com.reverse.kmsunflower.viewmodels.PlantDetailViewModel
import com.reverse.kmsunflower.viewmodels.PlantListViewModel
import dev.icerock.moko.mvvm.createViewModelFactory

fun getGalleryViewModel(owner: ViewModelStoreOwner): GalleryViewModel {
    val createViewModelFactory = createViewModelFactory {
        val unsplashRepository = UnsplashRepository(UnsplashService.create())
        GalleryViewModel(unsplashRepository)
    }
    val provider = ViewModelProvider(owner, createViewModelFactory)
    return provider.get(GalleryViewModel::class.java)
}
private fun plantListViewModel(owner: ViewModelStoreOwner,database: AppDatabase): PlantListViewModel {
    val createViewModelFactory = createViewModelFactory {
        val plantRepository = PlantRepository.getInstance(database.plantDao())
        PlantListViewModel(plantRepository)
    }
    val provider = ViewModelProvider(owner, createViewModelFactory)
    return provider.get(PlantListViewModel::class.java)
}
private fun gardenPlantingListViewModel(owner: ViewModelStoreOwner,database: AppDatabase): GardenPlantingListViewModel {
    val createViewModelFactory = createViewModelFactory {
        val gardenPlantingRepository = GardenPlantingRepository(database.GardenPlantingDao())
        GardenPlantingListViewModel(gardenPlantingRepository)
    }
    val provider = ViewModelProvider(owner, createViewModelFactory)
    return provider.get(GardenPlantingListViewModel::class.java)
}
private fun plantDetailViewModel(owner: ViewModelStoreOwner,database: AppDatabase): PlantDetailViewModel {
    val createViewModelFactory = createViewModelFactory {
        val plantRepository = PlantRepository.getInstance(database.PlantDao())
        val gardenPlantingRepository = GardenPlantingRepository(database.GardenPlantingDao())
        PlantDetailViewModel(plantRepository, gardenPlantingRepository)
    }
    val provider = ViewModelProvider(owner, createViewModelFactory)
    return provider.get(PlantDetailViewModel::class.java)
}
@Composable
fun SunflowerAppAndroid(
    database: AppDatabase,
    owner: ViewModelStoreOwner,
    onShareClick: (String) -> Unit,
    onPhotoClick: (UnsplashPhoto) -> Unit,
) {
    SunflowerApp(
        onShareClick = onShareClick,
        onPhotoClick = onPhotoClick,
        galleryViewModel = getGalleryViewModel(owner),
        plantListViewModel = plantListViewModel(owner,database),
        gardenPlantingListViewModel = gardenPlantingListViewModel(owner,database),
        plantDetailsViewModel = plantDetailViewModel(owner,database)
    )
}