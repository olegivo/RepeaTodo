package ru.olegivo.repeatodo.domain

import io.kotest.core.spec.style.FreeSpec
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.randomString
import kotlin.test.assertEquals

class AddTaskUseCaseImplTest : FreeSpec({
    "AddTaskUseCaseImpl created" - {
        val tasksRepository = FakeTaskRepository()
        val useCase: AddTaskUseCase = AddTaskUseCaseImpl(tasksRepository = tasksRepository)

        "invoke should add task to repository" {
            val task = Task(, title = randomString())

            useCase.invoke(task)

            assertEquals(task, tasksRepository.lastAddedTask)
        }
    }
}) {
    class FakeTaskRepository : TasksRepository {
        var lastAddedTask: Task? = null
            private set

        override val tasks: List<Task>
            get() = TODO("Not yet implemented")

        override fun add(task: Task) {
            lastAddedTask = task
        }
    }
}
