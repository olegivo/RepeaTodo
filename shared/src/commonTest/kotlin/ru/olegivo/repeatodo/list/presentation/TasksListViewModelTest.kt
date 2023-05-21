/*
 * Copyright (C) 2023 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of RepeaTodo.
 *
 * RepeaTodo is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * RepeaTodo PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * RepeaTodo.
 */

package ru.olegivo.repeatodo.list.presentation

import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeSameSizeAs
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.domain.FakeCancelTaskCompletionUseCase
import ru.olegivo.repeatodo.domain.FakeCompleteTaskUseCase
import ru.olegivo.repeatodo.domain.FakeDateTimeProvider
import ru.olegivo.repeatodo.domain.FakeGetTasksListUseCase
import ru.olegivo.repeatodo.domain.Priority
import ru.olegivo.repeatodo.domain.models.randomTask
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.list.domain.TasksListFilters
import ru.olegivo.repeatodo.main.navigation.FakeMainNavigator
import ru.olegivo.repeatodo.main.navigation.NavigationDestination
import ru.olegivo.repeatodo.randomInstant
import ru.olegivo.repeatodo.randomList
import ru.olegivo.repeatodo.randomNull
import kotlin.time.Duration.Companion.minutes

internal class TasksListViewModelTest: FreeSpec() {

    init {
        "instance" - {
            val getTasksListUseCase = FakeGetTasksListUseCase()
            val completeTaskUseCase = FakeCompleteTaskUseCase()
            val cancelTaskCompletionUseCase = FakeCancelTaskCompletionUseCase()
            val isTaskCompletedUseCase = FakeIsTaskCompletedUseCase()
            val relativeDateFormatter = FakeRelativeDateFormatter()
            val editTaskNavigator = FakeMainNavigator()

            val tasksListFilters = TasksListFilters()
            val viewModel = TasksListViewModel(
                getTasks = getTasksListUseCase,
                completeTask = completeTaskUseCase,
                cancelTaskCompletion = cancelTaskCompletionUseCase,
                editTaskNavigator = editTaskNavigator,
                isTaskCompleted = isTaskCompletedUseCase,
                relativeDateFormatter = relativeDateFormatter,
                tasksSorterByCompletion = TasksSorterByCompletion(
                    dateTimeProvider = FakeDateTimeProvider(),
                    isTaskCompleted = isTaskCompletedUseCase
                ),
                tasksListFilters = tasksListFilters,
            )
            val state = viewModel.state.testIn(name = "state")
            val isShowCompleted =
                tasksListFilters.isShowCompleted.testIn(name = "isShowCompletedTasks")
            val isShowOnlyHighestPriority =
                tasksListFilters.isShowOnlyHighestPriority.testIn(name = "isShowCompletedTasks")

            "initial state" - {
                state.awaitItem().tasks.shouldBeEmpty()
                isShowCompleted.awaitItem().shouldBeFalse()
                isShowOnlyHighestPriority.awaitItem().shouldBeTrue()

                val taskUi = randomTask().toUi(
                    isTaskCompleted = isTaskCompletedUseCase,
                    relativeDateFormatter = relativeDateFormatter
                )

                "actual state after loading" - {
                    val threshold = randomInstant()
                    isTaskCompletedUseCase.considerAsCompletedAfter = threshold
                    val lastCompletionDate = threshold + 1.minutes

                    val completedHighestPriority =
                        randomList {
                            randomTask(
                                lastCompletionDate = lastCompletionDate,
                                priority = Priority.HIGH
                            )
                        }
                    val completedLowerPriority =
                        randomList {
                            randomTask(
                                lastCompletionDate = lastCompletionDate,
                                priority = Priority.values()
                                    .filter { it != Priority.HIGH }
                                    .random()
                                    .randomNull()
                            )
                        }
                    val completed =
                        completedHighestPriority + completedLowerPriority
                    val notCompletedHighestPriority = randomList {
                        randomTask(
                            lastCompletionDate = null,
                            priority = Priority.HIGH
                        )
                    }
                    val notCompletedLowerPriority = randomList {
                        randomTask(
                            lastCompletionDate = null,
                            priority = Priority.values()
                                .filter { it != Priority.HIGH }
                                .random()
                                .randomNull()
                        )
                    }
                    val notCompleted =
                        notCompletedHighestPriority + notCompletedLowerPriority
                    val list = completed + notCompleted

                    getTasksListUseCase.list.update { list }

                    "state should contain only not completed tasks with highest priority" - {
                        state.awaitItem().tasks
                            .shouldForAll { it.isCompleted.shouldBeFalse() }
                            .shouldBeSameSizeAs(notCompletedHighestPriority)
                            .shouldContainExactlyInAnyOrder(
                                notCompletedHighestPriority.map {
                                    it.toUi(
                                        isTaskCompleted = isTaskCompletedUseCase,
                                        relativeDateFormatter = relativeDateFormatter
                                    )
                                }
                            )

                        "state should contain not completed high priority WHEN isShowOnlyHighestPriority = false" {
                            tasksListFilters.isShowOnlyHighestPriority.update { false }

                            state.awaitItem().tasks
                                .shouldForAll { it.isCompleted.shouldBeFalse() }
                                .shouldBeSameSizeAs(notCompleted)
                                .shouldContainExactlyInAnyOrder(
                                    notCompleted.map {
                                        it.toUi(
                                            isTaskCompleted = isTaskCompletedUseCase,
                                            relativeDateFormatter = relativeDateFormatter
                                        )
                                    }
                                )
                        }

                        "state should contain completed high priority and not completed high priority WHEN isShowCompleted = true" - {
                            tasksListFilters.isShowCompleted.update { true }

                            val tasks = state.expectMostRecentItem().tasks
                            val (completedResult, notCompletedResult) = tasks.partition { it.isCompleted }
                            tasks shouldContainExactlyInAnyOrder (completedHighestPriority + notCompletedHighestPriority).map {
                                it.toUi(
                                    isTaskCompleted = isTaskCompletedUseCase,
                                    relativeDateFormatter = relativeDateFormatter
                                )
                            }

                            notCompletedResult.shouldBeSameSizeAs(notCompletedHighestPriority)
                            completedResult.shouldBeSameSizeAs(completedHighestPriority)

                            "state should contain completed high priority and not completed high priority WHEN isShowOnlyHighestPriority = false" {
                                tasksListFilters.isShowOnlyHighestPriority.update { false }

                                val tasks = state.expectMostRecentItem().tasks
                                val (completedResult, notCompletedResult) = tasks.partition { it.isCompleted }
                                tasks shouldContainExactlyInAnyOrder list.map {
                                    it.toUi(
                                        isTaskCompleted = isTaskCompletedUseCase,
                                        relativeDateFormatter = relativeDateFormatter
                                    )
                                }

                                notCompletedResult.shouldBeSameSizeAs(notCompleted)
                                completedResult.shouldBeSameSizeAs(completed)
                            }
                        }
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
