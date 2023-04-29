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

import com.squareup.sqldelight.db.SqlDriver

interface DriverFactory {
    fun createDriver(dbName: String, foreignKeyConstraints: Boolean): SqlDriver
}

expect class DriverFactoryImpl: DriverFactory {
    override fun createDriver(dbName: String, foreignKeyConstraints: Boolean): SqlDriver
}

private const val DbName: String = "repeatodo.db"

fun createDatabase(
    driverFactory: DriverFactory,
    localDateTimeLongAdapter: LocalDateTimeLongAdapter
): RepeaTodoDb {

    val driver = driverFactory.createDriver(
        dbName = DbName,
        foreignKeyConstraints = true
    )
    val database = RepeaTodoDb(driver, TaskCompletion.Adapter(localDateTimeLongAdapter))

    // Do more work with the database (see below).
    return database
}
