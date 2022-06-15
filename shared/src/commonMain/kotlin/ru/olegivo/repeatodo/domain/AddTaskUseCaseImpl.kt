package ru.olegivo.repeatodo.domain

import ru.olegivo.repeatodo.domain.models.Task

internal class AddTaskUseCaseImpl(private val tasksRepository: TasksRepository) : AddTaskUseCase {
    override fun invoke(task: Task) {
        tasksRepository.add(task)
    }
}