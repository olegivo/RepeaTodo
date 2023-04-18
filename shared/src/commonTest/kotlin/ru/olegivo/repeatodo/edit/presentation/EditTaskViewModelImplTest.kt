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

import io.kotest.core.spec.IsolationMode
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.domain.GetTaskUseCase
import ru.olegivo.repeatodo.domain.SaveTaskUseCase
import ru.olegivo.repeatodo.domain.WorkState
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.kotest.LifecycleMode
import ru.olegivo.repeatodo.main.navigation.FakeMainNavigator
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
                saveTask = saveTaskUseCase,
                mainNavigator = FakeMainNavigator()
            )

            "initial state" - {
                viewModel.isLoading.assertItem { shouldBeTrue() }
                viewModel.isLoadingError.assertItem { shouldBeFalse() }
                viewModel.canSave.assertItem { shouldBeFalse() }
                viewModel.isSaving.assertItem { shouldBeFalse() }
                viewModel.title.assertItem { shouldBeEmpty() }

                "load request failed" {
                    getTaskUseCase.setResultError()

                    viewModel.isLoading.assertItem { shouldBeFalse() }
                    viewModel.isLoadingError.assertItem { shouldBeTrue() }
                    viewModel.canSave.assertItem { shouldBeFalse() }
                    viewModel.isSaving.assertItem { shouldBeFalse() }
                    viewModel.title.assertItem { shouldBeEmpty() }
                }

                "load request complete successfully" - {
                    getTaskUseCase.setResultCompleted(initialTask)

                    viewModel.isLoading.assertItem { shouldBeFalse() }
                    viewModel.isLoadingError.assertItem { shouldBeFalse() }
                    viewModel.isSaving.assertItem { shouldBeFalse() }
                    viewModel.title.assertItem { shouldBe(initialTask.title) }

                    "can't save WHEN has no changes" {
                        viewModel.canSave.assertItem { shouldBeFalse() }
                    }

                    "clear title" - {
                        viewModel.title.update { "" }

                        "canSave should be false WHEN the state is invalid" {
                            viewModel.canSave.assertItem { shouldBeFalse() }
                        }
                    }

                    "change title to other not empty" - {
                        val newTitle = randomString()
                        viewModel.title.update { newTitle }

                        "canSave should be true" {
                            viewModel.canSave.assertItem { shouldBeTrue() }
                        }

                        "onSaveClicked" - {
                            viewModel.onSaveClicked()

                            "should start saving" - {
                                viewModel.isSaving.assertItem { shouldBeTrue() }

                                "save completed" - {
                                    saveTaskUseCase.setResultCompleted()

                                    "saving state is completed" - {
                                        viewModel.isSaving.assertItem { shouldBeFalse() }
                                        viewModel.isSaveError.assertItem { shouldBeFalse() }
                                        viewModel.canSave.assertItem { shouldBeTrue() }

                                        "can't save WHEN new task became origin" {
                                            getTaskUseCase.setResultCompleted(
                                                initialTask.copy(title = newTitle)
                                            )
                                            viewModel.canSave.assertItem { shouldBeFalse() }
                                        }
                                    }
                                }

                                "save error" - {
                                    saveTaskUseCase.setResultError()

                                    "saving state is error" {
                                        viewModel.isSaving.assertItem { shouldBeFalse() }
                                        viewModel.isSaveError.assertItem { shouldBeTrue() }
                                        viewModel.canSave.assertItem { shouldBeTrue() }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    class FakeGetTaskUseCase : GetTaskUseCase {

        private val workState = MutableStateFlow<WorkState<Task>?>(null)

        override operator fun invoke(uuid: String): Flow<WorkState<Task>> {
            workState.update { WorkState.InProgress() }
            return workState.filterNotNull()
        }

        fun setResultCompleted(task: Task) {
            workState.update { WorkState.Completed(task) }
        }

        fun setResultError() {
            workState.update { WorkState.Error() }
        }
    }

    class FakeSaveTaskUseCase : SaveTaskUseCase {

        private val workState = MutableStateFlow<WorkState<Unit>?>(null)

        override suspend operator fun invoke(task: Task): Flow<WorkState<Unit>> {
            workState.update { WorkState.InProgress() }
            return workState.filterNotNull()
        }

        fun setResultCompleted() {
            workState.update { WorkState.Completed(Unit) }
        }

        fun setResultError() {
            workState.update { WorkState.Error() }
        }
    }
}
