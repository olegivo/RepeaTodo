package ru.olegivo.repeatodo.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModel
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModelImpl
import ru.olegivo.repeatodo.main.navigation.MainNavigator
import ru.olegivo.repeatodo.main.presentation.MainViewModel
import ru.olegivo.repeatodo.main.presentation.MainViewModelImpl
import ru.olegivo.repeatodo.presentation.TasksListViewModelImpl
import ru.olegivo.repeatodo.presentation.TasksListViewModel

actual fun platformModule() = module {
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

object TasksListComponent : KoinComponent {
    fun tasksListViewModel() = get<TasksListViewModel>()
}
