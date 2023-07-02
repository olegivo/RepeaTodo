/*
 * Copyright (C) 2023 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of RepeaTodo.
 *
 * RepeaTodo is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * RepeaTodo PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * RepeaTodo.
 */

package ru.olegivo.repeatodo.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.olegivo.repeatodo.DispatchersProvider
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModel
import ru.olegivo.repeatodo.db.DriverFactory
import ru.olegivo.repeatodo.db.DriverFactoryImpl
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.edit.presentation.EditTaskViewModel
import ru.olegivo.repeatodo.list.presentation.TasksListViewModel
import ru.olegivo.repeatodo.main.navigation.MainNavigator
import ru.olegivo.repeatodo.main.presentation.AddToDoListViewModel
import ru.olegivo.repeatodo.main.presentation.AddToDoListViewModelImpl
import ru.olegivo.repeatodo.main.presentation.DrawerToDoListsCustomItemViewModel
import ru.olegivo.repeatodo.main.presentation.DrawerToDoListsCustomItemViewModelImpl
import ru.olegivo.repeatodo.main.presentation.DrawerToDoListsViewModel
import ru.olegivo.repeatodo.main.presentation.DrawerToDoListsViewModelImpl
import ru.olegivo.repeatodo.main.presentation.MainViewModel
import ru.olegivo.repeatodo.platform.DispatchersProviderImpl

actual val platformModule = module {
    singleOf(::DispatchersProviderImpl).bind<DispatchersProvider>()
    singleOf(::DriverFactoryImpl).bind<DriverFactory>()
    factoryOf(::MainViewModel)
    factoryOf(::AddTaskViewModel)
    factoryOf(::EditTaskViewModel)
    factoryOf(::TasksListViewModel)
    factoryOf(::DrawerToDoListsViewModelImpl).bind<DrawerToDoListsViewModel>()
    factoryOf(::DrawerToDoListsCustomItemViewModelImpl).bind<DrawerToDoListsCustomItemViewModel>()
    singleOf(::AddToDoListViewModelImpl).bind<AddToDoListViewModel>()
}

object MainComponent: KoinComponent {
    fun mainViewModel() = get<MainViewModel>()
    fun mainNavigator() = get<MainNavigator>()
}

object ToDoListsComponent: KoinComponent {
    fun drawerToDoListsViewModel() = get<DrawerToDoListsViewModel>()
    fun addToDoListViewModel() = get<AddToDoListViewModel>()
    fun drawerToDoListsCustomItemViewModel(item: ToDoList) =
        get<DrawerToDoListsCustomItemViewModel> { parametersOf(item) }
}

object AddTaskComponent: KoinComponent {
    fun addTaskViewModel() = get<AddTaskViewModel>()
}

object EditTaskComponent: KoinComponent {
    fun editTaskViewModel(uuid: String) = get<EditTaskViewModel> { parametersOf(uuid) }
}

object TasksListComponent: KoinComponent {
    fun tasksListViewModel() = get<TasksListViewModel>()
}
