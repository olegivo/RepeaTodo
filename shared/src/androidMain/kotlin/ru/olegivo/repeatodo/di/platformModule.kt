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

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.olegivo.repeatodo.DispatchersProvider
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModel
import ru.olegivo.repeatodo.edit.presentation.EditTaskViewModel
import ru.olegivo.repeatodo.list.presentation.TasksListViewModel
import ru.olegivo.repeatodo.main.presentation.MainViewModel
import ru.olegivo.repeatodo.main.presentation.MainViewModelImpl
import ru.olegivo.repeatodo.platform.DispatchersProviderImpl

actual fun platformModule() = module {
    singleOf(::DispatchersProviderImpl).bind<DispatchersProvider>()
    viewModelOf(::MainViewModelImpl).bind<MainViewModel>()
    viewModelOf(::AddTaskViewModel)
    viewModelOf(::TasksListViewModel)
    viewModelOf(::EditTaskViewModel)
}
