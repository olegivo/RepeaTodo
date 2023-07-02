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
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.domain.LocalToDoListsDataSource
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.domain.models.randomToDoList
import ru.olegivo.repeatodo.kotest.FreeSpec

class LocalToDoListsDataSourceImplTest: FreeSpec() {
    init {
        "instance" - {
            val database = TestDbHelper.create().database

            val localTasksDataSource: LocalToDoListsDataSource = LocalToDoListsDataSourceImpl(
                db = database,
                dispatchersProvider = dispatchersProvider
            )

            val toDoList1 = randomToDoList()
            "empty data source" - {

                "getToDoLists should be empty" {
                    localTasksDataSource.getToDoLists()
                        .assertItem { shouldBeEmpty() }
                }

//                "delete should do nothing WHEN specified not exist task uuid" {
//                    val uuid = randomString()
//
//                    localTasksDataSource.delete(uuid = uuid)
//                }

                "save" - {
                    val toDoList2 = randomToDoList()
                    localTasksDataSource.save(toDoList1)

                    "tasks should contain added tasks" {
                        localTasksDataSource.getToDoLists().testIn(name = "getTasks")
                            .assertItem { shouldContainExactly(toDoList1) }
                    }

                    "delete should do nothing WHEN specified not exist task uuid" {
                        localTasksDataSource.delete(uuid = toDoList2.uuid)

                        localTasksDataSource.getToDoLists()
                            .assertItem { shouldContainExactly(toDoList1) }
                    }

                    "delete should delete added task WHEN specified added task uuid" {
                        localTasksDataSource.save(toDoList2) // one more
                        localTasksDataSource.expectSelect(toDoList1, toDoList2)

                        localTasksDataSource.delete(uuid = toDoList1.uuid)

                        localTasksDataSource.expectSelect(toDoList2)
                    }

                    "update added task" {
                        localTasksDataSource.save(toDoList2) // one more
                        localTasksDataSource.expectSelect(toDoList1, toDoList2)

                        val newVersion =
                            randomToDoList(uuid = toDoList1.uuid)
                        localTasksDataSource.save(newVersion)

                        localTasksDataSource.expectSelect(newVersion, toDoList2)
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
