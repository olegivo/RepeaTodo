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

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import ru.olegivo.repeatodo.domain.FakeDateTimeProvider
import ru.olegivo.repeatodo.domain.models.randomTask
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.randomInt
import ru.olegivo.repeatodo.randomString
import kotlin.time.Duration.Companion.minutes

class TasksSorterByCompletionTest: FreeSpec() {
    private val dateTimeProvider = FakeDateTimeProvider()

    init {
        "instance" - {
            val isTaskCompleted = FakeIsTaskCompletedUseCase()
            val tasksSorter: TasksSorter = TasksSorterByCompletion(
                dateTimeProvider = dateTimeProvider,
                isTaskCompleted = isTaskCompleted
            )

            "empty -> empty" {
                tasksSorter.sort(emptyList()).shouldBeEmpty()
            }

            "not completed, then completed" {
                val thresholdMinutes = randomInt(from = 10, until = 30)
                val threshold = dateTimeProvider.getCurrentInstant() - thresholdMinutes.minutes
                isTaskCompleted.considerAsCompletedAfter = threshold
                val completed = createTask(minutesAgo = thresholdMinutes - 1)
                val notCompleted = completed.copy(lastCompletionDate = getInstant(minutesAgo = thresholdMinutes))
                val tasks = listOf(completed, notCompleted)

                tasksSorter.sort(tasks)
                    .shouldContainExactly(notCompleted, completed)
            }

            "never completed, then completed" {
                val completed = createTask(minutesAgo = randomInt())
                val neverCompleted = completed.copy(lastCompletionDate = null)
                val tasks = listOf(completed, neverCompleted)

                tasksSorter.sort(tasks)
                    .shouldContainExactly(neverCompleted, completed)
            }

            "never completed: by shortest periodicity" {
                val periodicity1 = createTask(
                    minutesAgo = null,
                    daysPeriodicity = 1
                )
                val periodicity2 = createTask(
                    minutesAgo = null,
                    daysPeriodicity = 2
                )
                val tasks = listOf(periodicity2, periodicity1)

                tasksSorter.sort(tasks)
                    .shouldContainExactly(periodicity1, periodicity2)
            }

            "never completed + same periodicity: by title" {
                val title1 = createTask(
                    minutesAgo = null,
                    daysPeriodicity = 1,
                    title = "1"
                )
                val title2 = createTask(
                    minutesAgo = null,
                    daysPeriodicity = 1,
                    title = "2"
                )
                val tasks = listOf(title2, title1)

                tasksSorter.sort(tasks)
                    .shouldContainExactly(title1, title2)
            }

            "completed: by oldest last completion" {
                val oneMinuteAgo = createTask(
                    minutesAgo = 1
                )
                val twoMinuteAgo = createTask(
                    minutesAgo = 2
                )
                val tasks = listOf(oneMinuteAgo, twoMinuteAgo)

                tasksSorter.sort(tasks)
                    .shouldContainExactly(twoMinuteAgo, oneMinuteAgo)
            }

            "completed + same last completion: by shortest periodicity" {
                val periodicity1 = createTask(
                    minutesAgo = 1,
                    daysPeriodicity = 1
                )
                val periodicity2 = createTask(
                    minutesAgo = 1,
                    daysPeriodicity = 2
                )
                val tasks = listOf(periodicity2, periodicity1)

                tasksSorter.sort(tasks)
                    .shouldContainExactly(periodicity1, periodicity2)
            }

            "completed + same last completion + same periodicity: by title" {
                val title1 = createTask(
                    minutesAgo = 1,
                    daysPeriodicity = 1,
                    title = "1"
                )
                val title2 = createTask(
                    minutesAgo = 1,
                    daysPeriodicity = 1,
                    title = "2"
                )
                val tasks = listOf(title2, title1)

                tasksSorter.sort(tasks)
                    .shouldContainExactly(title1, title2)
            }
        }
    }

    private fun createTask(
        minutesAgo: Int?,
        daysPeriodicity: Int = randomInt(),
        title: String = randomString()
    ) = randomTask().copy(
        lastCompletionDate = minutesAgo?.let { getInstant(it) },
        daysPeriodicity = daysPeriodicity,
        title = title
    )

    private fun getInstant(minutesAgo: Int) =
        dateTimeProvider.getCurrentInstant() - minutesAgo.minutes
}
