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

package ru.olegivo.repeatodo.list.presentation

import app.cash.turbine.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import ru.olegivo.repeatodo.domain.GetTasksListUseCase
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.domain.models.createTask
import ru.olegivo.repeatodo.randomList
import ru.olegivo.repeatodo.randomString

internal class TasksListViewModelImplTest : FreeSpec({
    val mainThreadSurrogate = newSingleThreadContext("UI thread")
    beforeTest {
        Dispatchers.setMain(mainThreadSurrogate)
    }
    afterTest {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
    "initial state" - {
        val list = randomList { createTask() }
        val getTasksListUseCase = FakeGetTasksListUseCase()

        val viewModel: TasksListViewModel = TasksListViewModelImpl(getTasksListUseCase)

        viewModel.state.test {
            awaitItem().tasks.shouldBeEmpty()

            "actual state after loading" {
                getTasksListUseCase.list.update { list }

                awaitItem().tasks shouldBe list
            }
        }
    }
}) {

    class FakeGetTasksListUseCase : GetTasksListUseCase {

        val list = MutableStateFlow<List<Task>>(emptyList())

        override fun invoke(): Flow<List<Task>> = list
    }
}
