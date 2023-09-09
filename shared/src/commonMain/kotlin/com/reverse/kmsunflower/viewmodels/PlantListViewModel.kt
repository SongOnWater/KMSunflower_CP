/*
 * Copyright 2018 Google LLC
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

package com.reverse.kmsunflower.viewmodels


import com.reverse.kmsunflower.data.Plant
import com.reverse.kmsunflower.data.PlantRepository
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.asLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

//class PlantListViewModel(
//    private val plantRepository: PlantRepository,
//    private val _growZone : MutableStateFlow<Int?>
//) {
//
//   // private val _growZone = MutableStateFlow<Int?>(null)
//    //val growZone: StateFlow<Int?> get() = _growZone
//
//    val plants: Flow<List<Plant>> = _growZone.flatMapLatest { zone ->
//        when (zone) {
//            null, NO_GROW_ZONE -> plantRepository.getPlants()
//            else -> plantRepository.getPlantsWithGrowZoneNumber(zone)
//        }
//    }
//
//    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
//
//    init {
//        coroutineScope.launch {
//            _growZone.collect { newGrowZone ->
//                // Handle saving the grow zone if needed.
//                // This can be done using a multiplatform settings library.
//            }
//        }
//    }
//
//    fun updateData() {
//        if (isFiltered()) {
//            clearGrowZoneNumber()
//        } else {
//            setGrowZoneNumber(9)
//        }
//    }
//
//    fun setGrowZoneNumber(num: Int) {
//        _growZone.value = num
//    }
//
//    fun clearGrowZoneNumber() {
//        _growZone.value = NO_GROW_ZONE
//    }
//
//    fun isFiltered() = _growZone.value != NO_GROW_ZONE
//
//    companion object {
//        private const val NO_GROW_ZONE = -1
//    }
//}

class PlantListViewModel   constructor(
    private val plantRepository: PlantRepository
) : ViewModel() {

//    private val growZone: MutableStateFlow<Int> = MutableStateFlow(
//        savedStateHandle[GROW_ZONE_SAVED_STATE_KEY] ?: NO_GROW_ZONE
//    )
    private val growZone: MutableStateFlow<Int> = MutableStateFlow(
        NO_GROW_ZONE
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val plants: LiveData<List<Plant>> = growZone.flatMapLatest { zone ->
        if (zone == NO_GROW_ZONE) {
            plantRepository.getPlants()
        } else {
            plantRepository.getPlantsWithGrowZoneNumber(zone)
        }
    }.asLiveData(viewModelScope, emptyList())

    fun refreshPlants(){
        plantRepository.refreshPlants()
    }

    fun updateData() {
        if (isFiltered()) {
            clearGrowZoneNumber()
        } else {
            setGrowZoneNumber(9)
        }
    }

    fun setGrowZoneNumber(num: Int) {
        growZone.value = num
    }

    fun clearGrowZoneNumber() {
        growZone.value = NO_GROW_ZONE
    }

    fun isFiltered() = growZone.value != NO_GROW_ZONE

    companion object {
        private const val NO_GROW_ZONE = -1
    }
}