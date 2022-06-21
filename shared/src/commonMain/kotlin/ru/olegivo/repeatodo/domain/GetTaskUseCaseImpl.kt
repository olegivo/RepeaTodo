package ru.olegivo.repeatodo.domain

class GetTaskUseCaseImpl(private val tasksRepository: TasksRepository) : GetTaskUseCase {

    override fun invoke(uuid: String) = tasksRepository.getTask(uuid)
}
