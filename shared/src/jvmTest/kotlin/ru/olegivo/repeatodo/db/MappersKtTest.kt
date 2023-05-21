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

import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.domain.Priority
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.randomInt
import ru.olegivo.repeatodo.randomString
import ru.olegivo.repeatodo.utils.newUuid
import ru.olegivo.repeatodo.domain.models.Task as TaskDomain

class MappersKtTest: FreeSpec() {
    init {
        "map" - {
            (Priority.values().toList() + null).forEach { priority ->
                "priority = $priority" {
                    val task = TaskDomain(
                        uuid = newUuid(),
                        title = randomString(),
                        daysPeriodicity = randomInt(),
                        priority = priority
                    )
                    val expected = Task(
                        uuid = task.uuid,
                        title = task.title,
                        daysPeriodicity = task.daysPeriodicity,
                        priority = task.priority
                    )

                    task.toDb() shouldBe expected
                }
            }
        }
    }
}
