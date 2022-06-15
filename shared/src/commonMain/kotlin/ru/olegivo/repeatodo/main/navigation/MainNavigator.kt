package ru.olegivo.repeatodo.main.navigation

import kotlinx.coroutines.flow.SharedFlow

interface MainNavigator {
    val navigationDestination: SharedFlow<NavigationDestination?>

    fun addTask()
}

enum class NavigationDestination {
    AddTask
}
