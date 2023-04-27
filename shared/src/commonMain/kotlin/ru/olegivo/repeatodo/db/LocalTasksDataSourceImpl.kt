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

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import ru.olegivo.repeatodo.DispatchersProvider
import ru.olegivo.repeatodo.data.LocalTasksDataSource
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.utils.newUuid

class LocalTasksDataSourceImpl(
    private val db: RepeaTodoDb,
    private val localDateTimeLongAdapter: LocalDateTimeLongAdapter,
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
            .mapToOneOrNull()

    override suspend fun save(task: Task) {
        withContext(dispatchersProvider.io) {
            db.taskQueries.saveTask(task.toDb())
        }
    }

    override suspend fun delete(uuid: String) {
        withContext(dispatchersProvider.io) {
            db.taskQueries.deleteTask(uuid)
        }
    }

    override suspend fun addTaskCompletion(taskUuid: String, completionDate: LocalDateTime) {
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
        lastCompletionDate: Long?
    ) = Task(
        uuid = uuid,
        title = title,
        daysPeriodicity = daysPeriodicity,
        lastCompletionDate = lastCompletionDate?.toLocalDateTime()
    )

    private fun Long.toLocalDateTime() = localDateTimeLongAdapter.decode(this)
}
