package ru.olegivo.repeatodo.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.olegivo.repeatodo.data.LocalTasksDataSource
import ru.olegivo.repeatodo.data.TasksRepositoryImpl
import ru.olegivo.repeatodo.db.LocalTasksDataSourceImpl
import ru.olegivo.repeatodo.db.createDatabase
import ru.olegivo.repeatodo.domain.*
import ru.olegivo.repeatodo.main.navigation.MainNavigator
import ru.olegivo.repeatodo.main.navigation.MainNavigatorImpl

object DependencyInjection {
    fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
        startKoin {
            appDeclaration()
            modules(commonModule(), platformModule())
        }
    }

    fun initKoinAndReturnInstance(appDeclaration: KoinAppDeclaration = {}): org.koin.core.Koin =
        startKoin {
            appDeclaration()
            modules(commonModule(), platformModule())
        }.koin

    private fun commonModule() = module {
        single { MainNavigatorImpl() }.bind<MainNavigator>()
        single<TasksRepository> { TasksRepositoryImpl(get()) }
        single<LocalTasksDataSource> { LocalTasksDataSourceImpl(get()) }
        single { createDatabase(get()) }
        factory<AddTaskUseCase> { AddTaskUseCaseImpl(get()) }
        factory<GetTasksListUseCase> { GetTasksListUseCaseImpl(get()) }
    }
}

expect fun platformModule(): Module
