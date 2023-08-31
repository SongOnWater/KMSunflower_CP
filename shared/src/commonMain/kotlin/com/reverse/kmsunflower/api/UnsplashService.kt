/*
 * Copyright 2020 Google LLC
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

package com.reverse.kmsunflower.api

import com.reverse.kmsunflower.data.UnsplashSearchResponse
import com.reverse.kmsunflower.getPlatform
import com.reverse.kmsunflower.httpClient
import com.reverse.kmsunflower.initLogger
import com.reverse.kmsunflower.utilities.Log
import io.ktor.client.plugins.BodyProgress.Plugin.install
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLBuilder
import io.ktor.http.path
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json

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