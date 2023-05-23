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

import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import ru.olegivo.repeatodo.BaseViewModel
import ru.olegivo.repeatodo.domain.GetToDoListsUseCase
import ru.olegivo.repeatodo.domain.WorkState
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.utils.PreviewEnvironment

abstract class DrawerToDoListsViewModel: BaseViewModel() {
    abstract val toDoLists: CStateFlow<List<ToDoList>>
}

class DrawerToDoListsViewModelImpl(
    getToDoLists: GetToDoListsUseCase,
): DrawerToDoListsViewModel() {
    override val toDoLists = getToDoLists()
        .filterIsInstance<WorkState.Completed<List<ToDoList>>>()
        .map { it.result }
        .asState(emptyList())
        .cStateFlow()
}

class DrawerToDoListsViewModelFake(
    toDoLists: List<ToDoList>
): DrawerToDoListsViewModel() {
    override val toDoLists = MutableStateFlow(toDoLists)
        .cStateFlow()
}

fun PreviewEnvironment.drawerToDoListsViewModelFakes(toDoLists: List<ToDoList>) {
    register<DrawerToDoListsViewModel> { DrawerToDoListsViewModelFake(toDoLists) }
    drawerToDoListsCustomItemViewModelFakes()
}
