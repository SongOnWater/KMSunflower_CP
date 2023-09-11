# Sunflower migration to KMP/CMP 

[Sunflow](https://github.com/android/sunflower) is a sample project of Google to demonstrate Jetpack Components and Compose. This project has migrated it to  Kotlin/Compose Mutiplatform. The main work of migration is replace the Android Jetpack Components and libraries to corresponding mutliplatform ones.
Here is the main replacement mapping.
|Android(A)|Multiplatform|
| ------- | ------- |
|retrofit2|ktor|
|room|cash-sqldelight|
|glide|image-loader|
|A-Logger|napier|
|paging|cash-paging|
|A-assets|moko-resource|
|A-MVVM |moko-mvvm|
|A-Text |richeditor|
|Celendar |kotlinx-datetime|
|Hilt | |
|WindowInsetsPadding |insetsx |
|Navigation |decompose-router |
|parcelize | arkivanov-parcelize|
|A-CoroutineWorker | autodesk-coroutineWorker|
## Network 
Android code :
```kotlin
interface UnsplashService {
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("client_id") clientId: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): UnsplashSearchResponse

    companion object {
        private const val BASE_URL = "https://api.unsplash.com/"

        fun create(): UnsplashService {
            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UnsplashService::class.java)
        }
    }
}
```
multiplatform code :
```kotlin
class UnsplashService {
    companion object {
        private const val BASE_URL = "https://api.unsplash.com/"
        private val unsplashService=UnsplashService()
        fun create(): UnsplashService {
            return unsplashService
        }
    }
    private val httpClient = httpClient() {
        install(Logging) {
            level = LogLevel.HEADERS
            logger = object : Logger {
                override fun log(message: String) {
                    Log.v(tag = "HTTP Client", message = message)
                }
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }.also { initLogger() }

    suspend fun searchPhotos(
        query: String,
        page: Int,
         perPage: Int,
        clientId: String = getPlatform().accessKey
    ): UnsplashSearchResponse{
        val endpoint = "search/photos"
        val url = URLBuilder().takeFrom(BASE_URL).apply { path(endpoint) }.build()

        return  httpClient.get(url){
           parameter("query", query)
           parameter("page", page)
           parameter("per_page", perPage)
           parameter("client_id", clientId)
       }.body()
    }

}
```
Retrofit maps a fun to a server API with annotations then implements the real function by generating codes according your annotation. But you should implements the function manually. There are the following noticing points:

  1. BuildConfig can't be multiplatform so the UNSPLASH_ACCESS_KEY will be get by platform specifically.
  2. @GET is an endpoint in Ktor.
  3. @Query is parameter in Ktor.

## Database Room VS Sqldelight 
On Android Room offers up-to-down database structure definition, it maps entity to table row, DAO to operations and even helps to deal joint query. But Sqldelight bring down-to-up style one. You should write the SQL sentences in  .sq files, Sqldelight  will generate entities and functions for your according to the SQL sentences. 
There are following noticing points:
1. Room has callback after creating database, which could be used to insert inital data into DB. On Sqldelight the place is create() in Schema, but that not works on iOS for me, so I have to check the whether the data has been seeded or not everytime App launches(Needs Optimzation).
 
 Android code :
```kotlin
@Database(entities = [GardenPlanting::class, Plant::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gardenPlantingDao(): GardenPlantingDao
    abstract fun plantDao(): PlantDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
                                    .setInputData(workDataOf(KEY_FILENAME to PLANT_DATA_FILENAME))
                                    .build()
                            WorkManager.getInstance(context).enqueue(request)
                        }
                    }
                )
                .build()
        }
    }
}
```
multiplatform code :
```kotlin
object Schema : SqlSchema<QueryResult.Value<Unit>> by Database.Schema{
    override fun create(driver: SqlDriver): QueryResult.Value<Unit>{
        Log.i("Schema::create()")
        Database.Schema.create(driver)
        val dbHelper=DBHelper.getInstance(driver)
        val seedWorker= SeedDatabaseWorker(dbHelper)
        seedWorker.doWork()
        return QueryResult.Unit
    }
}
```
2. Room offers DAO, Sqldelight offers XXXQueries accordingly. Dao  returns Flow for each data fetch operation, but  Sqldelight returns Query<Entity> object, so you should write some data transport or transform code to map Query<Entity> object to Flow.

Android code :

```kotlin

@Dao
interface PlantDao {
@Query("SELECT * FROM plants ORDER BY name")
fun getPlants(): Flow<List<Plant>>

@Query("SELECT * FROM plants WHERE growZoneNumber = :growZoneNumber ORDER BY name")
fun getPlantsWithGrowZoneNumber(growZoneNumber: Int): Flow<List<Plant>>

@Query("SELECT * FROM plants WHERE id = :plantId")
fun getPlant(plantId: String): Flow<Plant>

@Upsert
suspend fun upsertAll(plants: List<Plant>)
}

```
multiplatform code :

```kotlin
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
    fun getInstance(
        driver: SqlDriver,
        backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default
    ): DBHelper {
        return instance ?: DBHelper(driver, backgroundDispatcher).also {
            instance = it
        }
    }
}


inner class PlantDao {
    private val refreshTrigger = MutableStateFlow(false)

    fun hasPopulatedData(): Boolean {
        return plantQueries.getPlantsCount().executeAsOne() != 0L
    }

    fun refreshPlants() {
        refreshTrigger.value = !refreshTrigger.value
    }

    fun getPlants(): Flow<List<Plant>> {
        return refreshTrigger.flatMapLatest {
            val rawPlants = plantQueries.getPlants()
            val plantList = rawPlants.executeAsList()
            val tPlantList = plantList.map { pt ->
                Plant.getFromPlantTable(pt)
            }
            Log.i("PlantDao::getPlants() tPlantList.size:${tPlantList.size}")
            flowOf(tPlantList)
        }.flowOn(backgroundDispatcher)
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
        database.transactionWithContext(backgroundDispatcher) {
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
            Log.i("PlantDao::insertAll()")
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
                val gardenPlantings =
                    gardenPlantingsQueries.getGardenPlantingsByPlantId(pt.id).executeAsList()
                        .map {
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

```
3. Room maps entity to table. Sqldelight creates entities for each table defined by SQL, but the entities may not be suitable for         ViewModel. You should define a corresponding entity for ViewModel.

Android code:

```kotlin
@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey @ColumnInfo(name = "id") val plantId: String,
    val name: String,
    val description: String,
    val growZoneNumber: Int,
    val wateringInterval: Int = 7, // how often the plant should be watered, in days
    val imageUrl: String = ""
) {

    /**
     * Determines if the plant should be watered.  Returns true if [since]'s date > date of last
     * watering + watering Interval; false otherwise.
     */
    fun shouldBeWatered(since: Calendar, lastWateringDate: Calendar) =
        since > lastWateringDate.apply { add(DAY_OF_YEAR, wateringInterval) }

    override fun toString() = name
}
```
multiplatform code:

 ```kotlin
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
 ```
There are some gists in above code:
1. @Serializable from kotlinx.serialization attached data class.
2. @SerialName from kotlinx.serialization attached to fields.
3. Calendar From Android converse to LocalDate. 
## assets Vs resource
Android sparates "assets" and "res" with defining "assets" as raw materal accessed by path and IO ,  "res" defined as values accessed by ID. Moko resource offers API used to access some of "res" as on Android. And  "compose.components.resources" offer API to access "assets".(But version 1.5.1  has a middle path issue ["compose-resources"](https://github.com/JetBrains/compose-multiplatform/issues/3637) on iOS).
For "res", there are some magics:
1. Moko resource can only deal SVG, so we should convert Android drawables into SVG files.
2. Actually we could use ChatGPT to convert some valuses defined in XML file into kotlin values, by telling ChatGPT the convert rules with converting example.  
Android code:
```kotlin
class SeedDatabaseWorker(
        context: Context,
        workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val filename = inputData.getString(KEY_FILENAME)
            if (filename != null) {
                applicationContext.assets.open(filename).use { inputStream ->
                    JsonReader(inputStream.reader()).use { jsonReader ->
                        val plantType = object : TypeToken<List<Plant>>() {}.type
                        val plantList: List<Plant> = Gson().fromJson(jsonReader, plantType)

                        val database = AppDatabase.getInstance(applicationContext)
                        database.plantDao().upsertAll(plantList)

                        Result.success()
                    }
                }
            } else {
                Log.e(TAG, "Error seeding database - no valid filename")
                Result.failure()
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SeedDatabaseWorker"
        const val KEY_FILENAME = "PLANT_DATA_FILENAME"
    }
}
 ```
multiplatform code:

 ```kotlin
class ResourceReader {
   @OptIn(ExperimentalResourceApi::class)
   suspend fun readText(file: String):String{
      return  resource(file).readBytes().decodeToString()
   }
}
class SeedDatabaseWorker(
    private val database: DBHelper
) {
    fun doWork() {
        CoroutineWorker.execute {
            try {
                val fileResource = "json/plants.json"
                val resourceReader = ResourceReader()
                val fileContent = resourceReader.readText(fileResource)
                val plantList: List<Plant> = Json.decodeFromString(fileContent)
                 database.plantDao().insertAll(plantList)
                Log.i("SeedDatabaseWorker::doWork()")
            } catch (ex: Exception) {
                Log.e(ex.message.toString())
            }
        }
    }
}
```
The above code fetches Json string from a file in "assets", then parse the Json string into a  list of entities, and insert the entities into the database as inital data. There ares some pionts :
 1. CoroutineWorker of Android and Multiplatform.
 2. Json of Android and Multiplatform.
## MVVM 
As DAOs and entities already, the respositories and viewmodels will be easy to migration.   
