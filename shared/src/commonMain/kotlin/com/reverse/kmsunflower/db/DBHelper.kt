package com.reverse.kmsunflower.db

import com.reverse.kmsunflower.data.Database
import com.reverse.kmsunflower.data.GardenPlanting
import com.reverse.kmsunflower.data.Plant
import com.reverse.kmsunflower.data.PlantAndGardenPlantings
import com.reverse.kmsunflower.helpers.transactionWithContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlin.native.concurrent.ThreadLocal


class DBHelper private constructor(
    driver: SqlDriver,
    private val backgroundDispatcher: CoroutineDispatcher
) {

    private val database = Database(driver)
    private val plantQueries = database.plantQueries
    private val gardenPlantingsQueries = database.gardenPlantingQueries
    private val plantDao = PlantDao()
    public fun plantDao() = plantDao

    @ThreadLocal
    companion object {
        private var instance: DBHelper? = null
        fun getInstance(driver: SqlDriver, backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default): DBHelper {
            return instance ?: DBHelper(driver,backgroundDispatcher).also {
                instance = it
            }
        }
    }


    inner class PlantDao {

        fun hasPopulatedData(): Boolean {
            return plantQueries.getPlantsCount().executeAsOne() != 0L
        }

        fun getPlants(): Flow<List<Plant>> {
            val rawPlants=plantQueries.getPlants()
            val plantList=rawPlants.executeAsList()
             val  tPlantList = plantList.map {
                    pt ->
                 Plant.getFromPlantTable(pt)
            }
            return flowOf(tPlantList).flowOn(backgroundDispatcher)
        }

        fun getPlantsWithGrowZoneNumber(growZoneNumber: Int): Flow<List<Plant>> {
            return plantQueries.getPlantsWithGrowZoneNumber(growZoneNumber.toLong()).asFlow().map {
                it.executeAsList().map { pt ->
                    Plant.getFromPlantTable(pt)
                }
            }.flowOn(backgroundDispatcher)
        }

        fun getPlant(plantId: String): Flow<Plant> {
            return flowOf(plantQueries.getPlant(plantId).executeAsOne()).map { pt ->
                Plant.getFromPlantTable(pt)
            }
        }

        suspend fun insertAll(plants: List<Plant>) {
            database.transactionWithContext(backgroundDispatcher){
                plants.forEach {
                    plantQueries.insertPlant(
                        it.plantId,
                        it.name,
                        it.description,
                        it.growZoneNumber,
                        it.wateringInterval,
                        it.imageUrl
                    )
                }
            }
        }

    }


    inner class GardenPlantingDao {

        fun getGardenPlantings(): Flow<List<GardenPlanting>> {
            return gardenPlantingsQueries.getAllGardenPlantings().asFlow().map {
                it.executeAsList().map { gpt ->
                    GardenPlanting.getFromGardenPlantingTable(gpt)
                }
            }.flowOn(backgroundDispatcher)
        }

        fun isPlanted(plantId: String): Flow<Boolean?> {
            return flowOf(gardenPlantingsQueries.isPlanted(plantId).executeAsOne())
        }

        fun getPlantedGardens(): Flow<List<PlantAndGardenPlantings>> {
            return gardenPlantingsQueries.getPlantedGardens().asFlow().map {
                it.executeAsList().map { pt ->
                 val gardenPlantings=   gardenPlantingsQueries.getGardenPlantingsByPlantId(pt.id).executeAsList().map {
                     GardenPlanting.getFromGardenPlantingTable(it)
                 }
                    PlantAndGardenPlantings(Plant.getFromPlantTable(pt), gardenPlantings)
                }
            }.flowOn(backgroundDispatcher)
        }

        suspend fun insertGardenPlanting(gardenPlanting: GardenPlanting) {

            gardenPlantingsQueries.insertGardenPlanting(
                gardenPlanting.plantId,
                GardenPlanting.localDateTimeToTimestamp(gardenPlanting.plantDate),
                GardenPlanting.localDateTimeToTimestamp(gardenPlanting.lastWateringDate)
            )
        }

        suspend fun deleteGardenPlanting(gardenPlanting: GardenPlanting) {
            gardenPlantingsQueries.deleteGardenPlanting(gardenPlanting.plantId)
        }

    }

}