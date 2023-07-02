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

package ru.olegivo.repeatodo.domain

import app.cash.turbine.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.domain.models.randomToDoList
import ru.olegivo.repeatodo.randomList

class GetToDoListsUseCaseImplTest: FreeSpec() {
    init {
        "instance" - {
            val localToDoListsDataSource = FakeLocalToDoListsDataSource()
            val useCase: GetToDoListsUseCase =
                GetToDoListsUseCaseImpl(localToDoListsDataSource)

            "should return all todo lists from local data source" {
                val toDoLists = randomList { randomToDoList() }
                localToDoListsDataSource.toDoLists.update { toDoLists }

                useCase.invoke().test {
                    awaitItem() shouldBe (WorkState.InProgress())
                    awaitItem().shouldBeInstanceOf<WorkState.Completed<List<ToDoList>>>().result shouldBe toDoLists
                }
            }
        }
    }

}
