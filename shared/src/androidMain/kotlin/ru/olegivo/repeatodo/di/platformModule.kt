package ru.olegivo.repeatodo.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModel
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModelImpl
import ru.olegivo.repeatodo.main.presentation.MainViewModel
import ru.olegivo.repeatodo.main.presentation.MainViewModelImpl

actual fun platformModule() = module {
    viewModel { MainViewModelImpl(get()) }.bind<MainViewModel>()
    viewModel { AddTaskViewModelImpl(get()) }.bind<AddTaskViewModel>()
}
