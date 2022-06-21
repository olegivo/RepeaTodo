package ru.olegivo.repeatodo

import app.cash.turbine.test
import kotlinx.coroutines.flow.Flow

suspend fun <T> Flow<T>.assertItem(block: T.() -> Unit) {
    test {
        awaitItem().apply(block)
    }
}

suspend fun <T> Flow<T>.assertNoEvents() {
    test {
        expectNoEvents()
    }
}
