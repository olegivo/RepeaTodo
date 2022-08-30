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

package ru.olegivo.repeatodo.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.data.FakeTasksRepository
import ru.olegivo.repeatodo.domain.models.createTask
import ru.olegivo.repeatodo.randomString

internal class GetTaskUseCaseImplTest : FreeSpec() {
    init {
        "GetTaskUseCaseImplTest created" - {
            val task = createTask()
            val tasksRepository = FakeTasksRepository()
            val useCase: GetTaskUseCase = GetTaskUseCaseImpl(tasksRepository = tasksRepository)

            "null should be returned WHEN has no task with specified uuid" {
                useCase.invoke(uuid = randomString())
                    .assertItem { shouldBeNull() }
            }

            "!the task should be returned WHEN has task with specified uuid" - {
                tasksRepository.add(task)

                useCase.invoke(uuid = randomString())
                    .assertItem { shouldBe(task) }
            }
        }
    }
}
