package ru.olegivo.repeatodo.main.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class MainNavigatorImpl : MainNavigator {
    override val navigationDestination = MutableSharedFlow<NavigationDestination?>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun addTask() {
        navigationDestination.tryEmit(NavigationDestination.AddTask)
    }
}
