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

package com.reverse.kmsunflower.compose.plantlist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.reverse.kmsunflower.compose.utils.dimensionResource
import com.reverse.kmsunflower.data.Plant
import com.reverse.kmsunflower.values.SR
import com.reverse.kmsunflower.viewmodels.PlantListViewModel
import dev.icerock.moko.mvvm.livedata.compose.observeAsState


//@Composable
//fun PlantListScreen(
//    onPlantClick: (Plant) -> Unit,
//    modifier: Modifier = Modifier,
//    viewModel: PlantListViewModel,
//) {
//    val plants by mutableStateOf(viewModel.plants)
//    PlantListScreen(plants = plants.value, modifier, onPlantClick = onPlantClick)
//}

@Composable
fun PlantListScreen(
    plantListViewModel: PlantListViewModel,
    modifier: Modifier = Modifier,
    onPlantClick: (Plant) -> Unit = {},
) {

    val plants by plantListViewModel.plants.observeAsState()

    if(plants.isEmpty()){
        plantListViewModel.refreshPlants()
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.testTag("plant_list"),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(id = SR.dimen.card_side_margin),
            vertical = dimensionResource(id = SR.dimen.header_margin)
        )
    ) {
        items(
            items = plants,
            key = { it.plantId }
        ) { plant ->
            PlantListItem(plant = plant) {
                onPlantClick(plant)
            }
        }
    }
}
