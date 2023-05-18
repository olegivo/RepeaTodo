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

package ru.olegivo.repeatodo.add.presentation

import app.cash.turbine.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModel
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModelImpl
import ru.olegivo.repeatodo.domain.AddTaskUseCase
import ru.olegivo.repeatodo.domain.models.Task

internal class AddTaskViewModelImplTest : FreeSpec({
    "viewModel" - {
        val viewModel: AddTaskViewModel = AddTaskViewModelImpl(FakeAddTaskUseCase())

        "initial state" {
            viewModel.onAdded.test {
                expectNoEvents()
            }

            viewModel.isLoading.test {
                awaitItem() shouldBe false
            }


        }
    }
}) {

    class FakeAddTaskUseCase : AddTaskUseCase {

        override suspend fun invoke(task: Task) {
            TODO("Not yet implemented")
        }
    }
}
