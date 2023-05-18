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

package ru.olegivo.repeatodo.kotest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import ru.olegivo.repeatodo.DispatchersProvider
import kotlin.time.Duration
import app.cash.turbine.testIn as turbineTestIn

abstract class FreeSpec(
    lifecycleMode: LifecycleMode = LifecycleMode.Root,
    body: FreeSpec.() -> Unit = {}
): FreeSpec(body) {

    private val coroutineListener = CoroutineTestListener(
        lifecycleMode = lifecycleMode,
//        beforeCleanupTestCoroutines = ::beforeCleanupTestCoroutines
    )

    protected val dispatchersProvider = object: DispatchersProvider {
        override val io get() = coroutineListener.dispatcher
        override val default get() = coroutineListener.dispatcher
        override val main get() = coroutineListener.dispatcher
    }

    protected val testCoroutineScope = coroutineListener.scope

    override fun listeners() =
        super.listeners() + listOf(coroutineListener)

    override fun isolationMode() = IsolationMode.InstancePerLeaf

    protected fun advanceTimeBy(delayTimeMillis: Long) {
        coroutineListener.scope.advanceTimeBy(delayTimeMillis)
        coroutineListener.scope.runCurrent()
    }

    protected fun runCurrent() {
        coroutineListener.scope.runCurrent()
    }

    protected fun advanceUntilIdle() {
        coroutineListener.scope.advanceUntilIdle()
    }

    protected fun <T> Flow<T>.testIn(
        scope: CoroutineScope = testCoroutineScope,
        timeout: Duration? = null,
        name: String? = null
    ) =
        turbineTestIn(
            scope = scope,
            timeout = timeout,
            name = name
        )
}

enum class LifecycleMode {
    Root,
    Test
}
