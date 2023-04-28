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
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

class IsTaskCompletedUseCaseImplTest: FreeSpec() {

    init {
        "instance" - {
            val dateTimeProvider = FakeDateTimeProvider()
            val useCase: IsTaskCompletedUseCase = IsTaskCompletedUseCaseImpl(
                dateTimeProvider = dateTimeProvider
            )
            with(dateTimeProvider.getCurrentTimeZone()) {
                val current = dateTimeProvider.getCurrentInstant()
                val daysPeriodicity = 1
                val daysPeriod = daysPeriodicity.days

                "should return false WHEN has no completion date" {
                    useCase.invoke(lastCompletionDate = null, daysPeriodicity = daysPeriodicity)
                        .shouldBeFalse()
                }

                val passedADaysBefore = current - daysPeriod
                val passedADaysWithMomentBefore = passedADaysBefore - 1.milliseconds
                val passedADaysWithLessThanMomentBefore = passedADaysBefore + 1.milliseconds

                "should return false WHEN completion date passed a days period and a moment before" {
                    useCase
                        .invoke(
                            lastCompletionDate = passedADaysWithMomentBefore.toLocalDateTime(),
                            daysPeriodicity = daysPeriodicity
                        )
                        .shouldBeFalse()
                }

                "should return false WHEN completion date passed a days period before" {
                    useCase
                        .invoke(
                            lastCompletionDate = passedADaysBefore.toLocalDateTime(),
                            daysPeriodicity = daysPeriodicity
                        )
                        .shouldBeFalse()
                }

                "should return false WHEN completion date passed a days period and less than a moment before" {
                    useCase
                        .invoke(
                            lastCompletionDate = passedADaysWithLessThanMomentBefore.toLocalDateTime(),
                            daysPeriodicity = daysPeriodicity
                        )
                        .shouldBeTrue()
                }
            }
        }
    }
}
