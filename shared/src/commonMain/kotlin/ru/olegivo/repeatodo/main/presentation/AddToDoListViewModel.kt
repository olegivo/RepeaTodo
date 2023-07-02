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

import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
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
import ru.olegivo.repeatodo.domain.SaveCustomToDoListUseCase
import ru.olegivo.repeatodo.domain.WorkState
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.utils.PreviewEnvironment
import ru.olegivo.repeatodo.utils.newUuid

abstract class AddToDoListViewModel: BaseViewModel() {
    abstract val isEditingNew: CStateFlow<Boolean>
    abstract val title: CMutableStateFlow<String>
    abstract val canSaveNew: CStateFlow<Boolean>
    abstract val isSaving: CStateFlow<Boolean>
    abstract fun beginAddingNew()
    abstract fun cancelAddNew()
    abstract fun onSaveClicked()
}


class AddToDoListViewModelImpl(
    private val saveCustomToDoList: SaveCustomToDoListUseCase
): AddToDoListViewModel() {
    private val _isEditingNew = MutableStateFlow(false)
    private val savingState = MutableSharedFlow<WorkState<Unit>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val isEditingNew = _isEditingNew.cStateFlow()
    override val title = MutableStateFlow("").cMutableStateFlow()
    override val canSaveNew = title.map { it.isNotBlank() }
        .asState(false).cStateFlow()
    override val isSaving = savingState.map { it is WorkState.InProgress }
        .asState(false).cStateFlow()

    init {
        viewModelScope.launch {
            merge(savingState).collect {
                if (it is WorkState.Completed) {
                    _isEditingNew.update { false }
                }
            }
        }
    }

    override fun beginAddingNew() {
        _isEditingNew.update { true }
    }

    override fun cancelAddNew() {
        _isEditingNew.update { false }
        title.update { "" }
    }

    override fun onSaveClicked() {
        viewModelScope.launch {
            savingState.emitAll(
                saveCustomToDoList(
                    ToDoList.Custom(
                        uuid = newUuid(),
                        title = title.first()
                    )
                )
            )
        }
    }
}

fun PreviewEnvironment.addToDoListViewModelFakes(isEditingNew: Boolean = false) {
    register<AddToDoListViewModel> {
        object: AddToDoListViewModel() {
            override val isEditingNew: CStateFlow<Boolean> =
                MutableStateFlow(isEditingNew).cStateFlow()
            override val title: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
            override val canSaveNew: CStateFlow<Boolean> = MutableStateFlow(false).cStateFlow()
            override val isSaving: CStateFlow<Boolean> = MutableStateFlow(false).cStateFlow()

            override fun beginAddingNew() {
            }

            override fun cancelAddNew() {
            }

            override fun onSaveClicked() {
            }
        }
    }
}
