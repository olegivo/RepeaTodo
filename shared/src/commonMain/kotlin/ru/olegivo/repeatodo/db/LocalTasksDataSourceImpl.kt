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

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.time.Instant
import ru.olegivo.repeatodo.DispatchersProvider
import ru.olegivo.repeatodo.domain.LocalTasksDataSource
import ru.olegivo.repeatodo.domain.Priority
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.utils.newUuid

class LocalTasksDataSourceImpl(
    private val db: RepeaTodoDb,
    private val dispatchersProvider: DispatchersProvider
): LocalTasksDataSource {
    override fun getTasks(): Flow<List<Task>> =
        db.taskQueries
            .getTasks(::toDomain)
            .asFlow()
            .flowOn(dispatchersProvider.io)
            .mapToList(dispatchersProvider.default)

    override fun getTask(uuid: String): Flow<Task?> =
        db.taskQueries
            .getTask(uuid, ::toDomain)
            .asFlow()
            .flowOn(dispatchersProvider.io)
            .mapToOneOrNull(dispatchersProvider.default)

    override suspend fun save(task: Task) {
        withContext(dispatchersProvider.io) {
            if (db.taskQueries.isTaskExists(task.uuid).executeAsOne()) {
                with(task) {
                    db.taskQueries.updateTask(
                        title = title,
                        daysPeriodicity = daysPeriodicity,
                        priority = priority,
                        toDoListUuid = toDoListUuid,
                        uuid = uuid
                    )
                }
            } else {
                db.taskQueries.addTask(task.toDb())
            }
        }
    }

    override suspend fun delete(uuid: String) {
        withContext(dispatchersProvider.io) {
            db.taskQueries.deleteTask(uuid)
        }
    }

    override suspend fun addTaskCompletion(taskUuid: String, completionDate: Instant) {
        withContext(dispatchersProvider.io) {
            db.taskCompletionQueries.addCompletion(
                TaskCompletion(
                    uuid = newUuid(),
                    taskUuid = taskUuid,
                    completionDateUtc = completionDate
                )
            )
        }
    }

    override suspend fun deleteLatestTaskCompletion(taskUuid: String) {
        withContext(dispatchersProvider.io) {
            db.taskCompletionQueries.deleteLatestTaskCompletion(taskUuid = taskUuid)
        }
    }

    private fun toDomain(
        uuid: String,
        title: String,
        daysPeriodicity: Int,
        priority: Priority?,
        toDoListUuid: String,
        lastCompletionDate: Instant?
    ) = Task(
        uuid = uuid,
        title = title,
        daysPeriodicity = daysPeriodicity,
        priority = priority,
        toDoListUuid = toDoListUuid,
        lastCompletionDate = lastCompletionDate
    )
}
