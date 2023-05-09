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

package ru.olegivo.repeatodo.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import ru.olegivo.repeatodo.domain.models.Task

class FakeLocalTasksDataSource: LocalTasksDataSource {

    private val tasks = MutableStateFlow(listOf<Task>())
    val deletedTasksUuids = mutableListOf<String>()
    val deletedTaskCompletionsUuids = mutableListOf<String>()
    val completedTasks = mutableListOf<TaskCompletionInvocation>()

    override fun getTasks(): StateFlow<List<Task>> = tasks

    override suspend fun save(task: Task) {
        tasks.update { prev -> prev.filter { it.uuid != task.uuid } + task }
    }

    override suspend fun delete(uuid: String) {
        if (tasks.value.any { task -> task.uuid == uuid }) {
            tasks.update { prev -> prev.filter { it.uuid != uuid } }
            deletedTasksUuids += uuid
        }
    }

    override suspend fun addTaskCompletion(taskUuid: String, completionDate: Instant) {
        completedTasks += TaskCompletionInvocation(
            taskUuid = taskUuid,
            completionDate = completionDate
        )
    }

    override fun getTask(uuid: String) =
        tasks.map { it.firstOrNull { task -> task.uuid == uuid } }

    override suspend fun deleteLatestTaskCompletion(taskUuid: String) {
        deletedTaskCompletionsUuids += taskUuid
    }

    data class TaskCompletionInvocation(val taskUuid: String, val completionDate: Instant)
}