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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.main.presentation.DrawerToDoListsViewModel
import ru.olegivo.repeatodo.main.presentation.drawerToDoListsViewModelFakes
import ru.olegivo.repeatodo.preview.fakeOrInjectKoin
import ru.olegivo.repeatodo.utils.PreviewEnvironment

@Composable
fun DrawerToDoLists(
    onCloseDrawerNeeded: () -> Unit = {},
    addToDoList: @Composable () -> Unit = {},
    previewEnvironment: PreviewEnvironment? = null,
) {
    val viewModel: DrawerToDoListsViewModel = previewEnvironment.fakeOrInjectKoin()

    val toDoLists = viewModel.toDoLists.collectAsState(initial = emptyList())
    Surface(modifier = Modifier.fillMaxWidth()) {
        LazyColumn {
            item {
                Spacer(Modifier.height(12.dp))
            }
            item {
                NavigationDrawerItem(
                    label = {
                        Text("TODO lists")
                    },
                    selected = false,
                    onClick = {},
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
            items(toDoLists.value) { item ->
                when (item) {
                    is ToDoList.Predefined -> {
                        PredefinedItem(
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            item = item,
                            onCloseDrawerNeeded = onCloseDrawerNeeded
                        )
                    }
                    is ToDoList.Custom -> {
                        DrawerToDoListsCustomItem(
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            item = item,
                            onCloseDrawerNeeded = onCloseDrawerNeeded,
                            previewEnvironment = previewEnvironment
                        )
                    }
                }
            }
            item {
                Divider()
            }
            item {
                addToDoList()
            }
            item {
                Divider()
            }
        }
    }
}

@Composable
private fun PredefinedItem(
    modifier: Modifier = Modifier,
    item: ToDoList.Predefined,
    onCloseDrawerNeeded: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(Icons.Outlined.List, contentDescription = null) },
        label = { Text(item.title) },
        selected = false,
        onClick = {
            onCloseDrawerNeeded()
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun DrawerToDoListsPreview() {
    MaterialTheme {
        DrawerToDoLists(
            previewEnvironment = PreviewEnvironment {
                drawerToDoListsViewModelFakes(
                    listOf(
                        ToDoList.Custom("1", "Custom 1"),
                        ToDoList.Custom("2", "Custom 2"),
                    )
                )
            }
        )
    }
}
