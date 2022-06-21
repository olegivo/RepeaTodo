package ru.olegivo.repeatodo.data

import kotlinx.coroutines.flow.Flow
import ru.olegivo.repeatodo.domain.models.Task

interface LocalTasksDataSource {
    fun getTasks(): Flow<List<Task>>
    fun add(task: Task)
}
