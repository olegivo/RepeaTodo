package ru.olegivo.repeatodo.domain

import kotlinx.coroutines.flow.Flow
import ru.olegivo.repeatodo.domain.models.Task

interface TasksRepository {
    val tasks: Flow<List<Task>>

    fun add(task: Task)
}
