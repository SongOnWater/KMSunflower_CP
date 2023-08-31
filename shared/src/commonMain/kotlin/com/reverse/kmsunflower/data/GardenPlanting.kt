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


import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * [GardenPlanting] represents when a user adds a [Plant] to their garden, with useful metadata.
 * Properties such as [lastWateringDate] are used for notifications (such as when to water the
 * plant).
 *
 * Declaring the column info allows for the renaming of variables without implementing a
 * database migration, as the column name would not change.
 */

data class GardenPlanting(
    val plantId: String,

    /**
     * Indicates when the [Plant] was planted. Used for showing notification when it's time
     * to harvest the plant.
     */
    val plantDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),

    /**
     * Indicates when the [Plant] was last watered. Used for showing notification when it's
     * time to water the plant.
     */

    val lastWateringDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
) {
    var gardenPlantingId: Long = 0

    companion object{
       private fun timestampToLocalDateTime(timestamp: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
            val instant = Instant.fromEpochMilliseconds(timestamp)
            return instant.toLocalDateTime(timeZone)
        }

        fun localDateTimeToTimestamp(localDateTime: LocalDateTime, timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
            val instant = localDateTime.toInstant(timeZone)
            return instant.toEpochMilliseconds()
        }

        fun getFromGardenPlantingTable(gpt:GardenPlantingTable):GardenPlanting{
            return GardenPlanting(gpt.plant_id,timestampToLocalDateTime(gpt.plant_date),
                timestampToLocalDateTime(gpt.last_watering_date)
            )
        }
    }
}
