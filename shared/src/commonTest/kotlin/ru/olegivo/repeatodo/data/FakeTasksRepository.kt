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

    override fun add(task: Task) {
        tasks.update { it + task }
        lastAddedTask = task
    }

    override suspend fun update(task: Task) {
        getTasks().update { prev -> prev.filter { it.uuid != task.uuid } + task }
    }

    override fun getTask(uuid: String) =
        tasks.map { it.firstOrNull { task -> task.uuid == uuid } }
}
