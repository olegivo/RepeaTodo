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

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.domain.TasksRepository
import ru.olegivo.repeatodo.domain.models.Task

class FakeTasksRepository : TasksRepository {

    var lastAddedTask: Task? = null
        private set

    private val tasks = MutableStateFlow<List<Task>>(emptyList())

    override fun getTasks() = tasks

    override suspend fun save(task: Task) {
        tasks.update { prev -> prev.filter { it.uuid != task.uuid } + task }
        lastAddedTask = task
    }

    override suspend fun delete(uuid: String) {
        tasks.update { prev -> prev.filter { it.uuid != uuid } }
    }

    override fun getTask(uuid: String) =
        tasks.map { it.firstOrNull { task -> task.uuid == uuid } }
}
