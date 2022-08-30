/*
 * Copyright (C) 2022 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of RepeaTodo.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * RepeaTodo.
 */

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
