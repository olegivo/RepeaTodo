package ru.olegivo.repeatodo.data

import ru.olegivo.repeatodo.domain.TasksRepository
import ru.olegivo.repeatodo.domain.models.Task

class TasksRepositoryImpl(private val localTasksDataSource: LocalTasksDataSource) :
    TasksRepository {

    override val tasks get() = localTasksDataSource.getTasks()

    override fun add(task: Task) {
        localTasksDataSource.add(task)
    }
}
