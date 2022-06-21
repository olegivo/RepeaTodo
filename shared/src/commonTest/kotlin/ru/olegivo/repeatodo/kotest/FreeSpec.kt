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
