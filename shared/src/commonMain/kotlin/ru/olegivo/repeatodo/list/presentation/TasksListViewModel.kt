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

import dev.icerock.moko.mvvm.flow.cStateFlow
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
import ru.olegivo.repeatodo.domain.Priority
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.edit.navigation.EditTaskNavigator
import ru.olegivo.repeatodo.list.domain.TasksListFilters
import ru.olegivo.repeatodo.main.navigation.FakeMainNavigator
import ru.olegivo.repeatodo.randomInstant
import ru.olegivo.repeatodo.utils.PreviewEnvironment
import ru.olegivo.repeatodo.utils.newUuid

class TasksListViewModel(
    getTasks: GetTasksListUseCase,
    private val completeTask: CompleteTaskUseCase,
    private val cancelTaskCompletion: CancelTaskCompletionUseCase,
    private val editTaskNavigator: EditTaskNavigator,
    private val isTaskCompleted: IsTaskCompletedUseCase,
    private val relativeDateFormatter: RelativeDateFormatter,
    private val tasksSorterByCompletion: TasksSorterByCompletion,
    tasksListFilters: TasksListFilters
): BaseViewModel() {

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
        tasksListFilters.isShowCompleted,
        tasksListFilters.isShowOnlyHighestPriority
    ) { tasks, showCompleted, isShowOnlyHighestPriority ->
        TasksListUiState(
            tasks
                .filterByShowCompleted(showCompleted)
                .filterByHighestPriority(isShowOnlyHighestPriority)
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

    private fun List<TaskUi>.filterByShowCompleted(showCompleted: Boolean): List<TaskUi> {
        return if (showCompleted) {
            this
        } else {
            filter { !it.isCompleted }
        }
    }

    private fun List<TaskUi>.filterByHighestPriority(showOnlyHighestPriority: Boolean): List<TaskUi> {
        return if (!showOnlyHighestPriority) {
            this
        } else {
            groupBy { it.priority?.priority }
                .let {
                    it[Priority.HIGH]
                        ?: it[Priority.MEDIUM]
                        ?: it[Priority.LOW]
                        ?: it[null]
                        ?: emptyList()
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
                    toDoListUuid = ToDoList.Predefined.Kind.INBOX.uuid,
                    lastCompletionDate = randomInstant(),
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
        TasksListFilters().also {
            it.isShowCompleted.value = true
        }
    }
    register {
        TasksListViewModel(get(), get(), get(), get(), get(), get(), get(), get())
    }
}
