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

import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.olegivo.repeatodo.BaseViewModel
import ru.olegivo.repeatodo.domain.GetTasksListUseCase
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.edit.navigation.EditTaskNavigator
import ru.olegivo.repeatodo.main.navigation.FakeMainNavigator
import ru.olegivo.repeatodo.utils.PreviewEnvironment
import ru.olegivo.repeatodo.utils.newUuid

class TasksListViewModel(
    getTasks: GetTasksListUseCase,
    private val editTaskNavigator: EditTaskNavigator
): BaseViewModel() {

    val state = getTasks()
        .map {
            TasksListUiState(it)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TasksListUiState(emptyList())
        ).cStateFlow()

    fun onTaskEditClicked(task: Task) {
        editTaskNavigator.editTask(task.uuid)
    }
}

fun PreviewEnvironment.taskListFakes() {
    register<GetTasksListUseCase> {
        object: GetTasksListUseCase {
            override fun invoke() = flowOf(
                (1..5).map {
                    Task(
                        uuid = newUuid(),
                        title = "Task $it",
                        daysPeriodicity = it
                    )
                }
            )
        }
    }
    register<EditTaskNavigator> { FakeMainNavigator() }
    register { TasksListViewModel(get(), get()) }
}
