package ru.olegivo.repeatodo.data

import ru.olegivo.repeatodo.domain.TasksRepository
import ru.olegivo.repeatodo.domain.models.Task

class TasksRepositoryImpl(private val localTasksDataSource: LocalTasksDataSource) :
    TasksRepository {

    override fun getTasks() = localTasksDataSource.getTasks()

    override fun getTask(uuid: String) = localTasksDataSource.getTask(uuid)

    override fun add(task: Task) {
        localTasksDataSource.add(task)
    }

    override suspend fun update(task: Task) {
        if (!localTasksDataSource.update(task)) {
            localTasksDataSource.add(task)
        }
    }
}
