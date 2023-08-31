package com.reverse.kmsunflower.data

import com.squareup.sqldelight.db.SqlDriver


expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}