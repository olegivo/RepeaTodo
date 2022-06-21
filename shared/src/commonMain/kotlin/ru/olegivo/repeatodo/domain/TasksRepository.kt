package ru.olegivo.repeatodo.domain

import kotlinx.coroutines.flow.Flow
import ru.olegivo.repeatodo.domain.models.Task

interface TasksRepository {

    fun getTasks(): Flow<List<Task>>

    fun add(task: Task)
    suspend fun update(task: Task)
    fun getTask(uuid: String): Flow<Task?>
}
