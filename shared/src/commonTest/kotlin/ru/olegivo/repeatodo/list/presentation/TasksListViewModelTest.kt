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

import io.kotest.core.spec.IsolationMode
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.domain.GetTasksListUseCase
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.domain.models.randomTask
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.kotest.LifecycleMode
import ru.olegivo.repeatodo.main.navigation.FakeMainNavigator
import ru.olegivo.repeatodo.main.navigation.NavigationDestination
import ru.olegivo.repeatodo.randomList

internal class TasksListViewModelTest: FreeSpec(LifecycleMode.Root) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf

    init {
        "instance" - {
            val getTasksListUseCase = FakeGetTasksListUseCase()
            val editTaskNavigator = FakeMainNavigator()

            val viewModel = TasksListViewModel(getTasksListUseCase, editTaskNavigator)
            val state = viewModel.state.testIn(name = "state")

            "initial state" - {
                state.awaitItem().tasks.shouldBeEmpty()

                "actual state after loading" {
                    val list = randomList { randomTask() }

                    getTasksListUseCase.list.update { list }

                    state.awaitItem().tasks shouldBe list
                }

                "onTaskEditClicked should navigate to edit" {
                    val task = randomTask()

                    viewModel.onTaskEditClicked(task = task)

                    editTaskNavigator.invocations shouldBe FakeMainNavigator.Invocations.To(
                        NavigationDestination.EditTask(task.uuid)
                    )
                }
            }
        }
    }

    class FakeGetTasksListUseCase: GetTasksListUseCase {

        val list = MutableStateFlow<List<Task>>(emptyList())

        override fun invoke(): Flow<List<Task>> = list
    }
}
