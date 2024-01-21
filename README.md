# Sunflower migration to KMP/CMP 

[Sunflow](https://github.com/android/sunflower) is a sample project of Google to demonstrate Jetpack Components and Compose. This project has migrated it to  Kotlin/Compose Multiplatform. The main work of migration is replacing the Android Jetpack Components and libraries to corresponding multiplatform ones.
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
Retrofit maps a fun to a server API with annotations then implements the real function by generating codes according your annotation. But you should implements the function manually. There are the following notable points:

  1. BuildConfig can't be multiplatform so the UNSPLASH_ACCESS_KEY will be get by platform specifically.
  2. @GET is an endpoint in Ktor.
  3. @Query is parameter in Ktor.

## Database Room VS Sqldelight 
On Android Room offers up-to-down database structure definition, it maps entity to table row, DAO to operations and even helps to deal joint query. But Sqldelight bring down-to-up style one. You should write the SQL sentences in  .sq files, Sqldelight  will generate entities and functions for your according to the SQL sentences. 
There are following notable points:
1. Room has callback after creating database, which could be used to insert inital data into DB. On Sqldelight the place is create() in Schema, but that not works on iOS for me, so I have to check the whether the data has been seeded or not every time App launches(Needs Optimization).
 
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
Android separates "assets" and "res" with defining "assets" as raw material accessed by path and IO ,  "res" defined as values accessed by ID. Moko resource offers API used to access some of "res" as on Android. And  "compose.components.resources" offer API to access "assets".(But version 1.5.1  has a middle path issue ["compose-resources"](https://github.com/JetBrains/compose-multiplatform/issues/3637) on iOS).
For "res", there are some tricks:
1. Moko resource can only deal SVG, so we should convert Android drawables into SVG files.
2. Actually we could use ChatGPT to convert some values defined in XML file into kotlin values, by telling ChatGPT the convert rules with converting example.  
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
The above code fetches Json string from a file in "assets", then parse the Json string into a  list of entities, and insert the entities into the database as initial data. There ares some points :
 1. CoroutineWorker of Android and Multiplatform.
 2. Json of Android and Multiplatform.
### Data Seeding
For Android Room has **RoomDatabase.Callback** to seed initial data into database, and Room also helps you deal the synchronization issues of querying while seeding immediately. Even thought We can seed data in Sqldelight in Schema::Create(), but the seeding not yet has been saw while doing query. So I add a mechanism to refresh the data passed through viewmodel.
```kotlin
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
}
class PlantRepository{
    fun refreshPlants()=plantDao.refreshPlants()
}
class PlantListViewModel{
    plantRepository.refreshPlants()
}
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
```
## MVVM 
As DAOs and entities already, the repositories 、 viewmodels and pagings will be easy to migration.  But as without Hilter for multiplatform, we should init the viewmodels manually.  
multiplatform code:
```kotlin
fun getGalleryViewModel(owner: ViewModelStoreOwner): GalleryViewModel {
    val createViewModelFactory = createViewModelFactory {
        val unsplashRepository = UnsplashRepository(UnsplashService.create())
        GalleryViewModel(unsplashRepository)
    }
    val provider = ViewModelProvider(owner, createViewModelFactory)
    return provider.get(GalleryViewModel::class.java)
}
private fun plantListViewModel(owner: ViewModelStoreOwner,database: DBHelper): PlantListViewModel {
    val createViewModelFactory = createViewModelFactory {
        val plantRepository = PlantRepository.getInstance(database.plantDao())
        PlantListViewModel(plantRepository)
    }
    val provider = ViewModelProvider(owner, createViewModelFactory)
    return provider.get(PlantListViewModel::class.java)
}
private fun gardenPlantingListViewModel(owner: ViewModelStoreOwner,database: DBHelper): GardenPlantingListViewModel {
    val createViewModelFactory = createViewModelFactory {
        val gardenPlantingRepository = GardenPlantingRepository(database.GardenPlantingDao())
        GardenPlantingListViewModel(gardenPlantingRepository)
    }
    val provider = ViewModelProvider(owner, createViewModelFactory)
    return provider.get(GardenPlantingListViewModel::class.java)
}
private fun plantDetailViewModel(owner: ViewModelStoreOwner,database: DBHelper): PlantDetailViewModel {
    val createViewModelFactory = createViewModelFactory {
        val plantRepository = PlantRepository.getInstance(database.PlantDao())
        val gardenPlantingRepository = GardenPlantingRepository(database.GardenPlantingDao())
        PlantDetailViewModel(plantRepository, gardenPlantingRepository)
    }
    val provider = ViewModelProvider(owner, createViewModelFactory)
    return provider.get(PlantDetailViewModel::class.java)
}
```
## Composable Views
With Compose Multiplatform , the most of the Android Composables adapter multiplatform easily, except the following positions:
1. **Navigation**, Android offers  Navigation and Parameters through it, but mulitplatform has no official Navigation framework, so there is third-party navigation decompose-router library.
Android code:
```kotlin
@Composable
fun SunflowerApp() {
    val navController = rememberNavController()
    SunFlowerNavHost(
        navController = navController
    )
}

@Composable
fun SunFlowerNavHost(
    navController: NavHostController
) {
    val activity = (LocalContext.current as Activity)
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onPlantClick = {
                    navController.navigate("plantDetail/${it.plantId}")
                }
            )
        }
        composable(
            "plantDetail/{plantId}",
            arguments = listOf(navArgument("plantId") {
                type = NavType.StringType
            })
        ) {
            PlantDetailsScreen(
                onBackClick = { navController.navigateUp() },
                onShareClick = {
                    createShareIntent(activity, it)
                },
                onGalleryClick = {
                    navController.navigate("gallery/${it.name}")
                }
            )
        }
        composable(
            "gallery/{plantName}",
            arguments = listOf(navArgument("plantName") {
                type = NavType.StringType
            })
        ) {
            GalleryScreen(
                onPhotoClick = {
                    val uri = Uri.parse(it.user.attributionUrl)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    activity.startActivity(intent)
                },
                onUpClick = {
                    navController.navigateUp()
                })
        }
    }
}
```
multiplatform code:
```kotlin
@Parcelize
sealed class SunflowerScreen : Parcelable {
    @Parcelize
    object Home : SunflowerScreen()
    @Parcelize
    data class PlantDetail(val plantId: String) : SunflowerScreen()
    @Parcelize
    data class Gallery(val plantName: String) : SunflowerScreen()
}
@Composable
fun SunflowerApp(
    onShareClick: (String) -> Unit,
    onPhotoClick: (UnsplashPhoto) -> Unit,
    galleryViewModel: GalleryViewModel,
    plantListViewModel: PlantListViewModel,
    gardenPlantingListViewModel: GardenPlantingListViewModel,
    plantDetailsViewModel:PlantDetailViewModel,
) {

    val router = rememberRouter(
        type = SunflowerScreen::class,
        stack = listOf(SunflowerScreen.Home)
    )
    RoutedContent(router = router) { page ->
        when (page) {
            is SunflowerScreen.Home -> HomeScreen(
                onPlantClick = { router.push(SunflowerScreen.PlantDetail(it.plantId)) },
                plantListViewModel = plantListViewModel ,
                gardenPlantingListViewModel= gardenPlantingListViewModel
            )
            is SunflowerScreen.PlantDetail -> {
                PlantDetailsScreen(
                    plantId=page.plantId,
                    plantDetailsViewModel= plantDetailsViewModel,
                    onBackClick = { router.pop() },
                    onShareClick=onShareClick,
                    onGalleryClick = { router.push(SunflowerScreen.Gallery(it.name)) }
                )
            }
            is SunflowerScreen.Gallery -> GalleryScreen(
                plantName=page.plantName ,
                galleryViewModel=galleryViewModel,
                onPhotoClick = onPhotoClick,
                onUpClick = { router.pop() }
            )
        }
    }
}
```
Above router logic should be wrapped by 'CompositionLocalProvider(LocalComponentContext provides rootComponentContext)' from platform specific code which offers a LocalComponentContext. 
2. **ConstraintLayout**, Android offers both View and Composable ConstraintLayout API, but multiplatform has no such feature. But after analysis the code actually, there is no need to use ConstraintLayout to implements the layouts. 
Android code:
```kotlin
@Composable
private fun PlantDetailsContent(
    scrollState: ScrollState,
    toolbarState: ToolbarState,
    plant: Plant,
    isPlanted: Boolean,
    hasValidUnsplashKey: Boolean,
    imageHeight: Dp,
    onNamePosition: (Float) -> Unit,
    onFabClick: () -> Unit,
    onGalleryClick: () -> Unit,
    contentAlpha: () -> Float,
) {
    Column(Modifier.verticalScroll(scrollState)) {
        ConstraintLayout {
            val (image, fab, info) = createRefs()

            PlantImage(
                imageUrl = plant.imageUrl,
                imageHeight = imageHeight,
                modifier = Modifier
                    .constrainAs(image) { top.linkTo(parent.top) }
                    .alpha(contentAlpha())
            )

            if (!isPlanted) {
                val fabEndMargin = Dimens.PaddingSmall
                PlantFab(
                    onFabClick = onFabClick,
                    modifier = Modifier
                        .constrainAs(fab) {
                            centerAround(image.bottom)
                            absoluteRight.linkTo(
                                parent.absoluteRight,
                                margin = fabEndMargin
                            )
                        }
                        .alpha(contentAlpha())
                )
            }

            PlantInformation(
                name = plant.name,
                wateringInterval = plant.wateringInterval,
                description = plant.description,
                hasValidUnsplashKey = hasValidUnsplashKey,
                onNamePosition = { onNamePosition(it) },
                toolbarState = toolbarState,
                onGalleryClick = onGalleryClick,
                modifier = Modifier.constrainAs(info) {
                    top.linkTo(image.bottom)
                }
            )
        }
    }
}
```
multiplatform code:
```kotlin
@Composable
private fun PlantDetailsContent(
    scrollState: ScrollState,
    toolbarState: ToolbarState,
    plant: Plant,
    isPlanted: Boolean,
    hasValidUnsplashKey: Boolean,
    imageHeight: Dp,
    onNamePosition: (Float) -> Unit,
    onFabClick: () -> Unit,
    onGalleryClick: () -> Unit,
    contentAlpha: () -> Float,
) {
    Column(Modifier.verticalScroll(scrollState).fillMaxWidth()) {
            PlantImage(
                imageUrl = plant.imageUrl,
                imageHeight = imageHeight,
                modifier = Modifier
                    .alpha(contentAlpha())
            )
            if (!isPlanted) {
                var boxSize by remember { mutableStateOf(IntSize.Zero) }
                PlantFab(
                    onFabClick = onFabClick,
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            boxSize = coordinates.size
                        }
                        .align(Alignment.End)
                        .padding(end=Dimens.PaddingSmall)
                        .offset(y= (boxSize.height/(2*LocalDensity.current.density)).unaryMinus().dp)
                        .alpha(contentAlpha())
                )
            }

            PlantInformation(
                name = plant.name,
                wateringInterval = plant.wateringInterval.toInt(),
                description = plant.description,
                hasValidUnsplashKey = hasValidUnsplashKey,
                onNamePosition = { onNamePosition(it) },
                toolbarState = toolbarState,
                onGalleryClick = onGalleryClick
            )

    }
}
```
The key points to get ride of the ConstraintLayout is that using **onGloballyPositioned** and **offset** to layout the target view upward half of its hight.
3. **HTML**, Android Text can render HTML as rich text. But official Multiplatform has no such feature, so I use a third-party library [Compose-Rich-Editor](https://github.com/MohamedRejeb/Compose-Rich-Editor) 
Android code:
```kotlin
@Composable
private fun PlantDescription(description: String) {
    // This remains using AndroidViewBinding because this feature is not in Compose yet
    AndroidViewBinding(ItemPlantDescriptionBinding::inflate) {
        plantDescription.text = HtmlCompat.fromHtml(
            description,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        plantDescription.movementMethod = LinkMovementMethod.getInstance()
        plantDescription.linksClickable = true
    }
}
```
multiplatform code:
```kotlin
@OptIn(ExperimentalRichTextApi::class)
@Composable
private fun PlantDescription(description: String) {

    val richTextState = rememberRichTextState().apply {
        setHtml(description)
        setConfig(
            linkColor = MaterialTheme.colorScheme.primary
        )
    }
    RichText(
       state = richTextState,
        modifier = Modifier.padding(bottom = Dimens.PaddingLargeX)
   )
}
```
Note: 'Modifier.padding(bottom = Dimens.PaddingLargeX)' should not be missed, it is the answer to ' android:paddingBottom="@dimen/padding_large" ' on Android, or the scrolling effect will not work well. 
## Image
There are lots of image loading libraries for Android, such as Glide, some of them even have composable versions, but they don't support multiplanform currently. There is one image loading [compose-imageloader](https://github.com/qdsfdhvh/compose-imageloader) library supporting KMP and it seems the best choice currently.  
Android code:
```kotlin
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SunflowerImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    requestBuilderTransform: RequestBuilderTransform<Drawable> = { it },
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier.background(Color.Magenta))
        return
    }
    GlideImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        requestBuilderTransform = requestBuilderTransform,
        loading = placeholder {
            Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(40.dp))
            }
        }
    )
}
```
multiplatform code:
```kotlin
@Composable
fun SunflowerImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    var boxSize by remember { mutableStateOf(Size.Zero) }
    val density = LocalDensity.current.density
    Box(Modifier.onGloballyPositioned { coordinates ->
        boxSize = coordinates.size.toSize()
    }, Alignment.Center) {
        val request = remember(model) {
            ImageRequest {
                data(model)
                scale(Scale.FIT)
                addInterceptor(NullDataInterceptor)
                options {
                    maxImageSize = (boxSize.width.toInt()* density).toInt()
                }
            }
        }
        val action by rememberImageAction(request)
        val painter = rememberImageActionPainter(action)
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter
        )
    }
}
```
Note: 'maxImageSize = (boxSize.width.toInt()* density).toInt()' this line is for loading big size image on iOS. Without the line the memory will be drained soon then causes a crash. This line has set the width of view used show the image as the "maxImageSize" to scale down its size while loading big images. 
## Conclusion
As Kotlin Multiplatform and Compose Multiplatform has implemented the multiplatform feature for the lower layer of programming language and higher layer of Composable views separately, the main work for migration is the replacement of middle layer of components and third-party libraries. But the components and libraries supporting multiplatform may have many difference from their peers on Android. So the ideal situation is when the majority components and libraries are able to support multiplatform, then do the migration for your Android code. 

If your App is developed from scratch, using multiplatform maybe a reasonable choice. But you may face lots uncertainty, as you are using a new framework and being a newbie. 

The advantages of Kotlin Multiplatform and Compose Multiplatform is that if you are an Android developer, you do not need to learn a new language and APIs of a new platform. But your experience on Android, especially on the third-party libraries will not match. So maybe KMP/CMP is a relatively easier way to wade into Multiplatform area. 

Compared with other multiplatform technologies, such as Flutter, React Native, KMP/CMP is still immature. So how to convince developers to adapt it is a big issue. Also like other  multiplatform technologies, it is inevitable to deal with platform specific problems, so developers  have to learn some platform specific knowledges, that seems like a paradox.  
