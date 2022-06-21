package ru.olegivo.repeatodo.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.data.FakeTasksRepository
import ru.olegivo.repeatodo.domain.models.createTask

internal class SaveTaskUseCaseImplTest : FreeSpec() {
    init {
        "should update exist task in repository" {
            val tasksRepository = FakeTasksRepository()
            val saveTaskUseCase: SaveTaskUseCase = SaveTaskUseCaseImpl(
                tasksRepository = tasksRepository
            )
            val origin = createTask()
            tasksRepository.add(origin)
            val newVersion = createTask().copy(uuid = origin.uuid)

            saveTaskUseCase(newVersion)

            tasksRepository.getTasks().assertItem {
                shouldNotContain(origin)
                shouldContain(newVersion)
            }
        }
    }
}
