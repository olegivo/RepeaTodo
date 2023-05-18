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

package ru.olegivo.repeatodo.add.presentation

import io.kotest.core.spec.IsolationMode
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.domain.FakeSaveTaskUseCase
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.randomString

internal class AddTaskViewModelTest: FreeSpec() {

    init {
        "viewModel" - {
            val saveTaskUseCase = FakeSaveTaskUseCase()
            val viewModel = AddTaskViewModel(saveTaskUseCase)

            "initial state" - {
                viewModel.isAdding.assertItem { shouldBeFalse() }
                viewModel.title.assertItem { shouldBeEmpty() }
                viewModel.canAdd.assertItem { shouldBeFalse() }
                saveTaskUseCase.savingTask.shouldBeNull()

                "change title" - {
                    val title = randomString()

                    viewModel.title.update { title }

                    viewModel.isAdding.assertItem { shouldBeFalse() }
                    viewModel.canAdd.assertItem { shouldBeTrue() }
                    saveTaskUseCase.savingTask.shouldBeNull()

                    "onAddClicked" - {
                        viewModel.onAddClicked()

                        viewModel.isAdding.assertItem { shouldBeTrue() }
                        viewModel.title.assertItem { shouldBe(title) }
                        viewModel.canAdd.assertItem { shouldBeFalse() }
                        saveTaskUseCase.savingTask.shouldNotBeNull().should {
                            it.title shouldBe title
                            it.daysPeriodicity shouldBe Task.DEFAULT_DAYS_PERIODICITY
                        }

                        "save success" {
                            saveTaskUseCase.setResultCompleted()

                            viewModel.isAdding.assertItem { shouldBeFalse() }
                            viewModel.title.assertItem { shouldBeEmpty() }
                            viewModel.canAdd.assertItem { shouldBeFalse() }
                        }

                        "save error" {
                            saveTaskUseCase.setResultError()

                            viewModel.title.assertItem { shouldBe(title) }
                            viewModel.isAdding.assertItem { shouldBeFalse() }
                            viewModel.canAdd.assertItem { shouldBeTrue() }
                        }
                    }
                }
            }
        }
    }
}
