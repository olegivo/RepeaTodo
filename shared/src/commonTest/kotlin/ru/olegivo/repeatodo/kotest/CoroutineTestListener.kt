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

package ru.olegivo.repeatodo.kotest

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*

class CoroutineTestListener(
    private val lifecycleMode: LifecycleMode = LifecycleMode.Root,
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
    val scope: TestScope = TestScope(dispatcher),
) : TestListener {

    private fun shouldRun(testCase: TestCase) =
        (lifecycleMode == LifecycleMode.Root && testCase.descriptor.isRootTest()) ||
            (lifecycleMode == LifecycleMode.Test && testCase.type == TestType.Test)

    override suspend fun beforeAny(testCase: TestCase) {
        if (shouldRun(testCase)) {
            Dispatchers.setMain(dispatcher)
        }
    }

    override suspend fun afterAny(testCase: TestCase, result: TestResult) {
        if (shouldRun(testCase)) {
//            beforeCleanupTestCoroutines()
            Dispatchers.resetMain()
        }
    }
}
