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

package ru.olegivo.repeatodo.domain

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.randomInt
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class IsTaskCompletedUseCaseImplTest: FreeSpec() {

    init {
        "instance" - {
            val dateTimeProvider = FakeDateTimeProvider()
            val useCase: IsTaskCompletedUseCase = IsTaskCompletedUseCaseImpl(
                dateTimeProvider = dateTimeProvider
            )

            Now.values().forEach { now ->
                "now is $now" - {
                    dateTimeProvider.instant = now.getCurrentInstant(dateTimeProvider)

                    (1..3).forEach { daysPeriodicity ->
                        "periodicity = $daysPeriodicity" - {

                            "should return false WHEN has no completion date" {
                                useCase.invoke(
                                    lastCompletionDate = null,
                                    daysPeriodicity = daysPeriodicity
                                )
                                    .shouldBeFalse()
                            }

                            Completion.values().forEach { completion ->
                                "completion is in $completion" - {
                                    "should return false WHEN completed ${daysPeriodicity + 1} days ago" {
                                        val lastCompletionDate = completion.getCompletionInstant(
                                            dateTimeProvider = dateTimeProvider,
                                            daysAgo = daysPeriodicity + 1
                                        )

                                        useCase.invoke(
                                            lastCompletionDate = lastCompletionDate,
                                            daysPeriodicity = daysPeriodicity
                                        )
                                            .shouldBeFalse()

                                    }

                                    "should return false WHEN completed $daysPeriodicity days ago" {
                                        val lastCompletionDate = completion.getCompletionInstant(
                                            dateTimeProvider = dateTimeProvider,
                                            daysAgo = daysPeriodicity
                                        )

                                        useCase.invoke(
                                            lastCompletionDate = lastCompletionDate,
                                            daysPeriodicity = daysPeriodicity
                                        )
                                            .shouldBeFalse()
                                    }

                                    val daysAgo = daysPeriodicity - 1
                                    val completedAgo =
                                        if (daysAgo == 0) "today" else "$daysAgo days ago"
                                    "should return true WHEN completed $completedAgo" {
                                        useCase.invoke(
                                            lastCompletionDate = completion.getCompletionInstant(
                                                dateTimeProvider = dateTimeProvider,
                                                daysAgo = daysAgo
                                            ),
                                            daysPeriodicity = daysPeriodicity
                                        )
                                            .shouldBeTrue()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    enum class Now {
        Midnight,
        Afternoon;

        fun getCurrentInstant(dateTimeProvider: DateTimeProvider) =
            dateTimeProvider.getCurrentStartOfDayInstant().let {
                val todayMidnight = dateTimeProvider.getCurrentStartOfDayInstant()
                when (this) {
                    Midnight -> todayMidnight
                    Afternoon -> todayMidnight + 12.hours
                }
            }
    }

    enum class Completion {
        Midnight,
        BeforeAfternoon,
        Afternoon,
        AfterAfternoon;

        fun getCompletionInstant(dateTimeProvider: DateTimeProvider, daysAgo: Int) =
            dateTimeProvider.getCurrentStartOfDayInstant().let {
                val todayMidnight = dateTimeProvider.getCurrentStartOfDayInstant()
                val todayAfternoon = todayMidnight + 12.hours

                when (this) {
                    Midnight -> {
                        todayMidnight
                    }
                    BeforeAfternoon -> {
                        todayMidnight + randomInt(
                            from = 1,
                            until = 11 * 60
                        ).minutes
                    }
                    Afternoon -> {
                        todayAfternoon
                    }
                    AfterAfternoon -> {
                        todayAfternoon + randomInt(
                            from = 1,
                            until = 11 * 60
                        ).minutes
                    }
                } - daysAgo.days
            }
    }
}
