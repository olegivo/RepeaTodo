package ru.olegivo.repeatodo.db

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import ru.olegivo.repeatodo.data.LocalTasksDataSource
import ru.olegivo.repeatodo.domain.models.Task

class LocalTasksDataSourceImpl(private val db: RepeaTodoDb) : LocalTasksDataSource {
    override fun getTasks(): Flow<List<Task>> =
        db.taskQueries
            .getTasks { uuid: String, title: String ->
                Task(uuid = uuid, title = title)
            }
            .asFlow()
            .mapToList()

    override fun add(task: Task) {
        db.taskQueries.addTask(task.doDb())
    }
}
