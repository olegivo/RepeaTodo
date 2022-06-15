package ru.olegivo.repeatodo.data

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.domain.TasksRepository
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.randomString

internal class TasksRepositoryImplTest : FreeSpec({
    "TasksRepositoryImpl created" - {
        val tasksRepository: TasksRepository = TasksRepositoryImpl()
        val task = Task(title = randomString())

        tasksRepository.tasks.shouldBeEmpty()

        "add" {
            tasksRepository.add(task)

            tasksRepository.tasks shouldBe listOf(task)
        }
    }
})