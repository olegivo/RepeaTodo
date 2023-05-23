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

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.domain.GetToDoListsUseCase
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.domain.WorkState
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.utils.newUuid

class DrawerToDoListsViewModelTest: FreeSpec() {
    init {
        "instance" - {
            val getToDoListsUseCase = FakeGetToDoListsUseCase()
            val viewModel = DrawerToDoListsViewModelImpl(
                getToDoLists = getToDoListsUseCase,
            )

            val todoLists = viewModel.toDoLists.testIn()
            val allToDoLists =
                (1..5).map { ToDoList.Custom(newUuid(), "List $it") }

            "initial state" - {
                todoLists.awaitItem().shouldBeEmpty()

                "actual state after loading" - {
                    getToDoListsUseCase.setResultCompleted(allToDoLists)

                    todoLists.awaitItem() shouldBe allToDoLists
                }
            }
        }
    }

    class FakeGetToDoListsUseCase: GetToDoListsUseCase {
        private val todoLists = MutableStateFlow<WorkState<List<ToDoList>>?>(null)

        override fun invoke() = todoLists.filterNotNull()

        fun setResultCompleted(todoLists: List<ToDoList>) {
            this.todoLists.update { WorkState.Completed(todoLists) }
        }
    }
}
