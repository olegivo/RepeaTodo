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

package ru.olegivo.repeatodo.domain

import app.cash.turbine.test
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.assertItem
import ru.olegivo.repeatodo.domain.models.randomToDoList
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.randomString

internal class SaveCustomToDoListUseCaseImplTest: FreeSpec() {
    init {
        "should add new todo list in local data source" - {
            val uuid = randomString()
            val origin = randomToDoList(uuid = uuid)
            val localToDoListsDataSource: LocalToDoListsDataSource = FakeLocalToDoListsDataSource()

            val saveCustomToDoList: SaveCustomToDoListUseCase = SaveCustomToDoListUseCaseImpl(
                localToDoListsDataSource = localToDoListsDataSource
            )

            saveCustomToDoList(toDoList = origin).test {
                expectMostRecentItem() shouldBe WorkState.Completed(Unit)
            }
            val persistedToDoLists = localToDoListsDataSource.getToDoLists().testIn()
            persistedToDoLists.assertItem {
                shouldContain(origin)
            }

            "should update exist todo list" {
                val newVersion = randomToDoList(uuid = uuid)

                saveCustomToDoList(newVersion).test {
                    expectMostRecentItem() shouldBe WorkState.Completed(Unit)
                }

                persistedToDoLists.assertItem {
                    shouldNotContain(origin)
                    shouldContain(newVersion)
                }
            }
        }
    }
}
