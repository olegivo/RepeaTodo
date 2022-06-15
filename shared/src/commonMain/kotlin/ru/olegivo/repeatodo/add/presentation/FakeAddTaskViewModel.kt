package ru.olegivo.repeatodo.add.presentation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class FakeAddTaskViewModel(initialState: AddTaskUiState) : AddTaskViewModel {

    override val title = MutableStateFlow("")
    override val isLoading = MutableStateFlow(false)
    override val canAdd = MutableStateFlow(false)
    override val onAdded: SharedFlow<Unit> = MutableSharedFlow()

    override fun onAddClicked() {
    }

    override fun onCleared() {
    }
}
