/*
 * Copyright (C) 2023 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.repeatodo.list.presentation

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.domain.models.randomTask

class TaskUiKtTest: FreeSpec({
    listOf(true, false).forEach { isCompleted ->
        "toUi WHEN IsTaskCompletedUseCase return $isCompleted" {
            val task = randomTask()

            val taskUi = task.toUi(isTaskCompleted = FakeIsTaskCompletedUseCase(isCompleted))

            taskUi.uuid shouldBe task.uuid
            taskUi.title shouldBe task.title
            taskUi.isCompleted shouldBe isCompleted
        }
    }
})
