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

package com.reverse.kmsunflower.compose.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.moriatsushi.insetsx.statusBarsPadding
//import com.moriatsushi.insetsx.statusBarsPadding
import com.reverse.kmsunflower.MR
import com.reverse.kmsunflower.compose.plantlist.PlantListScreen
import com.reverse.kmsunflower.data.Plant
import com.reverse.kmsunflower.data.PlantAndGardenPlantings
import com.reverse.kmsunflower.viewmodels.GardenPlantingListViewModel
import com.reverse.kmsunflower.viewmodels.PlantListViewModel
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import com.reverse.kmsunflower.compose.garden.GardenScreen
import com.reverse.kmsunflower.utilities.Log
import dev.icerock.moko.mvvm.livedata.compose.observeAsState

enum class SunflowerPage(
    val titleResId: StringResource,
    val drawableResId: ImageResource
) {
    MY_GARDEN(MR.strings.my_garden_title, MR.images.ic_my_garden_active),
    PLANT_LIST(MR.strings.plant_list_title, MR.images.ic_plant_list_active)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onPlantClick: (Plant) -> Unit = {},
    plantListViewModel: PlantListViewModel,
    gardenPlantingListViewModel: GardenPlantingListViewModel,
) {

    val pagerState = rememberPagerState{
        SunflowerPage.values().size
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                pagerState = pagerState,
                onFilterClick =  { plantListViewModel.updateData() },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        HomePagerScreen(
            onPlantClick = onPlantClick,
            modifier = modifier.padding(it),
            plantListViewModel = plantListViewModel,
            gardenPlantingListViewModel = gardenPlantingListViewModel,
            pagerState = pagerState
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePagerScreen(
    onPlantClick: (Plant) -> Unit,
    modifier: Modifier = Modifier,
    pages: Array<SunflowerPage> = SunflowerPage.values(),
    gardenPlantingListViewModel: GardenPlantingListViewModel,
    plantListViewModel: PlantListViewModel,
    pagerState: PagerState
) {
    val gardenPlants by gardenPlantingListViewModel.plantAndGardenPlantings.collectAsState(initial = emptyList())
    val plants by plantListViewModel.plants.observeAsState()
    //val stateFlowPlants by flowPlants.collectAsState(initial = emptyList())
    HomePagerScreen(
        onPlantClick = onPlantClick,
        modifier = modifier,
        pages = pages,
        gardenPlants = gardenPlants,
        plants = plants,
        pagerState = pagerState

    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePagerScreen(
    onPlantClick: (Plant) -> Unit,
    modifier: Modifier = Modifier,
    pages: Array<SunflowerPage> = SunflowerPage.values(),
    gardenPlants: List<PlantAndGardenPlantings>,
    plants: List<Plant>,
    pagerState: PagerState
) {
    Column (modifier){
        val coroutineScope = rememberCoroutineScope()
        // Tab Row
        TabRow(selectedTabIndex = pagerState.currentPage) {
            pages.forEachIndexed { index, page ->
                val title = stringResource(page.titleResId)
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = title) },
                    icon = {
                        Icon(
                            painter = painterResource(page.drawableResId),
                            contentDescription = title
                        )
                    },
                    unselectedContentColor = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        // Pages
        HorizontalPager(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { index ->
            when (pages[index]) {
                SunflowerPage.MY_GARDEN -> {
                    GardenScreen(
                        gardenPlants = gardenPlants,
                        modifier = Modifier.fillMaxSize(),
                        onAddPlantClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(SunflowerPage.PLANT_LIST.ordinal)
                            }
                        },
                        onPlantClick = {
                            onPlantClick(it.plant)
                        })
                }

                SunflowerPage.PLANT_LIST -> {
                    PlantListScreen(
                        plants = plants,
                        onPlantClick = onPlantClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }


}


@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
private fun HomeTopAppBar(
    pagerState: PagerState,
    onFilterClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        //backgroundColor = Color.Red ,
        title = {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(MR.strings.app_name),
                    style = MaterialTheme.typography.displaySmall
                )
            }
        },
        modifier = modifier.statusBarsPadding(),
        actions = {
            if (pagerState.currentPage == SunflowerPage.PLANT_LIST.ordinal) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        painter = painterResource(MR.images.ic_filter_list_24dp),
                        contentDescription = stringResource(
                            MR.strings.menu_filter_by_grow_zone
                        )
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}


