package ru.olegivo.repeatodo.domain

import kotlinx.coroutines.flow.Flow
import ru.olegivo.repeatodo.domain.models.Task

interface GetTaskUseCase {

    operator fun invoke(uuid: String): Flow<Task?>
}
