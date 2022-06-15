package ru.olegivo.repeatodo.main.presentation

import kotlinx.coroutines.flow.StateFlow

interface MainViewModel {
    val state: StateFlow<MainUiState>
    fun onAddTaskClicked()
    fun onCleared()
}
