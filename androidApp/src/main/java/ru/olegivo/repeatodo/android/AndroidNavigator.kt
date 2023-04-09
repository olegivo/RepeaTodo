/*
 * Copyright (C) 2023 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.repeatodo.android

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.olegivo.repeatodo.main.navigation.MainNavigator
import ru.olegivo.repeatodo.main.navigation.NavigationDestination

class AndroidNavigator(mainNavigator: MainNavigator) : CoroutineScope {
    private lateinit var navController: NavHostController

    override val coroutineContext = SupervisorJob() + Dispatchers.Main

    init {
        launch {
            mainNavigator.navigationBack.collect {
                navController.popBackStack()
            }
        }
        launch {
            mainNavigator.navigationDestination.filterNotNull().collect { destination ->
                when (destination) {
                    NavigationDestination.AddTask -> {
                        TODO("The route AddTask is not allowed cause inline adding used yet")
                    }
                    is NavigationDestination.EditTask -> {
                        val taskRoute = NavRoutes.EditTaskRoutes.getNavRoute {
                            EditTaskRoute(destination.uuid)
                        }
                        navController.navigate(route = taskRoute.getDestinationRoute())
                    }
                }
            }
        }
    }

    fun attachNavigation(navController: NavHostController) {
        this.navController = navController
    }
}

sealed class NavRoutes<TGraph : NavGraph, TRoute : NavRoute>(
    taskGraphFactory: () -> TGraph
) {
    val navGraph: NavGraph = taskGraphFactory()
    fun getNavRoute(builder: () -> TRoute): NavRoute = builder()

    companion object : NavGraph {
        val startRoute = HomeRoutes.getNavRoute { HomeRoute() }

        override fun addTo(navGraphBuilder: NavGraphBuilder) {
            listOf(HomeRoutes, EditTaskRoutes).forEach {
                it.navGraph.addTo(navGraphBuilder)
            }
        }
    }

    object HomeRoutes : NavRoutes<HomeGraph, HomeRoute>({ HomeGraph() })
    object EditTaskRoutes : NavRoutes<EditTaskGraph, EditTaskRoute>({ EditTaskGraph() })
}

interface NavGraph {
    fun addTo(navGraphBuilder: NavGraphBuilder)
}

class HomeGraph : NavGraph {
    override fun addTo(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(route = Routes.Home.route) {
            MainScreen()
        }
    }
}

class EditTaskGraph : NavGraph {
    override fun addTo(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(route = Routes.Tasks.Edit.route,
            arguments = listOf(
                navArgument(Routes.Tasks.Edit.Args.Uuid.name) {
                    type = NavType.StringType
                }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = Routes.Tasks.Edit.route })
        ) {
            val uuid = it.arguments?.getString(Routes.Tasks.Edit.Args.Uuid.name)
            EditTask(uuid = uuid)
        }
    }
}

interface NavRoute {
    fun getDestinationRoute(): String
}

class HomeRoute : NavRoute {
    override fun getDestinationRoute() = Routes.Home.route
}

class EditTaskRoute(private val uuid: String) : NavRoute {
    override fun getDestinationRoute() = Routes.Tasks.Edit(uuid).route
}
