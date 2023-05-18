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
import ru.olegivo.repeatodo.data.FakeLocalTasksDataSource
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.domain.models.randomTask
import ru.olegivo.repeatodo.randomString

internal class GetTaskUseCaseImplTest: FreeSpec() {
    init {
        "GetTaskUseCaseImplTest created" - {
            val task = randomTask()
            val localTasksDataSource = FakeLocalTasksDataSource()
            val useCase: GetTaskUseCase =
                GetTaskUseCaseImpl(localTasksDataSource = localTasksDataSource)

            "error should be returned WHEN has no task with specified uuid" {
                useCase.invoke(uuid = randomString())
                    .test {
                        awaitItem() shouldBe (WorkState.InProgress())
                        awaitItem() shouldBe (WorkState.Error())
                    }
            }

            "the task should be returned WHEN has task with specified uuid" {
                localTasksDataSource.save(task)

                useCase.invoke(uuid = task.uuid).test {
                    awaitItem() shouldBe (WorkState.InProgress())
                    awaitItem().shouldBeInstanceOf<WorkState.Completed<Task>>().result shouldBe task
                }
            }
        }
    }
}
