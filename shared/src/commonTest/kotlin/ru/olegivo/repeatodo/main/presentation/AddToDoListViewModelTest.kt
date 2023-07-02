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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import kotlinx.coroutines.flow.update
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.randomString

class AddToDoListViewModelTest: FreeSpec() {
    init {
        "instance" - {
            val saveTaskUseCase = FakeSaveCustomToDoListUseCase()
            val viewModel = AddToDoListViewModelImpl(
                saveCustomToDoList = saveTaskUseCase
            )

            "initial state" - {
                "should not indicate editing new list" {
                    viewModel.isEditingNew.assertItem { shouldBeFalse() }
                }

                "begin adding new" - {
                    viewModel.beginAddingNew()

                    "should indicate editing new list" {
                        viewModel.isEditingNew.assertItem { shouldBeTrue() }
                    }

                    "initial title should be empty" {
                        viewModel.title.assertItem { shouldBeEmpty() }
                    }

                    "should not allow save WHEN title is empty" {
                        viewModel.canSaveNew.assertItem { shouldBeFalse() }
                    }

                    "change title to not empty" - {
                        val newTitle = randomString()
                        viewModel.title.update { newTitle }

                        "should allow save" {
                            viewModel.canSaveNew.assertItem { shouldBeTrue() }
                        }

                        "cancel add new list" - {
                            viewModel.cancelAddNew()

                            "should not indicate editing new list" {
                                viewModel.isEditingNew.assertItem { shouldBeFalse() }
                            }

                            "should revert title to empty" {
                                viewModel.title.assertItem { shouldBeEmpty() }
                            }
                        }

                        "onSaveClicked" - {
                            viewModel.onSaveClicked()

                            "should start saving" - {
                                saveTaskUseCase.savingToDoList.shouldNotBeNull().title shouldBe newTitle
                                viewModel.isSaving.assertItem { shouldBeTrue() }

                                "save completed" - {
                                    saveTaskUseCase.setResultCompleted()

                                    "should indicate not saving" {
                                        viewModel.isSaving.assertItem { shouldBeFalse() }
                                    }

                                    "should indicate not editing" {
                                        viewModel.isEditingNew.assertItem { shouldBeFalse() }
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
