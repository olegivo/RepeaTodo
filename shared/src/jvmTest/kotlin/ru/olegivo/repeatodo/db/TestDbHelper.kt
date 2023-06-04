/*
 * Copyright (C) 2023 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of RepeaTodo.
 *
 * RepeaTodo is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * RepeaTodo PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * RepeaTodo.
 */

package ru.olegivo.repeatodo.db

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.util.Properties

class TestDbHelper private constructor(
    val database: RepeaTodoDb,
    val instantLongAdapter: InstantLongAdapter,
    val driver: JdbcSqliteDriver
) {
    companion object {
        fun create(): TestDbHelper {
            val instantLongAdapter = InstantLongAdapter()
            val priorityAdapter = PriorityLongAdapter()
            val properties = Properties().apply {
                setProperty(
                    /*SQLiteConfig.Pragma.FOREIGN_KEYS*/ "foreign_keys",
                    true.toString()
                )
            }
            val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, properties)
            val driverFactory = object: DriverFactory {
                override fun createDriver(dbName: String, foreignKeyConstraints: Boolean) =
                    driver
            }
            val database = createDatabase(
                driverFactory = driverFactory,
                instantLongAdapter = instantLongAdapter,
                priorityAdapter = priorityAdapter
            )
            RepeaTodoDb.Schema.create(driver)

            return TestDbHelper(
                database = database,
                instantLongAdapter = instantLongAdapter,
                driver = driver,
            )
        }
    }
}
