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
 */package ru.olegivo.repeatodo.edit.presentation

import kotlinx.coroutines.flow.MutableStateFlow

class FakeEditTaskViewModel : EditTaskViewModel {

    override val title = MutableStateFlow("Task 1")
    override val isLoading = MutableStateFlow(false)
    override val isLoadingError = MutableStateFlow(false)
    override val canSave = MutableStateFlow(false)
    override val isSaving = MutableStateFlow(false)
    override val isSaveError = MutableStateFlow(false)
    override val canDelete = MutableStateFlow(false)
    override val isDeleting = MutableStateFlow(false)
    override val daysPeriodicity = MutableStateFlow("")
    override val isDeleteError = MutableStateFlow(false)

    override fun onSaveClicked() {
        TODO("Not yet implemented")
    }

    override fun onDeleteClicked() {
        TODO("Not yet implemented")
    }

    override fun onCancelClicked() {
        TODO("Not yet implemented")
    }

    override fun onCleared() {
        TODO("Not yet implemented")
    }
}
