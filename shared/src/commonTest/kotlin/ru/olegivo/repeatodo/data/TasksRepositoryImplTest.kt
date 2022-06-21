package ru.olegivo.repeatodo.data

import app.cash.turbine.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.olegivo.repeatodo.domain.TasksRepository
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.randomString

internal class TasksRepositoryImplTest : FreeSpec({
    "TasksRepositoryImpl created" - {
        val task = Task(uuid = randomString(), title = randomString())
        val tasks = mutableListOf<Task>()
        val localTasksDataSource = FakeLocalTasksDataSource(tasks)

        val tasksRepository: TasksRepository = TasksRepositoryImpl(localTasksDataSource)

        tasksRepository.tasks.test {
            awaitItem().shouldBeEmpty()
            awaitComplete()
        }

        "add" {
            tasksRepository.add(task)

            tasksRepository.tasks.test {
                awaitItem() shouldBe listOf(task)
                awaitComplete()
            }
        }
    }
}) {

    class FakeLocalTasksDataSource(var tasks: MutableList<Task>) : LocalTasksDataSource {

        override fun getTasks(): Flow<List<Task>> {
            return flowOf(tasks)
        }

        override fun add(task: Task) {
            tasks.add(task)
        }
    }
}