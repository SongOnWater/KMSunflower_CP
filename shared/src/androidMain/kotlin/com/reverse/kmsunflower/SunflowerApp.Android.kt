package com.reverse.kmsunflower

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
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
import dev.icerock.moko.mvvm.createViewModelFactory

fun getGalleryViewModel(owner: ViewModelStoreOwner): GalleryViewModel {
    val createViewModelFactory = createViewModelFactory {
        val unsplashRepository = UnsplashRepository(UnsplashService.create())
        GalleryViewModel(unsplashRepository)
    }
    val provider = ViewModelProvider(owner, createViewModelFactory)
    return provider.get(GalleryViewModel::class.java)
}
private fun plantListViewModel(owner: ViewModelStoreOwner,database: DBHelper): PlantListViewModel {
    val createViewModelFactory = createViewModelFactory {
        val plantRepository = PlantRepository.getInstance(database.plantDao())
        PlantListViewModel(plantRepository)
    }
    val provider = ViewModelProvider(owner, createViewModelFactory)
    return provider.get(PlantListViewModel::class.java)
}
private fun gardenPlantingListViewModel(owner: ViewModelStoreOwner,database: DBHelper): GardenPlantingListViewModel {
    val createViewModelFactory = createViewModelFactory {
        val gardenPlantingRepository = GardenPlantingRepository(database.GardenPlantingDao())
        GardenPlantingListViewModel(gardenPlantingRepository)
    }
    val provider = ViewModelProvider(owner, createViewModelFactory)
    return provider.get(GardenPlantingListViewModel::class.java)
}
private fun plantDetailViewModel(owner: ViewModelStoreOwner,database: DBHelper): PlantDetailViewModel {
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
    database: DBHelper,
    owner: ViewModelStoreOwner,
    onShareClick: (String) -> Unit,
    onPhotoClick: (UnsplashPhoto) -> Unit,
) {
    SunflowerAppThemed(
        onShareClick = onShareClick,
        onPhotoClick = onPhotoClick,
        galleryViewModel = getGalleryViewModel(owner),
        plantListViewModel = plantListViewModel(owner,database),
        gardenPlantingListViewModel = gardenPlantingListViewModel(owner,database),
        plantDetailsViewModel = plantDetailViewModel(owner,database)
    )
}