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

package ru.olegivo.repeatodo.main.presentation

import dev.icerock.moko.mvvm.flow.CFlow
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.olegivo.repeatodo.BaseViewModel
import ru.olegivo.repeatodo.domain.DeleteCustomToDoListUseCase
import ru.olegivo.repeatodo.domain.SaveCustomToDoListUseCase
import ru.olegivo.repeatodo.domain.WorkState
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.utils.PreviewEnvironment

abstract class DrawerToDoListsCustomItemViewModel: BaseViewModel() {
    abstract val title: CMutableStateFlow<String>
    abstract val isEditing: CStateFlow<Boolean>
    abstract val canSave: CStateFlow<Boolean>
    abstract val showDeleteConfirmation: CStateFlow<Boolean>
    abstract val isSaving: CFlow<Boolean>
    abstract val isDeleting: CFlow<Boolean>
    abstract val isDeleteError: CStateFlow<Boolean>
    abstract val canDelete: CStateFlow<Boolean>
    abstract fun onBeginEditClicked()
    abstract fun onCancelEditClicked()
    abstract fun onSaveClicked()
    abstract fun onDeleteClicked()
    abstract fun onDeleteConfirmed()
    abstract fun onDeleteDismissed()
}

class DrawerToDoListsCustomItemViewModelImpl(
    private val toDoList: ToDoList.Custom,
    private val saveCustomToDoList: SaveCustomToDoListUseCase,
    private val deleteCustomToDoList: DeleteCustomToDoListUseCase
): DrawerToDoListsCustomItemViewModel() {
    override val title = MutableStateFlow(toDoList.title).cMutableStateFlow()
    private val _isEditing = MutableStateFlow(false)
    private val savingState = MutableSharedFlow<WorkState<Unit>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val _isDeleteConfirmationShown = MutableStateFlow(false)
    private val deletingState = MutableStateFlow<WorkState<Unit>?>(null)

    override val isEditing = _isEditing.cStateFlow()
    override val canSave = title.map { it.isNotBlank() && it != toDoList.title }
        .asState(false).cStateFlow()
    override val showDeleteConfirmation = _isDeleteConfirmationShown.cStateFlow()
    override val isSaving = savingState.map { it is WorkState.InProgress }
        .asState(false).cFlow()
    override val isDeleting = deletingState.map { it is WorkState.InProgress }
        .asState(false).cFlow()
    override val isDeleteError = deletingState.map { it is WorkState.Error }
        .asState(false).cStateFlow()
    override val canDelete =
        deletingState.map { deleting ->
            deleting == null || deleting is WorkState.Error
        }.asState(false).cStateFlow()

    init {
        viewModelScope.launch {
            merge(savingState, deletingState).collect {
                if (it is WorkState.Completed) {
                    _isEditing.update { false }
                }
            }
        }
    }

    override fun onBeginEditClicked() {
        _isEditing.update { true }
    }

    override fun onCancelEditClicked() {
        _isEditing.update { false }
        title.update { toDoList.title }
    }

    override fun onSaveClicked() {
        viewModelScope.launch {
            savingState.emitAll(saveCustomToDoList(toDoList.copy(title = title.first())))
        }
    }

    override fun onDeleteClicked() {
        _isDeleteConfirmationShown.update { true }
    }

    override fun onDeleteConfirmed() {
        _isDeleteConfirmationShown.update { false }
        viewModelScope.launch {
            deletingState.emitAll(deleteCustomToDoList(toDoList = toDoList))
        }
    }

    override fun onDeleteDismissed() {
        _isDeleteConfirmationShown.update { false }
    }
}

fun PreviewEnvironment.drawerToDoListsCustomItemViewModelFakes(
    isEditing: Boolean = false,
    isDeleting: Boolean = false,
    showDeleteConfirmation: Boolean = false,
) {
    register<DrawerToDoListsCustomItemViewModel> {
        FakeDrawerToDoListsCustomItemViewModel(
            toDoList = it as ToDoList.Custom,
            isEditing = isEditing,
            isDeleting = isDeleting,
            showDeleteConfirmation = showDeleteConfirmation,
        )
    }
}

class FakeDrawerToDoListsCustomItemViewModel(
    toDoList: ToDoList.Custom,
    isEditing: Boolean = false,
    isDeleting: Boolean = false,
    showDeleteConfirmation: Boolean = false,
): DrawerToDoListsCustomItemViewModel() {
    override val title: CMutableStateFlow<String> =
        MutableStateFlow(toDoList.title).cMutableStateFlow()
    override val isEditing: CStateFlow<Boolean> =
        MutableStateFlow(isEditing).cStateFlow()
    override val canSave: CStateFlow<Boolean> =
        MutableStateFlow(false).cStateFlow()
    override val showDeleteConfirmation: CStateFlow<Boolean> =
        MutableStateFlow(showDeleteConfirmation).cStateFlow()
    override val isSaving: CFlow<Boolean> =
        MutableStateFlow(false).cFlow()
    override val isDeleting: CFlow<Boolean> =
        MutableStateFlow(isDeleting).cFlow()
    override val isDeleteError: CStateFlow<Boolean> =
        MutableStateFlow(false).cStateFlow()
    override val canDelete: CStateFlow<Boolean> =
        MutableStateFlow(false).cStateFlow()

    override fun onBeginEditClicked() {
    }

    override fun onCancelEditClicked() {
    }

    override fun onSaveClicked() {
    }

    override fun onDeleteClicked() {
    }

    override fun onDeleteConfirmed() {
    }

    override fun onDeleteDismissed() {
    }
}
