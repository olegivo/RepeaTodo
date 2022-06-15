package ru.olegivo.repeatodo.add.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.olegivo.repeatodo.domain.AddTaskUseCase
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.utils.newUuid

class AddTaskViewModelImpl(private val addTask: AddTaskUseCase) : ViewModel(), AddTaskViewModel {

    override val title = MutableStateFlow("")
    override val isLoading = MutableStateFlow(false)
    override val canAdd = combine(title, isLoading) { title, isLoading ->
        title.isNotBlank() && !isLoading
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    override val onAdded = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun onAddClicked() {
        viewModelScope.launch {
            isLoading.update { true }
            addTask(Task(uuid = newUuid(), title = title.value))
            onAdded.emit(Unit)
            title.update { "" }
            isLoading.update { false }
        }
    }
}
