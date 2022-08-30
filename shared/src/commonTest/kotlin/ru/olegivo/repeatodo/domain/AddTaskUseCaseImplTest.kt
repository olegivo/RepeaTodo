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
import ru.olegivo.repeatodo.data.FakeTasksRepository
import ru.olegivo.repeatodo.domain.models.createTask
import kotlin.test.assertEquals

class AddTaskUseCaseImplTest : FreeSpec({
    "AddTaskUseCaseImpl created" - {
        val tasksRepository = FakeTasksRepository()
        val useCase: AddTaskUseCase = AddTaskUseCaseImpl(tasksRepository = tasksRepository)

        "invoke should add task to repository" {
            val task = createTask()

            useCase.invoke(task)

            assertEquals(task, tasksRepository.lastAddedTask)
        }
    }
})
