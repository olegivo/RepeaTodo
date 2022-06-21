package ru.olegivo.repeatodo.edit.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.olegivo.repeatodo.domain.GetTaskUseCase
import ru.olegivo.repeatodo.domain.SaveTaskUseCase
import ru.olegivo.repeatodo.domain.models.Task

class EditTaskViewModelImpl(
    uuid: String,
    getTask: GetTaskUseCase,
    private val saveTask: SaveTaskUseCase,
) : ViewModel(), EditTaskViewModel {

    private val originTask = getTask(uuid)
    override val title = MutableStateFlow("")

    private val actualTask = combine(
        originTask.mapNotNull { it },
        title
    ) { task, title ->
        task.copy(
            title = title
        )
    }

    override val isLoading = MutableStateFlow(false)
    override val isSaving = MutableStateFlow(false)
    override val canSave =
        combine(actualTask, originTask) { actual, origin ->
            actual.isValid() && actual != origin
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    override val onSaved = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        viewModelScope.launch {
            originTask.firstOrNull()?.let { task ->
                title.update { task.title }
            }
        }
    }

    override fun onSaveClicked() {
        viewModelScope.launch {
            isSaving.update { true }
            saveTask(actualTask.first())
            onSaved.emit(Unit)
            isSaving.update { false }
        }
    }

    private fun Task.isValid() = title.isNotBlank()
}
