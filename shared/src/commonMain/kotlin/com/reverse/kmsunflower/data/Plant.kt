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

package com.reverse.kmsunflower.data

import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Plant(
    @SerialName("plantId") val plantId: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("growZoneNumber") val growZoneNumber: Long,
    @SerialName("wateringInterval") val wateringInterval: Long = 7, // how often the plant should be watered, in days
    @SerialName("imageUrl") val imageUrl: String = ""
) {

    /**
     * Determines if the plant should be watered.  Returns true if [since]'s date > date of last
     * watering + watering Interval; false otherwise.
     */
    fun shouldBeWatered(since: LocalDate, lastWateringDate: LocalDate): Boolean {
        val daysUntilNextWatering = lastWateringDate.daysUntil(since)
        return daysUntilNextWatering >= wateringInterval
    }

    override fun toString() = name

    companion object{
        fun getFromPlantTable(pt: PlantTable):Plant{
            return Plant(pt.id,pt.name,pt.description,pt.growZoneNumber,pt.wateringInterval,pt.imageUrl)
        }
        val emptyPlant=Plant("","","",0)
    }
}


