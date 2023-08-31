package com.reverse.kmsunflower

class Greeting {
    private val platform: Platform = getPlatform()

    fun init(){

    }
    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}