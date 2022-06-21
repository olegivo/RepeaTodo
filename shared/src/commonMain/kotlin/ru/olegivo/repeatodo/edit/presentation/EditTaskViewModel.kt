package ru.olegivo.repeatodo.edit.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface EditTaskViewModel {

    val title: MutableStateFlow<String>
    val isLoading: StateFlow<Boolean>
    val canSave: StateFlow<Boolean>
    val isSaving: MutableStateFlow<Boolean>
    val onSaved: SharedFlow<Unit>
    fun onSaveClicked()
    fun onCleared()
}
