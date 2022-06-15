package ru.olegivo.repeatodo.add.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface AddTaskViewModel {

    val title: MutableStateFlow<String>
    val isLoading: StateFlow<Boolean>
    val canAdd: StateFlow<Boolean>
    val onAdded: SharedFlow<Unit>
    fun onAddClicked()
    fun onCleared()
}
