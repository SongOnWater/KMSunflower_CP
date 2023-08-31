/*
 * Copyright 2022 Google LLC
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

package com.reverse.kmsunflower.compose.plantlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.reverse.kmsunflower.MR
import com.reverse.kmsunflower.compose.card
import com.reverse.kmsunflower.compose.utils.SunflowerImage
import com.reverse.kmsunflower.compose.utils.dimensionResource
import com.reverse.kmsunflower.data.Plant
import com.reverse.kmsunflower.data.UnsplashPhoto
import com.reverse.kmsunflower.values.SR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PlantListItem(plant: Plant, onClick: () -> Unit) {
    ImageListItem(name = plant.name, imageUrl = plant.imageUrl, onClick = onClick)
}

@Composable
fun PhotoListItem(photo: UnsplashPhoto, onClick: () -> Unit) {
    ImageListItem(name = photo.user.name, imageUrl = photo.urls.small, onClick = onClick)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImageListItem(name: String, imageUrl: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        elevation = dimensionResource(id = SR.dimen.card_elevation),
        shape = MaterialTheme.shapes.card,
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = SR.dimen.card_side_margin))
            .padding(bottom = dimensionResource(id = SR.dimen.card_bottom_margin))
    ) {
        Column(Modifier.fillMaxWidth()) {
            SunflowerImage(
                model = imageUrl,
                contentDescription = stringResource(MR.strings.a11y_plant_item_image),
                Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(SR.dimen.plant_item_image_height)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = SR.dimen.margin_normal))
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}

