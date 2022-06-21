package ru.olegivo.repeatodo.main.navigation

import kotlinx.coroutines.flow.MutableSharedFlow

class FakeMainNavigator : MainNavigator {

    override val navigationDestination = MutableSharedFlow<NavigationDestination?>()

    override fun addTask() {
    }
}
