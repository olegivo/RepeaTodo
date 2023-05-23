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

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.domain.FakeDeleteCustomToDoListUseCase
import ru.olegivo.repeatodo.domain.models.randomToDoList
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.randomString

class DrawerToDoListsCustomItemViewModelImplTest: FreeSpec() {
    init {
        "instance" - {
            val toDoList = randomToDoList()
            val saveCustomToDoListUseCase = FakeSaveCustomToDoListUseCase()
            val deleteCustomToDoListUseCase = FakeDeleteCustomToDoListUseCase()
            val viewModel: DrawerToDoListsCustomItemViewModel =
                DrawerToDoListsCustomItemViewModelImpl(
                    toDoList = toDoList,
                    saveCustomToDoList = saveCustomToDoListUseCase,
                    deleteCustomToDoList = deleteCustomToDoListUseCase
                )
            val isEditing = viewModel.isEditing.testIn()
            val canSave = viewModel.canSave.testIn()

            "initial state" {
                viewModel.title.assertItem { shouldBe(toDoList.title) }
                isEditing.expectMostRecentItem().shouldBeFalse()
                viewModel.showDeleteConfirmation.assertItem { shouldBeFalse() }
            }

            "begin edit" - {
                viewModel.onBeginEditClicked()

                "should indicate editing" {
                    isEditing.expectMostRecentItem().shouldBeTrue()
                }

                "should not indicate saving" {
                    viewModel.isSaving.assertItem { shouldBeFalse() }
                }

                "should not allow save WHEN have no changes" {
                    canSave.expectMostRecentItem().shouldBeFalse()
                }

                "clear title" - {
                    viewModel.title.update { "" }

                    "should not allow save" {
                        viewModel.canSave.assertItem { shouldBeFalse() }
                    }
                }

                "change title to another not empty" - {
                    val newTitle = randomString()
                    viewModel.title.update { newTitle }

                    "should allow save" {
                        canSave.expectMostRecentItem().shouldBeTrue()
                    }

                    "cancel edit" - {
                        viewModel.onCancelEditClicked()

                        "should indicate not editing" {
                            isEditing.expectMostRecentItem().shouldBeFalse()
                        }

                        "should revert title to origin" {
                            viewModel.title.assertItem { shouldBe(toDoList.title) }
                        }
                    }

                    "onSaveClicked" - {
                        viewModel.onSaveClicked()

                        "should start saving" - {
                            saveCustomToDoListUseCase.savingToDoList shouldBe toDoList.copy(title = newTitle)
                            viewModel.isSaving.assertItem { shouldBeTrue() }

                            "save completed" - {
                                saveCustomToDoListUseCase.setResultCompleted()

                                "should indicate not saving" {
                                    viewModel.isSaving.assertItem { shouldBeFalse() }
                                }

                                "should indicate not editing" {
                                    viewModel.isEditing.assertItem { shouldBeFalse() }
                                }
                            }

                            "save error" - {
                                saveCustomToDoListUseCase.setResultError()

                                "should indicate not saving" {
                                    viewModel.isSaving.assertItem { shouldBeFalse() }
                                }

                                "should indicate editing" {
                                    viewModel.isEditing.assertItem { shouldBeTrue() }
                                }
                            }
                        }
                    }
                }
            }

            "onDeleteClicked" - {
                viewModel.onDeleteClicked()

                "should raise delete confirmation" {
                    viewModel.showDeleteConfirmation.assertItem { shouldBeTrue() }
                }

                "onDeleteConfirmed" - {
                    viewModel.onDeleteConfirmed()

                    "should not show delete confirmation" {
                        viewModel.showDeleteConfirmation.assertItem { shouldBeFalse() }
                    }

                    "should start deleting" - {
                        viewModel.isDeleting.assertItem { shouldBeTrue() }

                        "can't delete while deleting" {
                            viewModel.canDelete.assertItem { shouldBeFalse() }
                        }

                        "delete completed" - {
                            deleteCustomToDoListUseCase.setResultCompleted()

                            "deleting state is completed" - {
                                viewModel.isDeleteError.assertItem { shouldBeFalse() }
                                viewModel.canDelete.assertItem { shouldBeFalse() }

                                "should indicate not deleting" {
                                    viewModel.isDeleting.assertItem { shouldBeFalse() }
                                }

                                "should indicate not editing" {
                                    viewModel.isEditing.assertItem { shouldBeFalse() }
                                }
                            }
                        }

                        "delete error" - {
                            deleteCustomToDoListUseCase.setResultError()

                            "deleting state is error" {
                                viewModel.isDeleting.assertItem { shouldBeFalse() }
                                viewModel.isDeleteError.assertItem { shouldBeTrue() }
                                viewModel.canDelete.assertItem { shouldBeTrue() }
                            }
                        }
                    }
                }

                "onDeleteDismissed" - {
                    viewModel.onDeleteDismissed()

                    "should not show delete confirmation" {
                        viewModel.showDeleteConfirmation.assertItem { shouldBeFalse() }
                    }
                }
            }
        }
    }
}
