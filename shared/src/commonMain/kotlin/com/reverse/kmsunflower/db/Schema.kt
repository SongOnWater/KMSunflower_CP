package com.reverse.kmsunflower.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.reverse.kmsunflower.data.Database
import com.reverse.kmsunflower.utilities.Log
import com.reverse.kmsunflower.workers.SeedDatabaseWorker

object Schema : SqlSchema<QueryResult.Value<Unit>> by Database.Schema{
    override fun create(driver: SqlDriver): QueryResult.Value<Unit>{
        Log.i("Schema create()")
        Database.Schema.create(driver)
        val seedWorker= SeedDatabaseWorker(DBHelper.getInstance(driver))
        seedWorker.doWork()
        return QueryResult.Unit
    }
}