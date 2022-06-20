package ru.olegivo.repeatodo.list.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.olegivo.repeatodo.domain.GetTasksListUseCase

class TasksListViewModelImpl(getTasks: GetTasksListUseCase) :
    ViewModel(),
    TasksListViewModel {

    override val state = getTasks()
        .map {
            TasksListUiState(it)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TasksListUiState(emptyList())
        )
}
