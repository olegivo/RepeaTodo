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

package ru.olegivo.repeatodo.main.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import ru.olegivo.repeatodo.BaseViewModel
import ru.olegivo.repeatodo.add.presentation.addTaskViewModelWithFakes
import ru.olegivo.repeatodo.list.domain.TasksListFilters
import ru.olegivo.repeatodo.list.presentation.taskListFakes
import ru.olegivo.repeatodo.main.navigation.MainNavigator
import ru.olegivo.repeatodo.utils.PreviewEnvironment

class MainViewModel(
    private val mainNavigator: MainNavigator,
    tasksListFilters: TasksListFilters
): BaseViewModel() {
    val state = MutableStateFlow(MainUiState())
    val isShowCompleted = tasksListFilters.isShowCompleted
    val isShowOnlyHighestPriority = tasksListFilters.isShowOnlyHighestPriority

    fun onAddTaskClicked() {
        mainNavigator.addTask()
    }
}

fun PreviewEnvironment.mainScreenFakes() {
    taskListFakes()
    addTaskViewModelWithFakes()
    addToDoListViewModelFakes()
}
