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

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.domain.FakeDateTimeProvider
import ru.olegivo.repeatodo.domain.LocalTasksDataSource
import ru.olegivo.repeatodo.domain.Priority
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.randomEnum
import ru.olegivo.repeatodo.randomNull
import ru.olegivo.repeatodo.randomString
import ru.olegivo.repeatodo.utils.newUuid
import kotlin.time.Duration.Companion.hours

class LocalTasksDataSourceImplTest: FreeSpec() {
    init {
        "instance" - {
            val dateTimeProvider = FakeDateTimeProvider()
            val dbHelper = TestDbHelper.create()
            val database = dbHelper.database

            val localTasksDataSource: LocalTasksDataSource = LocalTasksDataSourceImpl(
                db = database,
                instantLongAdapter = dbHelper.instantLongAdapter,
                dispatchersProvider = dispatchersProvider
            )
            // TODO: toDoListUuid != inbox
            // Inbox:
            dbHelper.driver.execute(
                identifier = null,
                sql = "INSERT INTO ToDoList VALUES (?, ?, ?)",
                parameters = 3
            ) {
                bindString(1, ToDoList.Predefined.Kind.INBOX.uuid)
                bindString(2, ToDoList.Predefined.Kind.INBOX.name)
                bindLong(3, 1)
            }

            // Custom:
            val customToDoListUuid = newUuid()
            val customToDoListTitle = randomString()
            dbHelper.driver.execute(
                identifier = null,
                sql = "INSERT INTO ToDoList VALUES (?, ?, ?)",
                parameters = 3
            ) {
                bindString(1, customToDoListUuid)
                bindString(2, customToDoListTitle)
                bindLong(3, 0)
            }

            val task1 = Task(
                uuid = randomString(),
                title = randomString(),
                daysPeriodicity = 1,
                priority = randomEnum<Priority>().randomNull(),
                toDoListUuid = ToDoList.Predefined.Kind.INBOX.uuid,
                lastCompletionDate = null
            )
            val task2 = Task(
                uuid = randomString(),
                title = randomString(),
                daysPeriodicity = 1,
                priority = randomEnum<Priority>().randomNull(),
                toDoListUuid = customToDoListUuid,
                lastCompletionDate = null
            )

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
                        localTasksDataSource.save(task2) // the other

                        localTasksDataSource.getTask(task1.uuid)
                            .assertItem { shouldBe(task1) }
                    }

                    "delete should do nothing WHEN specified not exist task uuid" {
                        localTasksDataSource.delete(uuid = task2.uuid)

                        localTasksDataSource.getTasks()
                            .assertItem { shouldContainExactly(task1) }
                    }

                    "delete should delete added task WHEN specified added task uuid" {
                        localTasksDataSource.save(task2) // the other
                        localTasksDataSource.expectSelect(task1, task2)

                        localTasksDataSource.delete(uuid = task1.uuid)

                        localTasksDataSource.expectSelect(task2)
                    }

                    "update added task" {
                        localTasksDataSource.save(task2) // one more
                        localTasksDataSource.expectSelect(task1, task2)

                        val newVersion =
                            Task(
                                uuid = task1.uuid,
                                lastCompletionDate = null,
                                priority = Priority.values()
                                    .filter { it != task1.priority }
                                    .random(),
                                title = randomString(),
                                daysPeriodicity = task1.daysPeriodicity + 1,
                                toDoListUuid = customToDoListUuid
                            )
                        localTasksDataSource.save(newVersion)

                        localTasksDataSource.expectSelect(newVersion, task2)
                    }
                }

                "addTaskCompletion" - {
                    localTasksDataSource.save(task1)
                    localTasksDataSource.save(task2)
                    localTasksDataSource.expectSelect(task1, task2)

                    val (d1, d2) =
                        (dateTimeProvider.getCurrentInstant() - 1.hours) to
                            (dateTimeProvider.getCurrentInstant() - 2.hours)

                    localTasksDataSource.addTaskCompletion(task1.uuid, d1)
                    localTasksDataSource.addTaskCompletion(task2.uuid, d2)

                    localTasksDataSource.expectSelect(
                        task1.copy(lastCompletionDate = d1),
                        task2.copy(lastCompletionDate = d2)
                    )

                    "delete task should delete its completions" {
                        localTasksDataSource.delete(task1.uuid)
                        localTasksDataSource.delete(task2.uuid)
                        val long = dbHelper.driver.executeQuery(
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
