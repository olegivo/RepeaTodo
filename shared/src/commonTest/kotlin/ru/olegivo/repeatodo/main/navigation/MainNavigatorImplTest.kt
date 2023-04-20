/*
 * Copyright (C) 2022 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.repeatodo.main.navigation

import app.cash.turbine.test
import app.cash.turbine.testIn
import io.kotest.core.spec.IsolationMode
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.kotest.LifecycleMode
import ru.olegivo.repeatodo.randomString
import kotlin.time.Duration.Companion.milliseconds

internal class MainNavigatorImplTest : FreeSpec(LifecycleMode.Root) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf

    init {
        "instance" - {
            val navigator: MainNavigator = MainNavigatorImpl()
            val navigationBack = navigator.navigationBack.testIn(
                testCoroutineScope,
                timeout = 10.milliseconds,
                name = "navigationBack"
            )
            val navigationDestination = navigator.navigationDestination.testIn(
                testCoroutineScope,
                timeout = 10.milliseconds,
                name = "navigationDestination"
            )

            "initial state" {
                navigationBack.expectNoEvents()
                navigationDestination.expectNoEvents()
            }

            "back" - {
                navigator.back()

                "should emit back navigation" {
                    navigationBack.awaitItem()
                }
                "should not emit navigation destination" {
                    navigationDestination.expectNoEvents()
                }
            }

            "addTask" - {
                navigator.addTask()

                "should emit AddTask destination" {
                    navigationDestination.awaitItem()
                        .shouldNotBeNull() shouldBe NavigationDestination.AddTask
                }

                "should not emit back navigation" {
                    navigationBack.expectNoEvents()
                }
            }

            "editTask" - {
                val uuid = randomString()

                navigator.editTask(uuid = uuid)

                "should emit EditTask destination" {
                    navigationDestination.awaitItem()
                        .shouldNotBeNull() shouldBe NavigationDestination.EditTask(uuid = uuid)
                }

                "should not emit back navigation" {
                    navigationBack.expectNoEvents()
                }
            }
        }
    }
}
