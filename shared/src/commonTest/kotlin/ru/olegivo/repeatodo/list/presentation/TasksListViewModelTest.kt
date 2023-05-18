/*
 * Copyright (C) 2022 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of RepeaTodo.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * RepeaTodo.
 */

package ru.olegivo.repeatodo.list.presentation

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.domain.FakeCancelTaskCompletionUseCase
import ru.olegivo.repeatodo.domain.FakeCompleteTaskUseCase
import ru.olegivo.repeatodo.domain.FakeDateTimeProvider
import ru.olegivo.repeatodo.domain.FakeGetTasksListUseCase
import ru.olegivo.repeatodo.domain.models.randomTask
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.main.navigation.FakeMainNavigator
import ru.olegivo.repeatodo.main.navigation.NavigationDestination
import ru.olegivo.repeatodo.randomBoolean
import ru.olegivo.repeatodo.randomList

internal class TasksListViewModelTest: FreeSpec() {

    init {
        "instance" - {
            val getTasksListUseCase = FakeGetTasksListUseCase()
            val completeTaskUseCase = FakeCompleteTaskUseCase()
            val cancelTaskCompletionUseCase = FakeCancelTaskCompletionUseCase()
            val isTaskCompletedUseCase = FakeIsTaskCompletedUseCase(isCompleted = randomBoolean())
            val relativeDateFormatter = FakeRelativeDateFormatter()
            val editTaskNavigator = FakeMainNavigator()

            val viewModel = TasksListViewModel(
                getTasks = getTasksListUseCase,
                completeTask = completeTaskUseCase,
                cancelTaskCompletion = cancelTaskCompletionUseCase,
                editTaskNavigator = editTaskNavigator,
                isTaskCompleted = isTaskCompletedUseCase,
                relativeDateFormatter = relativeDateFormatter,
                tasksSorterByCompletion = TasksSorterByCompletion(FakeDateTimeProvider()),
            )
            val state = viewModel.state.testIn(name = "state")

            "initial state" - {
                state.awaitItem().tasks.shouldBeEmpty()

                val taskUi = randomTask().toUi(
                    isTaskCompleted = isTaskCompletedUseCase,
                    relativeDateFormatter = relativeDateFormatter
                )

                "actual state after loading" {
                    val list = randomList { randomTask() }

                    getTasksListUseCase.list.update { list }

                    state.awaitItem().tasks shouldBe list.map {
                        it.toUi(
                            isTaskCompleted = isTaskCompletedUseCase,
                            relativeDateFormatter = relativeDateFormatter
                        )
                    }
                }

                "onTaskEditClicked should navigate to edit" {

                    viewModel.onTaskEditClicked(task = taskUi)

                    editTaskNavigator.invocations shouldBe FakeMainNavigator.Invocations.To(
                        NavigationDestination.EditTask(taskUi.uuid)
                    )
                }

                "onTaskCompletionClicked should complete the task WHEN task is not completed" {
                    viewModel.onTaskCompletionClicked(task = taskUi.copy(isCompleted = false))

                    completeTaskUseCase.invocations.shouldContainExactly(taskUi.uuid)
                }

                "onTaskCompletionClicked should cancel completion WHEN task is completed" {
                    viewModel.onTaskCompletionClicked(task = taskUi.copy(isCompleted = true))

                    cancelTaskCompletionUseCase.invocations.shouldContainExactly(taskUi.uuid)
                }
            }
        }
    }
}
