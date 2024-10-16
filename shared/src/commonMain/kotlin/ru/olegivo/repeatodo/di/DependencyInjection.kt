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

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.olegivo.repeatodo.db.InstantLongAdapter
import ru.olegivo.repeatodo.db.LocalTasksDataSourceImpl
import ru.olegivo.repeatodo.db.LocalToDoListsDataSourceImpl
import ru.olegivo.repeatodo.db.PriorityLongAdapter
import ru.olegivo.repeatodo.db.createDatabase
import ru.olegivo.repeatodo.domain.AddTaskUseCase
import ru.olegivo.repeatodo.domain.AddTaskUseCaseImpl
import ru.olegivo.repeatodo.domain.CancelTaskCompletionUseCase
import ru.olegivo.repeatodo.domain.CancelTaskCompletionUseCaseImpl
import ru.olegivo.repeatodo.domain.CompleteTaskUseCase
import ru.olegivo.repeatodo.domain.CompleteTaskUseCaseImpl
import ru.olegivo.repeatodo.domain.DateTimeProvider
import ru.olegivo.repeatodo.domain.DateTimeProviderImpl
import ru.olegivo.repeatodo.domain.DeleteCustomToDoListUseCase
import ru.olegivo.repeatodo.domain.DeleteCustomToDoListUseCaseImpl
import ru.olegivo.repeatodo.domain.DeleteTaskUseCase
import ru.olegivo.repeatodo.domain.DeleteTaskUseCaseImpl
import ru.olegivo.repeatodo.domain.GetTaskUseCase
import ru.olegivo.repeatodo.domain.GetTaskUseCaseImpl
import ru.olegivo.repeatodo.domain.GetTasksListUseCase
import ru.olegivo.repeatodo.domain.GetTasksListUseCaseImpl
import ru.olegivo.repeatodo.domain.GetToDoListsUseCase
import ru.olegivo.repeatodo.domain.GetToDoListsUseCaseImpl
import ru.olegivo.repeatodo.domain.IsTaskCompletedUseCase
import ru.olegivo.repeatodo.domain.IsTaskCompletedUseCaseImpl
import ru.olegivo.repeatodo.domain.LocalTasksDataSource
import ru.olegivo.repeatodo.domain.LocalToDoListsDataSource
import ru.olegivo.repeatodo.domain.SaveCustomToDoListUseCase
import ru.olegivo.repeatodo.domain.SaveCustomToDoListUseCaseImpl
import ru.olegivo.repeatodo.domain.SaveTaskUseCase
import ru.olegivo.repeatodo.domain.SaveTaskUseCaseImpl
import ru.olegivo.repeatodo.edit.navigation.EditTaskNavigator
import ru.olegivo.repeatodo.list.domain.TasksListFilters
import ru.olegivo.repeatodo.list.presentation.RelativeDateFormatter
import ru.olegivo.repeatodo.list.presentation.RelativeDateFormatterImpl
import ru.olegivo.repeatodo.list.presentation.TasksSorterByCompletion
import ru.olegivo.repeatodo.main.navigation.MainNavigator
import ru.olegivo.repeatodo.main.navigation.MainNavigatorImpl

object DependencyInjection {

    fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
        startKoin {
            appDeclaration()
            modules(commonModule(), platformModule)
        }
    }

    fun initKoinAndReturnInstance(appDeclaration: KoinAppDeclaration = {}): Koin =
        startKoin {
            appDeclaration()
            modules(commonModule(), platformModule)
        }.koin

    private fun commonModule() = module {
        singleOf(::MainNavigatorImpl)
            .bind<MainNavigator>()
            .bind<EditTaskNavigator>()
        singleOf(::LocalTasksDataSourceImpl).bind<LocalTasksDataSource>()
        singleOf(::LocalToDoListsDataSourceImpl).bind<LocalToDoListsDataSource>()
        singleOf(::createDatabase)
        singleOf(::TasksListFilters) // TODO: scoped instead of single
        factoryOf(::InstantLongAdapter)
        factoryOf(::PriorityLongAdapter)
        factoryOf(::DateTimeProviderImpl).bind<DateTimeProvider>()
        factoryOf(::RelativeDateFormatterImpl).bind<RelativeDateFormatter>()
        factoryOf(::AddTaskUseCaseImpl).bind<AddTaskUseCase>()
        factoryOf(::GetTasksListUseCaseImpl).bind<GetTasksListUseCase>()
        factoryOf(::GetTaskUseCaseImpl).bind<GetTaskUseCase>()
        factoryOf(::SaveTaskUseCaseImpl).bind<SaveTaskUseCase>()
        factoryOf(::DeleteTaskUseCaseImpl).bind<DeleteTaskUseCase>()
        factoryOf(::CompleteTaskUseCaseImpl).bind<CompleteTaskUseCase>()
        factoryOf(::CancelTaskCompletionUseCaseImpl).bind<CancelTaskCompletionUseCase>()
        factoryOf(::IsTaskCompletedUseCaseImpl).bind<IsTaskCompletedUseCase>()
        factoryOf(::GetToDoListsUseCaseImpl).bind<GetToDoListsUseCase>()
        factoryOf(::SaveCustomToDoListUseCaseImpl).bind<SaveCustomToDoListUseCase>()
        factoryOf(::DeleteCustomToDoListUseCaseImpl).bind<DeleteCustomToDoListUseCase>()
        factoryOf(::TasksSorterByCompletion)

        includes(platformModule)
    }
}

internal expect val platformModule: Module
