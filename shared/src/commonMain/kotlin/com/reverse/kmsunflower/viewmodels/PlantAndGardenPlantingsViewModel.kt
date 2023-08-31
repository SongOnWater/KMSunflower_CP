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


import com.reverse.kmsunflower.data.PlantAndGardenPlantings
import kotlinx.datetime.LocalDateTime


class PlantAndGardenPlantingsViewModel(plantings: PlantAndGardenPlantings) {

    private val plant = checkNotNull(plantings.plant)
    private val gardenPlanting = plantings.gardenPlantings[0]

    val waterDateString: String = dateFormat.format(gardenPlanting.lastWateringDate)
    val wateringInterval
        get() = plant.wateringInterval
    val imageUrl
        get() = plant.imageUrl
    val plantName
        get() = plant.name
    val plantDateString: String = dateFormat.format(gardenPlanting.plantDate)
    val plantId
        get() = plant.plantId

    companion object {
        private val dateFormat = DateTimeFormatter()
    }
}
class DateTimeFormatter(){
    fun format(dataTime:LocalDateTime): String {
        dataTime.apply {
            val month = this.month.name.take(3) // 获取月份的前三个字母
            return "$month ${this.dayOfMonth}, ${this.year}"
        }
    }
}