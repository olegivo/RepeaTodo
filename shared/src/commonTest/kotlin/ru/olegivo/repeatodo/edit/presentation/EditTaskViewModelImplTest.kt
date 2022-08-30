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

package ru.olegivo.repeatodo.edit.presentation

import app.cash.turbine.test
import io.kotest.core.spec.IsolationMode
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.assertNoEvents
import ru.olegivo.repeatodo.domain.GetTaskUseCase
import ru.olegivo.repeatodo.domain.SaveTaskUseCase
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.kotest.LifecycleMode
import ru.olegivo.repeatodo.randomString

internal class EditTaskViewModelImplTest : FreeSpec(LifecycleMode.Root) {

    override fun isolationMode() = IsolationMode.InstancePerLeaf

    init {
        "viewModel" - {
            val initialTask = Task(
                uuid = randomString(),
                title = randomString()
            )
            val getTaskUseCase = FakeGetTaskUseCase()
            val saveTaskUseCase = FakeSaveTaskUseCase()

            val viewModel: EditTaskViewModel = EditTaskViewModelImpl(
                uuid = initialTask.uuid,
                getTask = getTaskUseCase,
                saveTask = saveTaskUseCase
            )

            "initial state" - {
                viewModel.isLoading.assertItem { shouldBeFalse() } // TODO: change loading state
                viewModel.isSaving.assertItem { shouldBeFalse() }
                viewModel.canSave.assertItem { shouldBeFalse() }
                viewModel.title.assertItem { shouldBeEmpty() }
                viewModel.onSaved.assertNoEvents()
                saveTaskUseCase.hasStarted.shouldBeFalse()
                saveTaskUseCase.hasCompleted.shouldBeFalse()

                "when has no task with specified uuid" {
                    getTaskUseCase.completeRequest(null)

                    viewModel.title.test { awaitItem() shouldBe "" }
                }

                "after request complete" - {
                    getTaskUseCase.completeRequest(initialTask)

                    viewModel.title.assertItem { shouldBe(initialTask.title) }

                    "change title to other not empty" - {
                        val newTitle = randomString()
                        viewModel.title.update { newTitle }

                        "canSave should be true" {
                            viewModel.canSave.assertItem { shouldBeTrue() }
                        }

                        "onSaveClicked" - {
                            viewModel.onSaved.test {
                                viewModel.onSaveClicked()

                                "isSaving should be true" {
                                    viewModel.isSaving.assertItem { shouldBeTrue() }
                                }

                                "onSaved should not be signaled" {
                                    expectNoEvents()
                                }

                                "should start saving" - {
                                    saveTaskUseCase.hasStarted.shouldBeTrue()

                                    "still saving" {
                                        viewModel.isSaving.assertItem { shouldBeTrue() }
                                    }

                                    "after save" - {
                                        saveTaskUseCase.completeRequest()

                                        "should not be in saving state" {
                                            viewModel.isSaving.assertItem { shouldBeFalse() }
                                        }

                                        "should be saved" {
                                            saveTaskUseCase.hasCompleted.shouldBeTrue()
                                        }

                                        "onSaved should be signaled" {
                                            awaitItem()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "clear title" - {
                        viewModel.title.update { "" }

                        "canSave should be true" {
                            viewModel.canSave.assertItem { shouldBeFalse() }
                        }
                    }
                }
            }
        }
    }

    class FakeGetTaskUseCase() : GetTaskUseCase {

        private val getTaskRequest = CompletableDeferred<Task?>()

        fun completeRequest(task: Task?) {
            getTaskRequest.complete(task)
        }

        override operator fun invoke(uuid: String) = flow {
            emit(getTaskRequest.await())
        }
    }

    class FakeSaveTaskUseCase : SaveTaskUseCase {

        private val saveTaskRequest = Job()

        var hasStarted = false
            private set

        var hasCompleted = false
            private set

        fun completeRequest() {
            saveTaskRequest.complete()
        }

        override suspend operator fun invoke(task: Task) {
            hasStarted = true
            saveTaskRequest.join()
            hasCompleted = true
        }
    }
}
