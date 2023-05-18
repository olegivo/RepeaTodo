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

package ru.olegivo.repeatodo.list.presentation

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import ru.olegivo.repeatodo.booleans
import ru.olegivo.repeatodo.combinator
import ru.olegivo.repeatodo.domain.models.randomTask
import ru.olegivo.repeatodo.randomString

class TaskUiKtTest: FreeSpec({
    combinator(
        booleans,
        listOf(null, Instant.parse("2023-02-28T23:30:59.123Z"))
    ) { (isCompleted, lastCompletionDate) ->
        "toUi WHEN IsTaskCompletedUseCase return $isCompleted, lastCompletionDate is $lastCompletionDate" - {
            val task = randomTask().copy(lastCompletionDate = lastCompletionDate)
            val formattedLastCompletionDate = randomString()
            val relativeDateFormatter = FakeRelativeDateFormatter(formattedLastCompletionDate)

            val taskUi = task.toUi(
                isTaskCompleted = FakeIsTaskCompletedUseCase(isCompleted),
                relativeDateFormatter = relativeDateFormatter
            )

            "map fields as is" {
                taskUi.uuid shouldBe task.uuid
                taskUi.title shouldBe task.title
            }

            "isCompleted should be from IsTaskCompletedUseCase" {
                taskUi.isCompleted shouldBe isCompleted
            }

            "lastCompletionDate should use relative date formatter" {
                taskUi.lastCompletionDate shouldBe lastCompletionDate?.let {
                    formattedLastCompletionDate
                }
            }
        }
    }
})
