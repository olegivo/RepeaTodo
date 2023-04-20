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

package ru.olegivo.repeatodo.data

import io.kotest.core.spec.IsolationMode
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.domain.TasksRepository
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.domain.models.randomTask
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.kotest.LifecycleMode
import ru.olegivo.repeatodo.randomString

internal class TasksRepositoryImplTest : FreeSpec(LifecycleMode.Root) {

    override fun isolationMode() = IsolationMode.InstancePerLeaf

    init {
        "TasksRepositoryImpl created" - {
            val task = randomTask()
            val localTasksDataSource = FakeLocalTasksDataSource()

            val tasksRepository: TasksRepository = TasksRepositoryImpl(localTasksDataSource)

            "empty data source" - {

                "getTasks should be empty" {
                    tasksRepository.getTasks().assertItem { shouldBeEmpty() }
                }

                "get should return null WHEN specified not exist task uuid" {
                    tasksRepository.getTask(uuid = task.uuid).assertItem { shouldBeNull() }
                }

                "delete should do nothing WHEN specified not exist task uuid" {
                    val uuid = randomString()

                    tasksRepository.delete(uuid = uuid)

                    localTasksDataSource.deletedTasksUuids shouldNotContain uuid
                }

                "save" - {
                    tasksRepository.save(task)

                    "tasks should contain added task" {
                        tasksRepository.getTasks().assertItem { shouldBe(listOf(task)) }
                    }

                    "get should return null WHEN specified not exist task uuid" {
                        tasksRepository.getTask(uuid = randomString()).assertItem { shouldBeNull() }
                    }

                    "get should return added task WHEN specified added task uuid" {
                        tasksRepository.getTask(uuid = task.uuid).assertItem { shouldBe(task) }
                    }

                    "delete should do nothing WHEN specified not exist task uuid" {
                        val uuid = randomString()

                        tasksRepository.delete(uuid = uuid)

                        localTasksDataSource.deletedTasksUuids shouldNotContain uuid
                        localTasksDataSource.getTasks().value shouldContain task
                    }

                    "delete should delete added task WHEN specified added task uuid" {
                        tasksRepository.delete(uuid = task.uuid)

                        localTasksDataSource.deletedTasksUuids shouldContain task.uuid
                        localTasksDataSource.getTasks().value shouldNotContain task
                    }

                    "update added task" {
                        val newVersion = randomTask().copy(uuid = task.uuid)
                        tasksRepository.save(newVersion)

                        localTasksDataSource.getTasks().value.apply {
                            shouldNotContain(task)
                            shouldContain(newVersion)
                        }
                    }
                }
            }
        }
    }

    class FakeLocalTasksDataSource : LocalTasksDataSource {

        private val tasks = MutableStateFlow(listOf<Task>())
        val deletedTasksUuids = mutableListOf<String>()

        override fun getTasks(): StateFlow<List<Task>> = tasks

        override fun save(task: Task) {
            tasks.update { prev -> prev.filter { it.uuid != task.uuid } + task }
        }

        override fun delete(uuid: String) {
            if (tasks.value.any { task -> task.uuid == uuid }) {
                tasks.update { prev -> prev.filter { it.uuid != uuid } }
                deletedTasksUuids += uuid
            }
        }

        override fun getTask(uuid: String) =
            tasks.map { it.firstOrNull { task -> task.uuid == uuid } }
    }
}