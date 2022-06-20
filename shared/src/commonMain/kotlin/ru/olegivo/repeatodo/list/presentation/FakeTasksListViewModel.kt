package ru.olegivo.repeatodo.list.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.utils.newUuid

class FakeTasksListViewModel : TasksListViewModel {

    override val state = MutableStateFlow(
        TasksListUiState(
            listOf(
                Task(
                    uuid = newUuid(),
                    title = "Task 1"
                ),
                Task(
                    uuid = newUuid(),
                    title = "Task 2"
                ),
            )
        )
    )

    override fun onCleared() {
    }
}
