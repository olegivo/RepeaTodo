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
