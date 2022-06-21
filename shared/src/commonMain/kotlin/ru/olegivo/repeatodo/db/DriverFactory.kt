package ru.olegivo.repeatodo.db

import com.squareup.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(dbName: String): SqlDriver
}

private const val DbName: String = "repeatodo.db"

fun createDatabase(driverFactory: DriverFactory): RepeaTodoDb {
    val driver = driverFactory.createDriver(dbName = DbName)
    val database = RepeaTodoDb(driver)

    // Do more work with the database (see below).
    return database
}
