package ru.olegivo.repeatodo.db

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(dbName: String): SqlDriver {
        return AndroidSqliteDriver(RepeaTodoDb.Schema, context, dbName)
    }
}
