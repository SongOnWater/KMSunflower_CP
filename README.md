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
|A-resource|moko-resource|
|A-MVVM |moko-mvvm|
|A-Text |richeditor|
|Celendar |kotlinx-datetime|
|Hilt | |
|WindowInsetsPadding |insetsx |
|Navigation |decompose-router |
|parcelize | arkivanov-parcelize|
|A-CoroutineWorker | autodesk-coroutineWorker|
## Network 
Orignal code :
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
## Data 
  ### Database Room VS Sqldelight 
  On Android Room offers up-to-down database structure definition, it maps entity to table row, DAO to operations and even helps to deal joint query. But Sqldelight bring down-to-up style one. You should write the SQL sentences in  .sq files, Sqldelight  will generate entities and functions for your according to the SQL sentences. 
 There are following noticing points:
 1. Room has callback after creating database, which could be used to insert inital data into DB. On Sqldelight the place is create() in Schema, but that not works on iOS for me, so I have to check the whether the data has been seeded or not everytime App launches(Needs Improments). 
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
 2. Sqldelight offers XXXQueries which is DAO in Room has functions. You write some data transport or transform code to map data to Flow.
 3.  Sqldelight creates entities for each table structure, but the entities may not suitable for ViewModel, you should define a corresponding entity for ViewModel. 
  
