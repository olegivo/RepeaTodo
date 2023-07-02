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

package ru.olegivo.repeatodo.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.olegivo.repeatodo.main.presentation.MainViewModel
import ru.olegivo.repeatodo.main.presentation.mainScreenFakes
import ru.olegivo.repeatodo.preview.fakeOrInjectKoin
import ru.olegivo.repeatodo.utils.PreviewEnvironment

private const val MAIN_VIEW_MODEL_REMEMBER_KEY = "MAIN_VIEW_MODEL_REMEMBER_KEY"

@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    previewEnvironment: PreviewEnvironment? = null
) {
    val viewModel: MainViewModel = previewEnvironment.fakeOrInjectKoin(MAIN_VIEW_MODEL_REMEMBER_KEY)
    val isCompletedTasksFilter = viewModel.isShowCompleted.collectAsState()
    val isHighestPriorityTasksFilter = viewModel.isShowOnlyHighestPriority.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(
                    WindowInsetsSides.Start +
                        WindowInsetsSides.End +
                        WindowInsetsSides.Top +
                        WindowInsetsSides.Bottom
                )
            ),
        topBar = {
            AppBar(onMenuClicked = {
                scope.launch {
                    when (drawerState.currentValue) {
                        DrawerValue.Closed -> drawerState.open()
                        DrawerValue.Open -> drawerState.close()
                    }
                }
            })
        },
        bottomBar = {
            AddTaskInlined(
                Modifier
                    .fillMaxWidth(),
                previewEnvironment = previewEnvironment
            )
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .padding(contentPadding)
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        DrawerToDoLists(
                            onCloseDrawerNeeded = {
                                scope.launch { drawerState.close() }
                            },
                            addToDoList = {
                                AddToDoList(
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                    previewEnvironment = previewEnvironment
                                )
                            },
                            previewEnvironment = previewEnvironment
                        )
                    }
                },
                content = {
                    Column {
                        IsAllTasksFilter(
                            Modifier
                                .align(Alignment.End)
                                .padding(top = 16.dp, end = 16.dp),
                            isCompletedTasksFilterState = isCompletedTasksFilter,
                            onChange = { viewModel.isShowCompleted.value = it }
                        )
                        IsHighestPriorityTasksFilter(
                            Modifier
                                .align(Alignment.End)
                                .padding(all = 16.dp),
                            isHighestPriorityTasksFilterState = isHighestPriorityTasksFilter,
                            onChange = { viewModel.isShowOnlyHighestPriority.value = it }
                        )
                        TasksList(
                            Modifier
                                .wrapContentHeight(align = Alignment.Top),
                            previewEnvironment = previewEnvironment
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun AppBar(
    onMenuClicked: () -> Unit
) {
    TopAppBar(
        title = { Text("RepeaTodo") },
        navigationIcon = {
            IconButton(onClick = { onMenuClicked() }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    )
}

@Composable
private fun IsAllTasksFilter(
    modifier: Modifier = Modifier,
    isCompletedTasksFilterState: State<Boolean>,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isCompletedTasksFilterState.value,
            onCheckedChange = onChange
        )
        Text(text = "Show completed")
    }
}

@Composable
private fun IsHighestPriorityTasksFilter(
    modifier: Modifier = Modifier,
    isHighestPriorityTasksFilterState: State<Boolean>,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isHighestPriorityTasksFilterState.value,
            onCheckedChange = onChange
        )
        Text(text = "Only high priority")
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MaterialTheme {
        MainScreen(
            previewEnvironment = PreviewEnvironment { mainScreenFakes() }
        )
    }
}
