package ru.olegivo.repeatodo.add.presentation

import app.cash.turbine.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModel
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModelImpl
import ru.olegivo.repeatodo.domain.AddTaskUseCase
import ru.olegivo.repeatodo.domain.models.Task

internal class AddTaskViewModelImplTest : FreeSpec({
    "viewModel" - {
        val viewModel: AddTaskViewModel = AddTaskViewModelImpl(FakeAddTaskUseCase())

        "initial state" {
            viewModel.onAdded.test {
                expectNoEvents()
            }

            viewModel.isLoading.test {
                awaitItem() shouldBe false
            }


        }
    }
}) {

    class FakeAddTaskUseCase : AddTaskUseCase {

        override fun invoke(task: Task) {
            TODO("Not yet implemented")
        }
    }
}
