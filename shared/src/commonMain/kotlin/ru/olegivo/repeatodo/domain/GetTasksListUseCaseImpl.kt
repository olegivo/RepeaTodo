package ru.olegivo.repeatodo.domain

import kotlinx.coroutines.flow.Flow
import ru.olegivo.repeatodo.domain.models.Task

class GetTasksListUseCaseImpl(private val tasksRepository: TasksRepository) : GetTasksListUseCase {
    override fun invoke(): Flow<List<Task>> = tasksRepository.tasks
}
