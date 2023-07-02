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

package ru.olegivo.repeatodo.add.presentation

import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.olegivo.repeatodo.BaseViewModel
import ru.olegivo.repeatodo.domain.FakeSaveTaskUseCase
import ru.olegivo.repeatodo.domain.SaveTaskUseCase
import ru.olegivo.repeatodo.domain.WorkState
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.utils.PreviewEnvironment
import ru.olegivo.repeatodo.utils.newUuid

class AddTaskViewModel(private val saveTask: SaveTaskUseCase): BaseViewModel() {

    private val savingState: MutableSharedFlow<WorkState<Unit>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val title = MutableStateFlow("").cMutableStateFlow()

    val isAdding = savingState.map { it is WorkState.InProgress }
        .asState(false).cStateFlow()

    val canAdd = combine(title, isAdding) { title, isLoading ->
        title.isNotBlank() && !isLoading
    }.stateIn(viewModelScope, SharingStarted.Lazily, false).cStateFlow()

    fun onAddClicked() {
        viewModelScope.launch {
            val task = Task(
                uuid = newUuid(),
                title = title.value,
                daysPeriodicity = Task.DEFAULT_DAYS_PERIODICITY,
                priority = null,
                lastCompletionDate = null,
                toDoListUuid = ToDoList.Predefined.Kind.INBOX.uuid
            )
            savingState.emitAll(saveTask(task))
        }
        viewModelScope.launch {
            savingState.filter { it is WorkState.Completed }.collect {
                title.update { "" }
            }
        }
    }
}

fun PreviewEnvironment.addTaskViewModelWithFakes() {
    register<SaveTaskUseCase> { FakeSaveTaskUseCase() }
    register { AddTaskViewModel(get()) }
}
