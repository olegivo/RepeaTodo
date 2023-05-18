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
package ru.olegivo.repeatodo.edit.presentation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.olegivo.repeatodo.BaseViewModel
import ru.olegivo.repeatodo.domain.GetTaskUseCase
import ru.olegivo.repeatodo.domain.SaveTaskUseCase
import ru.olegivo.repeatodo.domain.WorkState
import ru.olegivo.repeatodo.domain.filterCompleted
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.main.navigation.MainNavigator

class EditTaskViewModelImpl(
    private val uuid: String,
    private val getTask: GetTaskUseCase,
    private val saveTask: SaveTaskUseCase,
    private val mainNavigator: MainNavigator,
) : BaseViewModel(), EditTaskViewModel {

    private val loadingState: MutableSharedFlow<WorkState<Task>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val savingState: MutableSharedFlow<WorkState<Unit>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val loadedTask: Flow<Task> = loadingState.filterCompleted()

    override val title: MutableStateFlow<String> = MutableStateFlow("")

    private val actualTask: Flow<Task> = combine(
        loadedTask,
        title
    ) { task, title ->
        task.copy(
            title = title
        )
    }

    override val isLoading: StateFlow<Boolean> = loadingState.map { it is WorkState.InProgress }
        .asState(false)

    override val isLoadingError: StateFlow<Boolean> = loadingState.map { it is WorkState.Error }
        .asState(false)

    override val canSave: StateFlow<Boolean> =
        combine(actualTask, loadedTask) { actual, origin ->
            actual.isValid() && actual != origin
        }.asState(false)

    override val isSaving: StateFlow<Boolean> = savingState.map { it is WorkState.InProgress }
        .asState(false)

    override val isSaveError: StateFlow<Boolean> = savingState.map { it is WorkState.Error }
        .asState(false)

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            loadedTask.collect { origin ->
                title.update { origin.title }
            }
        }
        viewModelScope.launch {
            loadingState.collect {
                //
            }
        }
        viewModelScope.launch {
            savingState.collect {
                if (it is WorkState.Completed) {
                    mainNavigator.back()
                }
            }
        }
        viewModelScope.launch {
            loadingState.emitAll(getTask(uuid))
//            loadingState.update { EditTaskState.Loading }
//            originTask.collect { loadingState ->
//                this@EditTaskViewModelImpl.loadingState.update {
//                    when (loadingState) {
//                        is WorkState.Completed -> EditTaskState.Editing(loadingState.result)
//                        is WorkState.Error -> EditTaskState.LoadingError
//                        is WorkState.InProgress -> EditTaskState.Loading
//                    }
//                }
//            }
        }
    }

    override fun onSaveClicked() {
        viewModelScope.launch {
            savingState.emitAll(saveTask(actualTask.first()))
        }
    }

    override fun onCancelClicked() {
        mainNavigator.back()
    }

    private fun Task.isValid() = title.isNotBlank()
}
