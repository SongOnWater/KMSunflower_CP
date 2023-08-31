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


import androidx.compose.runtime.mutableStateOf
import com.autodesk.coroutineworker.CoroutineWorker
import com.reverse.kmsunflower.Platform
import com.reverse.kmsunflower.data.GardenPlantingRepository
import com.reverse.kmsunflower.data.Plant
import com.reverse.kmsunflower.data.PlantRepository
import com.reverse.kmsunflower.getPlatform
import com.reverse.kmsunflower.utilities.Log
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.asLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class PlantDetailViewModel  constructor(
    private val plantRepository: PlantRepository,
    private val gardenPlantingRepository: GardenPlantingRepository,
) : ViewModel() {

    fun isPlanted(plantId: String) = gardenPlantingRepository.isPlanted(plantId)

    fun  plant(plantId: String) :Flow<Plant> {
       return plantRepository.getPlant(plantId)
    }

    private val _showSnackbar = MutableLiveData(false)
    val showSnackbar: LiveData<Boolean>
        get() = _showSnackbar

    fun addPlantToGarden(plantId: String) {
        viewModelScope.launch {
            gardenPlantingRepository.createGardenPlanting(plantId)
            _showSnackbar.value = true
        }
    }

    fun dismissSnackbar() {
        _showSnackbar.value = false
    }

    fun hasValidUnsplashKey() = (getPlatform().accessKey != "null")

    companion object {
        private const val PLANT_ID_SAVED_STATE_KEY = "plantId"
    }
}