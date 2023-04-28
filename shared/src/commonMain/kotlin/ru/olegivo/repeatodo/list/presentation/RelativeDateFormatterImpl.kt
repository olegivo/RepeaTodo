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

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.periodUntil
import ru.olegivo.repeatodo.domain.DateTimeProvider

class RelativeDateFormatterImpl(private val dateTimeProvider: DateTimeProvider):
    RelativeDateFormatter {
    override fun format(value: LocalDateTime): String {
        val current = dateTimeProvider.getCurrentInstant()
        val currentTimeZone = dateTimeProvider.getCurrentTimeZone()
        val instant = with(currentTimeZone) {
            value.toInstant()
        }
        val isPast = instant <= current
        val periodUntil =
            if (isPast) {
                instant.periodUntil(current, currentTimeZone)
            } else {
                current.periodUntil(instant, currentTimeZone)
            }

        return when {
            periodUntil.days >= 1 -> "${periodUntil.days}d"
            periodUntil.hours >= 1 -> "${periodUntil.hours}h"
            periodUntil.minutes >= 1 -> "${periodUntil.minutes}m"
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
