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

package ru.olegivo.repeatodo.domain.models

import kotlinx.datetime.Instant
import ru.olegivo.repeatodo.domain.Priority

data class Task(
    val uuid: String,
    val title: String,
    val daysPeriodicity: Int,
    val priority: Priority?,
    val toDoListUuid: String,
    val lastCompletionDate: Instant?
) {
    companion object {
        const val DEFAULT_DAYS_PERIODICITY: Int = 1
        const val MIN_DAYS_PERIODICITY: Int = 1
        const val MAX_DAYS_PERIODICITY: Int = 1000
    }
}
