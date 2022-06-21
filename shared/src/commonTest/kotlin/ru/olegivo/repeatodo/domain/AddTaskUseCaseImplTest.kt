package ru.olegivo.repeatodo.domain

import io.kotest.core.spec.style.FreeSpec
import ru.olegivo.repeatodo.data.FakeTasksRepository
import ru.olegivo.repeatodo.domain.models.createTask
import kotlin.test.assertEquals

class AddTaskUseCaseImplTest : FreeSpec({
    "AddTaskUseCaseImpl created" - {
        val tasksRepository = FakeTasksRepository()
        val useCase: AddTaskUseCase = AddTaskUseCaseImpl(tasksRepository = tasksRepository)

        "invoke should add task to repository" {
            val task = createTask()

            useCase.invoke(task)

            assertEquals(task, tasksRepository.lastAddedTask)
        }
    }
})
