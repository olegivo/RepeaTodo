package ru.olegivo.repeatodo.main.presentation

import kotlinx.coroutines.flow.MutableStateFlow

class FakeMainViewModel : MainViewModel {

    override val state = MutableStateFlow(MainUiState())

    override fun onAddTaskClicked() {
    }

    override fun onCleared() {
    }
}
