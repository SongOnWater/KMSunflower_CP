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

package com.reverse.kmsunflower.compose.gallery

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.cash.paging.compose.LazyPagingItems
import com.reverse.kmsunflower.MR
import com.reverse.kmsunflower.compose.plantlist.PhotoListItem
import com.reverse.kmsunflower.compose.utils.dimensionResource
import com.reverse.kmsunflower.data.UnsplashPhoto
import com.reverse.kmsunflower.values.SR
import com.reverse.kmsunflower.viewmodels.GalleryViewModel
import dev.icerock.moko.resources.compose.stringResource
import app.cash.paging.compose.collectAsLazyPagingItems

@Composable
fun GalleryScreen(
    plantName:String,
    galleryViewModel: GalleryViewModel,
    onPhotoClick: (UnsplashPhoto) -> Unit = {},
    onUpClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            GalleryTopBar(onUpClick = onUpClick)
        },
    ) { padding ->
        val pagingItems: LazyPagingItems<UnsplashPhoto> = galleryViewModel.plantPictures(plantName).collectAsLazyPagingItems()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(all = dimensionResource(id = SR.dimen.card_side_margin))
        ) {
            // TODO update this implementation once paging Compose supports LazyGridScope
            // See: https://issuetracker.google.com/issues/178087310
            items(
                count = pagingItems.itemCount,
                key = { index ->
                    val photo = pagingItems[index]
                    "${ photo?.id ?: ""}${index}"
                }
            ) { index ->
                val photo = pagingItems[index] ?: return@items
                PhotoListItem(photo = photo) {
                    onPhotoClick(photo)
                }
            }
        }
    }
}

@Composable
private fun GalleryTopBar(
    onUpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(stringResource(MR.strings.gallery_title))
        },
        //modifier = modifier.statusBarsPadding(),
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
    )
}
