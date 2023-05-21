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
import ru.olegivo.repeatodo.randomEnum
import ru.olegivo.repeatodo.randomInstant
import ru.olegivo.repeatodo.randomInt
import ru.olegivo.repeatodo.randomNull
import ru.olegivo.repeatodo.randomString

fun randomTask(
    priority: Priority? = randomEnum<Priority>().randomNull(),
    toDoListUuid: String = ToDoList.Predefined.Kind.INBOX.uuid,
    lastCompletionDate: Instant? = randomInstant().randomNull()
) = Task(
    uuid = randomString(),
    title = randomString(),
    daysPeriodicity = randomInt(),
    lastCompletionDate = lastCompletionDate,
    priority = priority,
    toDoListUuid = toDoListUuid,
)
