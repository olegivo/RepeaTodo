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

import kotlinx.datetime.Instant
import kotlinx.datetime.daysUntil
import kotlinx.datetime.periodUntil
import ru.olegivo.repeatodo.domain.DateTimeProvider

class RelativeDateFormatterImpl(private val dateTimeProvider: DateTimeProvider):
    RelativeDateFormatter {
    override fun format(value: Instant): String {
        val current = dateTimeProvider.getCurrentInstant()
        val currentTimeZone = dateTimeProvider.getCurrentTimeZone()
        val isPast = value <= current
        val periodUntil =
            if (isPast) {
                value.periodUntil(current, currentTimeZone)
            } else {
                current.periodUntil(value, currentTimeZone)
            }

        return when {
            periodUntil.months > 0 -> "${value.daysUntil(current, currentTimeZone)}d"
            periodUntil.days > 0 -> "${periodUntil.days}d"
            periodUntil.hours > 0 -> "${periodUntil.hours}h"
            periodUntil.minutes > 0 -> "${periodUntil.minutes}m"
            periodUntil.minutes < 1 -> "several seconds"
            else -> TODO()
        }.let {
            if (isPast) {
                "$it ago"
            } else {
                "in $it"
            }
        }
    }
}
