/*
 * Copyright (C) 2022 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of RepeaTodo.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * RepeaTodo.
 */

package ru.olegivo.repeatodo.db

import co.touchlab.sqliter.DatabaseConfiguration
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.squareup.sqldelight.drivers.native.wrapConnection

actual class DriverFactoryImpl: DriverFactory {
    actual override fun createDriver(dbName: String, foreignKeyConstraints: Boolean): SqlDriver =
        NativeSqliteDriver(
            /*
            * TODO: https://github.com/cashapp/sqldelight/issues/3493
            *  Replace when will be stable release after 1.5.5
            * */
            configuration = DatabaseConfiguration(
                name = dbName,
                version = RepeaTodoDb.Schema.version,
                create = { connection ->
                    wrapConnection(connection) { RepeaTodoDb.Schema.create(it) }
                },
                upgrade = { connection, oldVersion, newVersion ->
                    wrapConnection(connection) {
                        RepeaTodoDb.Schema.migrate(
                            it,
                            oldVersion,
                            newVersion
                        )
                    }
                }
            ),
            maxReaderConnections = 1
        )
}
