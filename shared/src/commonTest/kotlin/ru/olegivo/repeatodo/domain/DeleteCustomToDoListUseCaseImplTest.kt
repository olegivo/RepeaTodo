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

class DeleteCustomToDoListUseCaseImplTest: FreeSpec() {
    init {
        "should delete only exist task from localTasksDataSource" {
            val localToDoListsDataSource: LocalToDoListsDataSource = FakeLocalToDoListsDataSource()
            val deleteCustomToDoListUseCase: DeleteCustomToDoListUseCase =
                DeleteCustomToDoListUseCaseImpl(localToDoListsDataSource)
            val deleting = randomToDoList()
            localToDoListsDataSource.save(deleting)
            val task2 = randomToDoList()
            localToDoListsDataSource.save(task2)

            deleteCustomToDoListUseCase(deleting).test {
                expectMostRecentItem() shouldBe WorkState.Completed(Unit)
            }

            localToDoListsDataSource.getToDoLists().assertItem {
                shouldNotContain(deleting)
                shouldContain(task2)
            }
        }
    }
}
