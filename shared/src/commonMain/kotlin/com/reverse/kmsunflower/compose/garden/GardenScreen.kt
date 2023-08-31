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

package com.reverse.kmsunflower.compose.garden


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.reverse.kmsunflower.MR
import com.reverse.kmsunflower.compose.card
import com.reverse.kmsunflower.compose.plantdetail.pluralStringResource
import com.reverse.kmsunflower.compose.utils.SunflowerImage
import com.reverse.kmsunflower.compose.utils.dimensionResource
import com.reverse.kmsunflower.data.PlantAndGardenPlantings
import com.reverse.kmsunflower.values.SR
import com.reverse.kmsunflower.viewmodels.GardenPlantingListViewModel
import com.reverse.kmsunflower.viewmodels.PlantAndGardenPlantingsViewModel
import dev.icerock.moko.resources.compose.stringResource


@Composable
fun GardenScreen(
    modifier: Modifier = Modifier,
    viewModel: GardenPlantingListViewModel,
    onAddPlantClick: () -> Unit,
    onPlantClick: (PlantAndGardenPlantings) -> Unit
) {
    val gardenPlants by viewModel.plantAndGardenPlantings.collectAsState(initial = emptyList())
    GardenScreen(
        gardenPlants = gardenPlants,
        modifier = modifier,
        onAddPlantClick = onAddPlantClick,
        onPlantClick = onPlantClick
    )
}

@Composable
fun GardenScreen(
    gardenPlants: List<PlantAndGardenPlantings>,
    modifier: Modifier = Modifier,
    onAddPlantClick: () -> Unit = {},
    onPlantClick: (PlantAndGardenPlantings) -> Unit = {}
) {
    if (gardenPlants.isEmpty()) {
        EmptyGarden(onAddPlantClick, modifier)
    } else {
        GardenList(gardenPlants = gardenPlants, onPlantClick = onPlantClick, modifier = modifier)
    }
}

@Composable
private fun GardenList(
    gardenPlants: List<PlantAndGardenPlantings>,
    onPlantClick: (PlantAndGardenPlantings) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Call reportFullyDrawn when the garden list has been rendered
    val gridState = rememberLazyGridState()
   // ReportDrawnWhen { gridState.layoutInfo.totalItemsCount > 0 }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier,
        state = gridState,
        contentPadding = PaddingValues(
            horizontal = dimensionResource(id = SR.dimen.card_side_margin),
            vertical = dimensionResource(id = SR.dimen.margin_normal)
        )
    ) {
        items(
            items = gardenPlants,
            key = { it.plant.plantId }
        ) {
            GardenListItem(plant = it, onPlantClick = onPlantClick)
        }
    }
}



@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
private fun GardenListItem(
    plant: PlantAndGardenPlantings,
    onPlantClick: (PlantAndGardenPlantings) -> Unit
) {
    val vm = PlantAndGardenPlantingsViewModel(plant)

    // Dimensions
    val cardSideMargin = dimensionResource(id = SR.dimen.card_side_margin)
    val marginNormal = dimensionResource(id = SR.dimen.margin_normal)

    Card(
        onClick = { onPlantClick(plant) },
        modifier = Modifier.padding(
            start = cardSideMargin,
            end = cardSideMargin,
            bottom = dimensionResource(id = SR.dimen.card_bottom_margin)
        ),
        elevation = dimensionResource(id = SR.dimen.card_elevation),
        shape = MaterialTheme.shapes.card,
    ) {
        Column(Modifier.fillMaxWidth()) {
            SunflowerImage(
                model = vm.imageUrl,
                contentDescription = plant.plant.description,
                Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = SR.dimen.plant_item_image_height)),
                contentScale = ContentScale.Crop,
            )

            // Plant name
            Text(
                text = vm.plantName,
                Modifier
                    .padding(vertical = marginNormal)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.subtitle1,
            )

            // Planted date
            Text(
                text = stringResource(MR.strings.plant_date_header),
                Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primaryVariant,
                style = MaterialTheme.typography.subtitle2
            )
            Text(
                text = vm.plantDateString,
                Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.subtitle2
            )

            // Last Watered
            Text(
                text = stringResource(MR.strings.watered_date_header),
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = marginNormal),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primaryVariant,
                style = MaterialTheme.typography.subtitle2
            )
            Text(
                text = vm.waterDateString,
                Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.subtitle2
            )
            Text(
                text = pluralStringResource(
                     MR.plurals.watering_next,
                    vm.wateringInterval.toInt(),
                    vm.wateringInterval.toInt()
                ),
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = marginNormal),
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}

@Composable
private fun EmptyGarden(onAddPlantClick: () -> Unit, modifier: Modifier = Modifier) {
    // Calls reportFullyDrawn when this composable is composed.
    //ReportDrawn()

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(MR.strings.garden_empty),
            style = MaterialTheme.typography.h5
        )
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onPrimary),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = dimensionResource(SR.dimen.button_corner_radius),
                bottomStart = dimensionResource(SR.dimen.button_corner_radius),
                bottomEnd = 0.dp,
            ),
            onClick = onAddPlantClick
        ) {
            Text(
                color = MaterialTheme.colors.primary,
                text = stringResource(MR.strings.add_plant)
            )
        }
    }
}

