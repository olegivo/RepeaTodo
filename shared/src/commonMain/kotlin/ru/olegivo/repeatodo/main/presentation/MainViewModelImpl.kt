package ru.olegivo.repeatodo.main.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import ru.olegivo.repeatodo.main.navigation.MainNavigator

internal class MainViewModelImpl(private val mainNavigator: MainNavigator) : ViewModel(), MainViewModel {
    override val state = MutableStateFlow(MainUiState())

    override fun onAddTaskClicked() {
        mainNavigator.addTask()
    }
}
