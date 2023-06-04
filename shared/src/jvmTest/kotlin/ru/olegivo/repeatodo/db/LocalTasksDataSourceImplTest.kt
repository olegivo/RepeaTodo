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
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.domain.LocalTasksDataSource
import ru.olegivo.repeatodo.domain.FakeDateTimeProvider
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.domain.models.randomTask
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.randomString
import java.util.Properties
import kotlin.time.Duration.Companion.hours

class LocalTasksDataSourceImplTest: FreeSpec() {
    init {
        "instance" - {
            val dateTimeProvider = FakeDateTimeProvider()
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

            val localTasksDataSource: LocalTasksDataSource = LocalTasksDataSourceImpl(
                db = database,
                instantLongAdapter = instantLongAdapter,
                dispatchersProvider = dispatchersProvider
            )

            val task1 = randomTask(lastCompletionDate = null)

            "empty data source" - {

                "getTasks should be empty" {
                    localTasksDataSource.getTasks()
                        .assertItem { shouldBeEmpty() }
                }

                "get should return null WHEN specified not exist task uuid" {
                    localTasksDataSource.getTask(task1.uuid)
                        .assertItem { shouldBeNull() }
                }

                "delete should do nothing WHEN specified not exist task uuid" {
                    val uuid = randomString()

                    localTasksDataSource.delete(uuid = uuid)
                }

                "save" - {
                    val task2 = randomTask(lastCompletionDate = null)
                    localTasksDataSource.save(task1)

                    "tasks should contain added tasks" {
                        localTasksDataSource.getTasks().testIn(name = "getTasks")
                            .assertItem { shouldContainExactly(task1) }
                    }

                    "get should return null WHEN specified not exist task uuid" {
                        localTasksDataSource.getTask(uuid = task2.uuid)
                            .assertItem { shouldBeNull() }
                    }

                    "get should return added task WHEN specified added task uuid" {
                        localTasksDataSource.save(task2) // one more

                        localTasksDataSource.getTask(task1.uuid)
                            .assertItem { shouldBe(task1) }
                    }

                    "delete should do nothing WHEN specified not exist task uuid" {
                        localTasksDataSource.delete(uuid = task2.uuid)

                        localTasksDataSource.getTasks()
                            .assertItem { shouldContainExactly(task1) }
                    }

                    "delete should delete added task WHEN specified added task uuid" {
                        localTasksDataSource.save(task2) // one more
                        localTasksDataSource.expectSelect(task1, task2)

                        localTasksDataSource.delete(uuid = task1.uuid)

                        localTasksDataSource.expectSelect(task2)
                    }

                    "update added task" {
                        localTasksDataSource.save(task2) // one more
                        localTasksDataSource.expectSelect(task1, task2)

                        val newVersion = randomTask(lastCompletionDate = null).copy(uuid = task1.uuid)
                        localTasksDataSource.save(newVersion)

                        localTasksDataSource.expectSelect(newVersion, task2)
                    }
                }

                "addTaskCompletion" - {
                    val task1 = Task(
                        uuid = randomString(),
                        title = randomString(),
                        daysPeriodicity = 1,
                        priority = null,
                        lastCompletionDate = null
                    )
                    val task2 = Task(
                        uuid = randomString(),
                        title = randomString(),
                        daysPeriodicity = 1,
                        priority = null,
                        lastCompletionDate = null
                    )
                    localTasksDataSource.save(task1)
                    localTasksDataSource.save(task2)
                    localTasksDataSource.expectSelect(task1, task2)

                    val (d1, d2) = with(dateTimeProvider.getCurrentTimeZone()) {
                        (dateTimeProvider.getCurrentInstant() - 1.hours) to
                            (dateTimeProvider.getCurrentInstant() - 2.hours)
                    }

                    localTasksDataSource.addTaskCompletion(task1.uuid, d1)
                    localTasksDataSource.addTaskCompletion(task2.uuid, d2)

                    localTasksDataSource.expectSelect(
                        task1.copy(lastCompletionDate = d1),
                        task2.copy(lastCompletionDate = d2)
                    )

                    "delete task should delete its completions" {
                        localTasksDataSource.delete(task1.uuid)
                        localTasksDataSource.delete(task2.uuid)
                        val long = driver.executeQuery(
                            null,
                            "select count(*) from TaskCompletion",
                            0
                        ).use {
                            it.getLong(0)
                        }
                        long shouldBe 0L
                    }
                }

                "deleteLatestTaskCompletion" - {
                    val task1 = Task(
                        uuid = randomString(),
                        title = randomString(),
                        daysPeriodicity = 1,
                        priority = null,
                        lastCompletionDate = null
                    )
                    val task2 = Task(
                        uuid = randomString(),
                        title = randomString(),
                        daysPeriodicity = 1,
                        priority = null,
                        lastCompletionDate = null
                    )
                    localTasksDataSource.save(task1)
                    localTasksDataSource.save(task2)
                    localTasksDataSource.expectSelect(task1, task2)

                    val (d1, d2) = with(dateTimeProvider.getCurrentTimeZone()) {
                        (dateTimeProvider.getCurrentInstant() - 1.hours) to
                            (dateTimeProvider.getCurrentInstant() - 2.hours)
                    }

                    localTasksDataSource.addTaskCompletion(task1.uuid, d1)
                    localTasksDataSource.addTaskCompletion(task1.uuid, d2)
                    localTasksDataSource.addTaskCompletion(task2.uuid, d1)
                    localTasksDataSource.addTaskCompletion(task2.uuid, d2)

                    localTasksDataSource.expectSelect(
                        task1.copy(lastCompletionDate = d1),
                        task2.copy(lastCompletionDate = d1)
                    )

                    "delete 1/2 completion" - {
                        localTasksDataSource.deleteLatestTaskCompletion(task1.uuid)

                        localTasksDataSource.expectSelect(
                            task1.copy(lastCompletionDate = d2),
                            task2.copy(lastCompletionDate = d1)
                        )

                        "delete 2/2 completion" {
                            localTasksDataSource.deleteLatestTaskCompletion(task1.uuid)

                            localTasksDataSource.expectSelect(
                                task1.copy(lastCompletionDate = null),
                                task2.copy(lastCompletionDate = d1)
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun LocalTasksDataSource.expectSelect(vararg expectedTasks: Task) {
        getTasks().assertItem {
            shouldContainExactlyInAnyOrder(*expectedTasks)
        }
        expectedTasks.forEach { task ->
            getTask(task.uuid).assertItem { shouldBe(task) }
        }
    }
}
