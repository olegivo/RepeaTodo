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

package ru.olegivo.repeatodo.db

import ru.olegivo.repeatodo.db.Task as TaskDb
import ru.olegivo.repeatodo.db.ToDoList as ToDoListDb
import ru.olegivo.repeatodo.domain.models.Task as TaskDomain
import ru.olegivo.repeatodo.domain.models.ToDoList as ToDoListDomain

internal fun TaskDomain.toDb() =
    TaskDb(
        uuid = uuid,
        title = title,
        daysPeriodicity = daysPeriodicity,
        priority = priority,
        toDoListUuid = toDoListUuid
    )


internal fun ToDoListDomain.Custom.toDb() =
    ToDoListDb(
        uuid = uuid,
        title = title,
        isPredefined = false
    )
