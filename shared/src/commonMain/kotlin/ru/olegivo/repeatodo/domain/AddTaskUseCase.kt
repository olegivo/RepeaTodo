package ru.olegivo.repeatodo.domain

import ru.olegivo.repeatodo.domain.models.Task

interface AddTaskUseCase {
    operator fun invoke(task: Task)
}
