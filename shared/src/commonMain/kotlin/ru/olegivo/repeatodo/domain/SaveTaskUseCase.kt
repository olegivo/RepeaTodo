package ru.olegivo.repeatodo.domain

import ru.olegivo.repeatodo.domain.models.Task

interface SaveTaskUseCase {

    suspend operator fun invoke(task: Task)
}
