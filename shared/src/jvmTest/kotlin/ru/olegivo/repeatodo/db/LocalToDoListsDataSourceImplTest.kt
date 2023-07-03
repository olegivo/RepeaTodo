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
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.domain.LocalTasksDataSource
import ru.olegivo.repeatodo.domain.LocalToDoListsDataSource
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.domain.models.randomToDoList
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.randomString
import ru.olegivo.repeatodo.utils.newUuid
import ru.olegivo.repeatodo.domain.models.Task as TaskDomain

class LocalToDoListsDataSourceImplTest: FreeSpec() {
    init {
        "instance" - {
            val dbHelper = TestDbHelper.create()
            val database = dbHelper.database

            val localToDoListsDataSource: LocalToDoListsDataSource = LocalToDoListsDataSourceImpl(
                db = database,
                dispatchersProvider = dispatchersProvider
            )

            val toDoList1 = randomToDoList()
            "empty data source" - {

                "getToDoLists should be empty" {
                    localToDoListsDataSource.getToDoLists()
                        .assertItem { shouldBeEmpty() }
                }

//                "delete should do nothing WHEN specified not exist task uuid" {
//                    val uuid = randomString()
//
//                    localTasksDataSource.delete(uuid = uuid)
//                }

                "save" - {
                    val toDoList2 = randomToDoList()
                    localToDoListsDataSource.save(toDoList1)

                    "tasks should contain added todo-lists" {
                        localToDoListsDataSource.getToDoLists().testIn(name = "getTasks")
                            .assertItem { shouldContainExactly(toDoList1) }
                    }

                    "delete should do nothing WHEN specified not exist todo-list uuid" {
                        localToDoListsDataSource.delete(uuid = toDoList2.uuid)

                        localToDoListsDataSource.getToDoLists()
                            .assertItem { shouldContainExactly(toDoList1) }
                    }

                    "delete should delete added todo-list WHEN specified added task uuid" - {
                        localToDoListsDataSource.save(toDoList2) // one more
                        localToDoListsDataSource.expectSelect(toDoList1, toDoList2)

                        val taskUuid = newUuid()
                        val localTasksDataSource: LocalTasksDataSource = LocalTasksDataSourceImpl(
                            db = database,
                            instantLongAdapter = dbHelper.instantLongAdapter,
                            dispatchersProvider = dispatchersProvider
                        )
                        val task = TaskDomain(
                            uuid = taskUuid,
                            title = randomString(),
                            daysPeriodicity = 1,
                            priority = null,
                            toDoListUuid = toDoList1.uuid,
                            lastCompletionDate = null
                        )
                        localTasksDataSource.save(task)
                        dbHelper.createInboxToDoList()

                        localToDoListsDataSource.delete(uuid = toDoList1.uuid)

                        localToDoListsDataSource.expectSelect(toDoList2, InboxToDoList)

                        "should move tasks from deleted todo-list to inbox" {
                            localTasksDataSource.getTask(taskUuid).assertItem {
                                shouldBe(task.copy(toDoListUuid = ToDoList.Predefined.Kind.INBOX.uuid))
                            }
                        }
                    }

                    "update added todo-list" {
                        localToDoListsDataSource.save(toDoList2) // one more
                        localToDoListsDataSource.expectSelect(toDoList1, toDoList2)

                        val newVersion =
                            randomToDoList(uuid = toDoList1.uuid)
                        localToDoListsDataSource.save(newVersion)

                        localToDoListsDataSource.expectSelect(newVersion, toDoList2)
                    }
                }
            }
        }
    }

    private suspend fun LocalToDoListsDataSource.expectSelect(vararg expectedTasks: ToDoList) {
        getToDoLists().assertItem {
            shouldContainExactlyInAnyOrder(*expectedTasks)
        }
    }
}
