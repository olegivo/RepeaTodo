package ru.olegivo.repeatodo.domain

import kotlinx.coroutines.flow.Flow
import ru.olegivo.repeatodo.domain.models.Task

interface GetTasksListUseCase {
    operator fun invoke(): Flow<List<Task>>
}
