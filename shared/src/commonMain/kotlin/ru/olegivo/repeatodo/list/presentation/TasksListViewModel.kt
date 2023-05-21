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

import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.olegivo.repeatodo.BaseViewModel
import ru.olegivo.repeatodo.domain.CancelTaskCompletionUseCase
import ru.olegivo.repeatodo.domain.CompleteTaskUseCase
import ru.olegivo.repeatodo.domain.DateTimeProvider
import ru.olegivo.repeatodo.domain.FakeCancelTaskCompletionUseCase
import ru.olegivo.repeatodo.domain.FakeCompleteTaskUseCase
import ru.olegivo.repeatodo.domain.FakeDateTimeProvider
import ru.olegivo.repeatodo.domain.FakeGetTasksListUseCase
import ru.olegivo.repeatodo.domain.GetTasksListUseCase
import ru.olegivo.repeatodo.domain.IsTaskCompletedUseCase
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.edit.navigation.EditTaskNavigator
import ru.olegivo.repeatodo.main.navigation.FakeMainNavigator
import ru.olegivo.repeatodo.utils.PreviewEnvironment
import ru.olegivo.repeatodo.utils.newUuid

class TasksListViewModel(
    getTasks: GetTasksListUseCase,
    private val completeTask: CompleteTaskUseCase,
    private val cancelTaskCompletion: CancelTaskCompletionUseCase,
    private val editTaskNavigator: EditTaskNavigator,
    private val isTaskCompleted: IsTaskCompletedUseCase,
    private val relativeDateFormatter: RelativeDateFormatter,
    private val tasksSorterByCompletion: TasksSorterByCompletion
): BaseViewModel() {

    val isShowCompleted = MutableStateFlow(false).cMutableStateFlow()
    val state = combine(
        getTasks().map { tasks ->
            tasksSorterByCompletion.sort(tasks)
                .map {
                    it.toUi(
                        isTaskCompleted = isTaskCompleted,
                        relativeDateFormatter = relativeDateFormatter
                    )
                }
        },
        isShowCompleted
    ) { tasks, showCompleted ->
        TasksListUiState(
            if (showCompleted) {
                tasks
            } else {
                tasks.filter { !it.isCompleted }
            }
        )
    }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TasksListUiState(emptyList())
        )
        .cStateFlow()

    fun onTaskEditClicked(task: TaskUi) {
        editTaskNavigator.editTask(task.uuid)
    }

    fun onTaskCompletionClicked(task: TaskUi) {
        viewModelScope.launch {
            if (task.isCompleted) {
                cancelTaskCompletion(task.uuid)
            } else {
                completeTask(task.uuid)
            }
        }
    }
}

fun PreviewEnvironment.taskListFakes() {
    register<GetTasksListUseCase> {
        FakeGetTasksListUseCase().also { instance ->
            instance.list.value = (1..5).map {
                Task(
                    uuid = newUuid(),
                    title = "Task $it",
                    daysPeriodicity = it,
                    priority = null,
                    lastCompletionDate = null,
                )
            }
        }
    }
    register<CompleteTaskUseCase> { FakeCompleteTaskUseCase() }
    register<CancelTaskCompletionUseCase> { FakeCancelTaskCompletionUseCase() }
    register<IsTaskCompletedUseCase> { FakeIsTaskCompletedUseCase() }
    register<RelativeDateFormatter> { FakeRelativeDateFormatter() }
    register<EditTaskNavigator> { FakeMainNavigator() }
    register<DateTimeProvider> { FakeDateTimeProvider() }
    register { TasksSorterByCompletion(get(), get()) }
    register {
        TasksListViewModel(get(), get(), get(), get(), get(), get(), get()).also {
            it.isShowCompleted.value = true
        }
    }
}
