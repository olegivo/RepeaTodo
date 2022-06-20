package ru.olegivo.repeatodo.list.presentation

import kotlinx.coroutines.flow.StateFlow

interface TasksListViewModel {
    val state: StateFlow<TasksListUiState>
    fun onCleared()
}
