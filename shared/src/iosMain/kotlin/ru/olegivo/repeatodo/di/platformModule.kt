package ru.olegivo.repeatodo.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModel
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModelImpl
import ru.olegivo.repeatodo.db.DriverFactory
import ru.olegivo.repeatodo.edit.presentation.EditTaskViewModel
import ru.olegivo.repeatodo.list.presentation.TasksListViewModel
import ru.olegivo.repeatodo.list.presentation.TasksListViewModelImpl
import ru.olegivo.repeatodo.main.navigation.MainNavigator
import ru.olegivo.repeatodo.main.presentation.MainViewModel
import ru.olegivo.repeatodo.main.presentation.MainViewModelImpl

actual fun platformModule() = module {
    single { DriverFactory() }
    factory { MainViewModelImpl(get()) }.bind<MainViewModel>()
    factory { AddTaskViewModelImpl(get()) }.bind<AddTaskViewModel>()
    factory { TasksListViewModelImpl(get()) }.bind<TasksListViewModel>()
}

object MainComponent : KoinComponent {

    fun mainViewModel() = get<MainViewModel>()
    fun mainNavigator() = get<MainNavigator>()
}

object AddTaskComponent : KoinComponent {

    fun addTaskViewModel() = get<AddTaskViewModel>()
}

object EditTaskComponent : KoinComponent {

    fun editTaskViewModel() = get<EditTaskViewModel>()
}

object TasksListComponent : KoinComponent {

    fun tasksListViewModel() = get<TasksListViewModel>()
}
