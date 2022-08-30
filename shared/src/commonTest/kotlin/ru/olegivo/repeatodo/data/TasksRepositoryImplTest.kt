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
import kotlinx.coroutines.flow.*
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.domain.TasksRepository
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.domain.models.createTask
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.kotest.LifecycleMode
import ru.olegivo.repeatodo.randomString

internal class TasksRepositoryImplTest : FreeSpec(LifecycleMode.Root) {

    override fun isolationMode() = IsolationMode.InstancePerLeaf

    init {
        "TasksRepositoryImpl created" - {
            val task = createTask()
            val localTasksDataSource = FakeLocalTasksDataSource()

            val tasksRepository: TasksRepository = TasksRepositoryImpl(localTasksDataSource)

            "empty data source" - {

                "tasks should be empty" {
                    tasksRepository.getTasks().assertItem { shouldBeEmpty() }
                }

                "update not exist before should add the task" {
                    tasksRepository.update(task)

                    localTasksDataSource.getTasks().assertItem {
                        shouldContain(task)
                    }
                }

                "add" - {
                    tasksRepository.add(task)

                    "tasks should contain added task" {
                        tasksRepository.getTasks().assertItem { shouldBe(listOf(task)) }
                    }

                    "get should return null WHEN specified not exist task uuid" {
                        tasksRepository.getTask(uuid = randomString()).assertItem { shouldBeNull() }
                    }

                    "get should return added task WHEN specified added task uuid" {
                        tasksRepository.getTask(uuid = task.uuid).assertItem { shouldBe(task) }
                    }

                    "update added task" {
                        val newVersion = createTask().copy(uuid = task.uuid)
                        tasksRepository.update(newVersion)

                        localTasksDataSource.getTasks().assertItem {
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

        override fun getTasks(): Flow<List<Task>> = tasks

        override fun add(task: Task) {
            tasks.update { it + task }
        }

        override fun getTask(uuid: String) =
            tasks.map { it.firstOrNull { task -> task.uuid == uuid } }

        override suspend fun update(task: Task): Boolean {
            val exist = getTask(task.uuid).first()
            return if (exist != null) {
                tasks.update { prev -> prev.filter { it != exist } + task }
                true
            } else {
                false
            }
        }
    }
}