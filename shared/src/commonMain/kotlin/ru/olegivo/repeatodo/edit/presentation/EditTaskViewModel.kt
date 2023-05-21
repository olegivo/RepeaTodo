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
package ru.olegivo.repeatodo.edit.presentation

import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.olegivo.repeatodo.BaseViewModel
import ru.olegivo.repeatodo.domain.DeleteTaskUseCase
import ru.olegivo.repeatodo.domain.FakeDeleteTaskUseCase
import ru.olegivo.repeatodo.domain.FakeSaveTaskUseCase
import ru.olegivo.repeatodo.domain.GetTaskUseCase
import ru.olegivo.repeatodo.domain.GetToDoListsUseCase
import ru.olegivo.repeatodo.domain.Priority
import ru.olegivo.repeatodo.domain.SaveTaskUseCase
import ru.olegivo.repeatodo.domain.WorkState
import ru.olegivo.repeatodo.domain.filterCompleted
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.main.navigation.FakeMainNavigator
import ru.olegivo.repeatodo.main.navigation.MainNavigator
import ru.olegivo.repeatodo.utils.PreviewEnvironment
import ru.olegivo.repeatodo.utils.newUuid

class EditTaskViewModel(
    private val uuid: String,
    private val getTask: GetTaskUseCase,
    private val getToDoLists: GetToDoListsUseCase,
    private val saveTask: SaveTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val mainNavigator: MainNavigator,
): BaseViewModel() {

    private val loadingTask = MutableSharedFlow<WorkState<Task>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val loadingToDoLists = MutableSharedFlow<WorkState<List<ToDoList>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val loadingState = combine(
        loadingTask,
        loadingToDoLists
    ) { taskState, toDoListsState -> taskState to toDoListsState }

    private val savingState = MutableSharedFlow<WorkState<Unit>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val deletingState = MutableStateFlow<WorkState<Unit>?>(null)

    private val loadedTask = loadingTask.filterCompleted()
    private val loadedToDoLists = loadingToDoLists.filterCompleted()

    val title = MutableStateFlow("").cMutableStateFlow()

    val toDoList = MutableStateFlow<ToDoListItem>(ToDoListItem.UNDEFINED).cMutableStateFlow()

    val daysPeriodicity = MutableStateFlow("").cMutableStateFlow()

    val priority = MutableStateFlow<Priority?>(null).cMutableStateFlow()

    val toDoListItems: CStateFlow<List<ToDoListItem>> =
        loadingToDoLists.filterCompleted()
            .map { items ->
                items.map { it.toItem() }
            }
            .asState(emptyList())
            .cStateFlow()

    private val actualTask = combine(
        loadedTask,
        title,
        daysPeriodicity.filter { it.toIntOrNull() != null },
        priority,
        toDoList.filterNotNull()
    ) { task, title, daysPeriodicity, priority, toDoList ->
        task.copy(
            title = title,
            daysPeriodicity = daysPeriodicity.toInt(),
            priority = priority,
            toDoListUuid = toDoList.uuid
        )
    }

    val isLoading = loadingState.map { (taskState, toDoListsState) ->
        taskState is WorkState.InProgress || toDoListsState is WorkState.InProgress
    }
        .asState(false).cStateFlow()

    val isLoadingError = loadingState.map { (taskState, toDoListsState) ->
        taskState is WorkState.Error || toDoListsState is WorkState.Error
    }
        .asState(false).cStateFlow()

    val canSave =
        combine(actualTask, loadedTask) { actual, origin ->
            actual.isValid() && actual != origin
        }.asState(false).cStateFlow()

    val isSaving = savingState.map { it is WorkState.InProgress }
        .asState(false).cStateFlow()

    val isSaveError = savingState.map { it is WorkState.Error }
        .asState(false).cStateFlow()

    val isDeleting = deletingState.map { it is WorkState.InProgress }
        .asState(false).cStateFlow()

    val canDelete =
        combine(
            loadingTask.map { it is WorkState.Completed },
            deletingState
        ) { isLoaded, deleting ->
            isLoaded && (deleting == null || deleting is WorkState.Error)
        }.asState(false).cStateFlow()

    val isDeleteError = deletingState.map { it is WorkState.Error }
        .asState(false).cStateFlow()

    val priorityItems: List<PriorityItem> =
        Priority.values().sortedByDescending { it.value }.map { priority ->
            PriorityItem(
                priority = priority,
                title = priority.toString().lowercase()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            )
        }

    init {
        viewModelScope.launch {
            launch {
                loadingToDoLists.emitAll(getToDoLists())
            }
            launch {
                loadTask()
            }
        }
    }

    private fun loadTask() {
        viewModelScope.launch {
            loadedTask.collect { origin ->
                loadTaskFields(origin)
            }
        }
        viewModelScope.launch {
            merge(savingState, deletingState).collect {
                if (it is WorkState.Completed) {
                    mainNavigator.back()
                }
            }
        }
        viewModelScope.launch {
            loadingTask.emitAll(getTask(uuid))
        }
    }

    private suspend fun loadTaskFields(origin: Task) {
        title.update { origin.title }
        daysPeriodicity.update { origin.daysPeriodicity.toString() }
        priority.update { origin.priority }
        toDoList.update {
            loadedToDoLists.first().single { it.uuid == origin.toDoListUuid }.toItem()
        }
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            savingState.emitAll(saveTask(actualTask.first()))
        }
    }

    fun onCancelClicked() {
        mainNavigator.back()
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            deletingState.emitAll(deleteTask(actualTask.first()))
        }
    }

    private fun Task.isValid() =
        title.isNotBlank() &&
            daysPeriodicity in (Task.MIN_DAYS_PERIODICITY..Task.MAX_DAYS_PERIODICITY)
}

private val uuid = newUuid()

fun PreviewEnvironment.editTaskViewModelWithFakes(
    loadResult: WorkState<Task> = WorkState.Completed(
        Task(
            uuid = uuid,
            title = "Task 1",
            daysPeriodicity = 1,
            priority = null,
            lastCompletionDate = null,
            toDoListUuid = ToDoList.Predefined.Kind.INBOX.uuid,
        )
    )
) {
    register<GetTaskUseCase> {
        class FakeGetTaskUseCase(
            private val loadResult: WorkState<Task> = WorkState.Completed(
                Task(
                    uuid = uuid,
                    title = "Task 1",
                    daysPeriodicity = 1,
                    priority = null,
                    lastCompletionDate = null,
                    toDoListUuid = ToDoList.Predefined.Kind.INBOX.uuid,
                )
            )
        ): GetTaskUseCase {
            override fun invoke(uuid: String): Flow<WorkState<Task>> = flowOf(this.loadResult)
        }

        FakeGetTaskUseCase(loadResult = loadResult)
    }
    register<SaveTaskUseCase> { FakeSaveTaskUseCase() }
    register<DeleteTaskUseCase> { FakeDeleteTaskUseCase() }
    register<MainNavigator> { FakeMainNavigator() }
    register { EditTaskViewModel(uuid, get(), get(), get(), get(), get()) }
}
