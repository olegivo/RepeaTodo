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

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeMainNavigator : MainNavigator {

    override val navigationDestination = MutableSharedFlow<NavigationDestination?>().asSharedFlow()
    override val navigationBack = MutableSharedFlow<Unit>().asSharedFlow()

    var invocations: Invocations = Invocations.None
        private set

    override fun back() {
        invocations = Invocations.Back
    }

    override fun addTask() {
        invocations = Invocations.To(NavigationDestination.AddTask)
    }

    override fun editTask(uuid: String) {
        invocations = Invocations.To(NavigationDestination.EditTask(uuid))
    }

    sealed interface Invocations {
        object None : Invocations
        data class To(val destination: NavigationDestination?) : Invocations
        object Back : Invocations
    }
}
