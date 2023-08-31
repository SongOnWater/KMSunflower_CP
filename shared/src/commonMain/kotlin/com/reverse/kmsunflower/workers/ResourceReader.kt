package com.reverse.kmsunflower.workers

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource


class ResourceReader {
   @OptIn(ExperimentalResourceApi::class)
   suspend fun readText(file: String):String{
      return  resource(file).readBytes().decodeToString()
   }
}