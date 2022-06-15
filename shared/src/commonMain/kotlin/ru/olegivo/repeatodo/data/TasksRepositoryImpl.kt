package ru.olegivo.repeatodo.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.domain.TasksRepository
import ru.olegivo.repeatodo.domain.models.Task

class TasksRepositoryImpl : TasksRepository {
    override val tasks = MutableStateFlow<List<Task>>(emptyList())

    override fun add(task: Task) {
        tasks.update { it + task }
    }
}
