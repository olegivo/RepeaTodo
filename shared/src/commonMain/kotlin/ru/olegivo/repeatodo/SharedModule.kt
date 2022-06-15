package ru.olegivo.repeatodo

import org.koin.dsl.module
import ru.olegivo.repeatodo.domain.AddTaskUseCase
import ru.olegivo.repeatodo.domain.AddTaskUseCaseImpl

fun sharedModule() = module {
    factory<AddTaskUseCase> { AddTaskUseCaseImpl(tasksRepository = get()) }
}
