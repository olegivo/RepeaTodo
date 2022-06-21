package ru.olegivo.repeatodo.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DriverFactory {
    actual fun createDriver(dbName: String): SqlDriver {
        return NativeSqliteDriver(RepeaTodoDb.Schema, dbName)
    }
}
