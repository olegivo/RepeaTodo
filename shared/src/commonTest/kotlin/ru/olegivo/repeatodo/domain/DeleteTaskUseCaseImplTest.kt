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

import app.cash.turbine.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.data.FakeTasksRepository
import ru.olegivo.repeatodo.domain.models.randomTask

internal class DeleteTaskUseCaseImplTest : FreeSpec() {
    init {
        "should delete only exist task from repository" {
            val tasksRepository = FakeTasksRepository()
            val deleteTaskUseCase: DeleteTaskUseCase = DeleteTaskUseCaseImpl(
                tasksRepository = tasksRepository
            )
            val origin = randomTask()
            tasksRepository.save(origin)
            val task2 = randomTask()
            tasksRepository.save(task2)

            deleteTaskUseCase(origin).test {
                expectMostRecentItem() shouldBe WorkState.Completed(Unit)
            }

            tasksRepository.getTasks().assertItem {
                shouldNotContain(origin)
                shouldContain(task2)
            }
        }
    }
}
