/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reverse.kmsunflower.compose

import androidx.compose.runtime.Composable
import app.cash.paging.compose.LazyPagingItems
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.reverse.kmsunflower.compose.gallery.GalleryScreen
import com.reverse.kmsunflower.compose.home.HomeScreen
import com.reverse.kmsunflower.compose.home.SunflowerPage
import com.reverse.kmsunflower.compose.plantdetail.PlantDetailsScreen
import com.reverse.kmsunflower.data.UnsplashPhoto
import com.reverse.kmsunflower.viewmodels.GalleryViewModel
import com.reverse.kmsunflower.viewmodels.GardenPlantingListViewModel
import com.reverse.kmsunflower.viewmodels.PlantDetailViewModel
import com.reverse.kmsunflower.viewmodels.PlantListViewModel
import io.github.xxfast.decompose.router.content.RoutedContent
import io.github.xxfast.decompose.router.rememberRouter

@Composable
fun SunflowerApp(
    onShareClick: (String) -> Unit,
    onPhotoClick: (UnsplashPhoto) -> Unit,
    galleryViewModel: GalleryViewModel,
    plantListViewModel: PlantListViewModel,
    gardenPlantingListViewModel: GardenPlantingListViewModel,
    plantDetailsViewModel:PlantDetailViewModel,
) {


    val router = rememberRouter(
        type = SunflowerScreen::class,
        stack = listOf(SunflowerScreen.Home)
    )
    RoutedContent(router = router) { page ->
        when (page) {
            is SunflowerScreen.Home -> HomeScreen(
                onPlantClick = { router.push(SunflowerScreen.PlantDetail(it.plantId)) },
                plantListViewModel = plantListViewModel ,
                gardenPlantingListViewModel= gardenPlantingListViewModel
            )
            is SunflowerScreen.PlantDetail -> PlantDetailsScreen(
                plantId=page.plantId,
                plantDetailsViewModel= plantDetailsViewModel,
                onBackClick = { router.pop() },
                onShareClick=onShareClick,
                onGalleryClick = { router.push(SunflowerScreen.Gallery(it.name)) }
            )
            is SunflowerScreen.Gallery -> GalleryScreen(
                plantName=page.plantName ,
                galleryViewModel=galleryViewModel,
                onPhotoClick = onPhotoClick,
                onUpClick = { router.pop() }
            )
        }
    }
}
@Parcelize
sealed class SunflowerScreen : Parcelable {
    @Parcelize
    object Home : SunflowerScreen()
    @Parcelize
    data class PlantDetail(val plantId: String) : SunflowerScreen()
    @Parcelize
    data class Gallery(val plantName: String) : SunflowerScreen()
}