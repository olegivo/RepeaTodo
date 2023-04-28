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

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.domain.FakeDeleteTaskUseCase
import ru.olegivo.repeatodo.domain.FakeGetTaskUseCase
import ru.olegivo.repeatodo.domain.FakeSaveTaskUseCase
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.domain.models.randomTask
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.main.navigation.FakeMainNavigator
import ru.olegivo.repeatodo.randomString

internal class EditTaskViewModelImplTest: FreeSpec() {

    init {
        "viewModel" - {
            val initialTask = randomTask()
            val getTaskUseCase = FakeGetTaskUseCase()
            val saveTaskUseCase = FakeSaveTaskUseCase()
            val deleteTaskUseCase = FakeDeleteTaskUseCase()
            val mainNavigator = FakeMainNavigator()

            val viewModel = EditTaskViewModel(
                uuid = initialTask.uuid,
                getTask = getTaskUseCase,
                saveTask = saveTaskUseCase,
                deleteTask = deleteTaskUseCase,
                mainNavigator = mainNavigator
            )

            "initial state" - {
                viewModel.isLoading.assertItem { shouldBeTrue() }
                viewModel.isLoadingError.assertItem { shouldBeFalse() }
                viewModel.canSave.assertItem { shouldBeFalse() }
                viewModel.isSaving.assertItem { shouldBeFalse() }
                viewModel.canDelete.assertItem { shouldBeFalse() }
                viewModel.isDeleting.assertItem { shouldBeFalse() }
                viewModel.isDeleteError.assertItem { shouldBeFalse() }
                viewModel.title.assertItem { shouldBeEmpty() }
                viewModel.daysPeriodicity.assertItem { shouldBeEmpty() }

                "onCancelClicked should navigate back" {
                    viewModel.onCancelClicked()

                    mainNavigator.invocations shouldBe FakeMainNavigator.Invocations.Back
                }

                "load request failed" {
                    getTaskUseCase.setResultError()

                    viewModel.isLoading.assertItem { shouldBeFalse() }
                    viewModel.isLoadingError.assertItem { shouldBeTrue() }
                    viewModel.canSave.assertItem { shouldBeFalse() }
                    viewModel.isSaving.assertItem { shouldBeFalse() }
                    viewModel.canDelete.assertItem { shouldBeFalse() }
                    viewModel.isDeleting.assertItem { shouldBeFalse() }
                    viewModel.isDeleteError.assertItem { shouldBeFalse() }
                    viewModel.title.assertItem { shouldBeEmpty() }
                    viewModel.daysPeriodicity.assertItem { shouldBeEmpty() }
                }

                "load request complete successfully" - {
                    getTaskUseCase.setResultCompleted(initialTask)

                    viewModel.isLoading.assertItem { shouldBeFalse() }
                    viewModel.isLoadingError.assertItem { shouldBeFalse() }
                    viewModel.isSaving.assertItem { shouldBeFalse() }
                    viewModel.canDelete.assertItem { shouldBeTrue() }
                    viewModel.isDeleting.assertItem { shouldBeFalse() }
                    viewModel.isDeleteError.assertItem { shouldBeFalse() }
                    viewModel.title.assertItem { shouldBe(initialTask.title) }
                    viewModel.daysPeriodicity.assertItem { shouldBe(initialTask.daysPeriodicity.toString()) }

                    "should not navigate" {
                        mainNavigator.invocations shouldBe FakeMainNavigator.Invocations.None
                    }

                    "can't save WHEN has no changes" {
                        viewModel.canSave.assertItem { shouldBeFalse() }
                    }

                    "clear title" - {
                        viewModel.title.update { "" }

                        "canSave should be false WHEN the state is invalid" {
                            viewModel.canSave.assertItem { shouldBeFalse() }
                        }
                    }

                    "clear daysPeriodicity" - {
                        viewModel.daysPeriodicity.update { "" }

                        "canSave should be false WHEN the state is invalid" {
                            viewModel.canSave.assertItem { shouldBeFalse() }
                        }
                    }

                    "set daysPeriodicity less than minimum" - {
                        viewModel.daysPeriodicity.update { (Task.MIN_DAYS_PERIODICITY - 1).toString() }

                        "canSave should be false WHEN the state is invalid" {
                            viewModel.canSave.assertItem { shouldBeFalse() }
                        }
                    }

                    "set daysPeriodicity greater than maximum" - {
                        viewModel.daysPeriodicity.update { (Task.MAX_DAYS_PERIODICITY + 1).toString() }

                        "canSave should be false WHEN the state is invalid" {
                            viewModel.canSave.assertItem { shouldBeFalse() }
                        }
                    }

                    "change daysPeriodicity to other in range" - {
                        viewModel.daysPeriodicity.update {
                            val value = it.toInt()
                            if (value == Task.MIN_DAYS_PERIODICITY) {
                                value + 1
                            } else {
                                value - 1
                            }.toString()
                        }

                        "canSave should be true" {
                            viewModel.canSave.assertItem { shouldBeTrue() }
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

                                        "should navigate back" {
                                            mainNavigator.invocations shouldBe FakeMainNavigator.Invocations.Back
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

                                    "should not navigate" {
                                        mainNavigator.invocations shouldBe FakeMainNavigator.Invocations.None
                                    }
                                }
                            }
                        }

                        "onDeleteClicked" - {
                            viewModel.onDeleteClicked()

                            "should start deleting" - {
                                viewModel.isDeleting.assertItem { shouldBeTrue() }

                                "can't delete while deleting" {
                                    viewModel.canDelete.assertItem { shouldBeFalse() }
                                }

                                "delete completed" - {
                                    deleteTaskUseCase.setResultCompleted()

                                    "deleting state is completed" - {
                                        viewModel.isDeleting.assertItem { shouldBeFalse() }
                                        viewModel.isDeleteError.assertItem { shouldBeFalse() }
                                        viewModel.canDelete.assertItem { shouldBeFalse() }

                                        "should navigate back" {
                                            mainNavigator.invocations shouldBe FakeMainNavigator.Invocations.Back
                                        }
                                    }
                                }

                                "delete error" - {
                                    deleteTaskUseCase.setResultError()

                                    "deleting state is error" {
                                        viewModel.isDeleting.assertItem { shouldBeFalse() }
                                        viewModel.isDeleteError.assertItem { shouldBeTrue() }
                                        viewModel.canDelete.assertItem { shouldBeTrue() }
                                    }

                                    "should not navigate" {
                                        mainNavigator.invocations shouldBe FakeMainNavigator.Invocations.None
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
