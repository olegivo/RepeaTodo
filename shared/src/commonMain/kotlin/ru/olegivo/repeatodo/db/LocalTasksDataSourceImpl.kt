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
import kotlinx.coroutines.flow.Flow
import ru.olegivo.repeatodo.data.LocalTasksDataSource
import ru.olegivo.repeatodo.domain.models.Task

class LocalTasksDataSourceImpl(private val db: RepeaTodoDb) : LocalTasksDataSource {
    override fun getTasks(): Flow<List<Task>> =
        db.taskQueries
            .getTasks { uuid: String, title: String ->
                Task(uuid = uuid, title = title)
            }
            .asFlow()
            .mapToList()

    override fun getTask(uuid: String): Flow<Task?> {
        TODO("Not yet implemented")
    }

    override fun add(task: Task) {
        db.taskQueries.addTask(task.doDb())
    }

    override suspend fun update(task: Task): Boolean {
        TODO("Not yet implemented")
    }
}
