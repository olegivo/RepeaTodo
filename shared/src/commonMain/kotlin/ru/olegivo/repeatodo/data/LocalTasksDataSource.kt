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

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import ru.olegivo.repeatodo.domain.models.Task

interface LocalTasksDataSource {
    fun getTasks(): Flow<List<Task>>
    fun getTask(uuid: String): Flow<Task?>
    suspend fun save(task: Task)
    suspend fun delete(uuid: String)
    suspend fun addTaskCompletion(taskUuid: String, completionDate: LocalDateTime)
    suspend fun deleteLatestTaskCompletion(taskUuid: String)
}
