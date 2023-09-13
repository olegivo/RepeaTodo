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

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ru.olegivo.repeatodo.DispatchersProvider
import ru.olegivo.repeatodo.domain.LocalToDoListsDataSource
import ru.olegivo.repeatodo.domain.models.ToDoList

class LocalToDoListsDataSourceImpl(
    private val db: RepeaTodoDb,
    private val dispatchersProvider: DispatchersProvider,
): LocalToDoListsDataSource {
    override fun getToDoLists(): Flow<List<ToDoList>> {
        return db.toDoListQueries
            .getToDoLists(::toDomain)
            .asFlow()
            .flowOn(dispatchersProvider.io)
            .mapToList(dispatchersProvider.default)
    }

    override suspend fun save(toDoList: ToDoList.Custom) {
        withContext(dispatchersProvider.io) {
            db.transaction {
                if (db.toDoListQueries.isToDoListExists(toDoList.uuid).executeAsOne()) {
                    with(toDoList) {
                        db.toDoListQueries.updateToDoList(
                            title = title,
                            uuid = uuid
                        )
                    }
                } else {
                    db.toDoListQueries.addToDoList(toDoList.toDb())
                }
            }
        }
    }

    override suspend fun delete(uuid: String) {
        withContext(dispatchersProvider.io) {
            db.transaction {
                db.toDoListQueries.moveTasksToInbox(uuid)
                db.toDoListQueries.deleteToDoList(uuid)
            }
        }
    }

    private fun toDomain(uuid: String, title: String, isPredefined: Boolean): ToDoList =
        if (isPredefined) {
            ToDoList.Predefined(
                uuid = uuid,
                title = title
            )
        } else {
            ToDoList.Custom(
                uuid = uuid,
                title = title
            )
        }
}
