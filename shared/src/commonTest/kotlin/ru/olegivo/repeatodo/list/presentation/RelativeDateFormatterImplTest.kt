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

import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.domain.FakeDateTimeProvider
import ru.olegivo.repeatodo.kotest.FreeSpec
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class RelativeDateFormatterImplTest: FreeSpec() {
    init {
        "instance" - {
            val dateTimeProvider = FakeDateTimeProvider()
            val relativeDateFormatter: RelativeDateFormatter =
                RelativeDateFormatterImpl(dateTimeProvider)
            val currentInstant = dateTimeProvider.getCurrentInstant()

            listOf(
                30.seconds to "in several seconds",
                1.seconds to "in several seconds",
                0.milliseconds to "several seconds ago",
                (-1).seconds to "several seconds ago",
                (-30).seconds to "several seconds ago",
                (-59).seconds to "several seconds ago",
                (-1).minutes to "1m ago",
                (-1).minutes - 10.seconds to "1m ago",
                (-10).minutes to "10m ago",
                (-1).hours to "1h ago",
                (-1).hours - 10.minutes - 10.seconds to "1h ago",
                (-10).hours to "10h ago",
                (-1).days to "1d ago",
                (-1).days - 1.hours - 10.minutes - 10.seconds to "1d ago",
                (-10).days to "10d ago",
                (-90).days to "90d ago",
            ).forEach { (extraPeriod, expected) ->
                val value = currentInstant + extraPeriod
                "should return `$expected` WHEN $extraPeriod from current datetime" {
                    relativeDateFormatter.format(value) shouldBe expected
                }
            }
        }
    }
}
