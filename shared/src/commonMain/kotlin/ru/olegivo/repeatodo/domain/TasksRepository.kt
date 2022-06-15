package ru.olegivo.repeatodo.domain

import kotlinx.coroutines.flow.StateFlow
import ru.olegivo.repeatodo.domain.models.Task

interface TasksRepository {
    val tasks: StateFlow<List<Task>>

    fun add(task: Task)
}
