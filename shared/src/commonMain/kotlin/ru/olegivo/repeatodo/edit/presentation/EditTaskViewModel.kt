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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface EditTaskViewModel {

    val title: MutableStateFlow<String>
    val daysPeriodicity: MutableStateFlow<String>

    val isLoading: Flow<Boolean>
    val isLoadingError: StateFlow<Boolean>

    val canSave: StateFlow<Boolean>
    val isSaving: StateFlow<Boolean>
    val isSaveError: StateFlow<Boolean>

    val canDelete: StateFlow<Boolean>
    val isDeleting: StateFlow<Boolean>
    val isDeleteError: StateFlow<Boolean>

    fun onSaveClicked()
    fun onCancelClicked()
    fun onDeleteClicked()
    fun onCleared()
}
