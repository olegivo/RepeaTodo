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

import ru.olegivo.repeatodo.domain.DateTimeProvider
import ru.olegivo.repeatodo.domain.IsTaskCompletedUseCase
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.extensions.atStartOfDayIn

class TasksSorterByCompletion(
    private val dateTimeProvider: DateTimeProvider,
    private val isTaskCompleted: IsTaskCompletedUseCase
): TasksSorter {
    private val byNotCompletedFirst = compareBy<Task> {
        isTaskCompleted(
            lastCompletionDate = it.lastCompletionDate,
            daysPeriodicity = it.daysPeriodicity
        )
    }
    private val byNeverCompletedFirst = compareByDescending<Task> {
        it.lastCompletionDate == null
    }

    override fun sort(tasks: List<Task>): List<Task> {
        val currentDayStartMs =
            dateTimeProvider.getCurrentStartOfDayInstant().toEpochMilliseconds()
        val byOldestLastCompletion = compareByDescending<Task> { task ->
            task.lastCompletionDate
                ?.atStartOfDayIn(dateTimeProvider.getCurrentTimeZone())
                ?.toEpochMilliseconds()
                ?.let { completionDayStartMs -> currentDayStartMs - completionDayStartMs }
        }
        val byShortestDaysPeriodicity = compareBy<Task> { task ->
            task.daysPeriodicity
        }
        val byTitle = compareBy<Task> { task ->
            task.title
        }

        return tasks.sortedWith(
            byNotCompletedFirst
                .then(byNeverCompletedFirst)
                .then(byOldestLastCompletion)
                .then(byShortestDaysPeriodicity)
                .then(byTitle)
        )
    }
}
