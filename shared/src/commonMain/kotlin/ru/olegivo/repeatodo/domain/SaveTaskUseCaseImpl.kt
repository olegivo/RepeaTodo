package ru.olegivo.repeatodo.domain

import ru.olegivo.repeatodo.domain.models.Task

class SaveTaskUseCaseImpl(private val tasksRepository: TasksRepository) : SaveTaskUseCase {

    override suspend fun invoke(task: Task) {
        tasksRepository.update(task)
    }
}
