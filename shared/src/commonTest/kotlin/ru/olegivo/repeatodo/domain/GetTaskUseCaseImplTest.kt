package ru.olegivo.repeatodo.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.data.FakeTasksRepository
import ru.olegivo.repeatodo.domain.models.createTask
import ru.olegivo.repeatodo.randomString

internal class GetTaskUseCaseImplTest : FreeSpec() {
    init {
        "GetTaskUseCaseImplTest created" - {
            val task = createTask()
            val tasksRepository = FakeTasksRepository()
            val useCase: GetTaskUseCase = GetTaskUseCaseImpl(tasksRepository = tasksRepository)

            "null should be returned WHEN has no task with specified uuid" {
                useCase.invoke(uuid = randomString())
                    .assertItem { shouldBeNull() }
            }

            "!the task should be returned WHEN has task with specified uuid" - {
                tasksRepository.add(task)

                useCase.invoke(uuid = randomString())
                    .assertItem { shouldBe(task) }
            }
        }
    }
}
