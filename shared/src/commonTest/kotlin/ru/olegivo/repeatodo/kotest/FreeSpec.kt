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

import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent

abstract class FreeSpec(private val lifecycleMode: LifecycleMode = LifecycleMode.Test,body: FreeSpec.() -> Unit = {}) : FreeSpec(body) {

    private val coroutineListener = CoroutineTestListener(
        lifecycleMode = lifecycleMode,
//        beforeCleanupTestCoroutines = ::beforeCleanupTestCoroutines
    )

    override fun listeners() =
        super.listeners() + listOf(coroutineListener)

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
}

enum class LifecycleMode {
    Root,
    Test
}
