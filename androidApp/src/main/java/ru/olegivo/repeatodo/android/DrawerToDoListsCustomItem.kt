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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.core.parameter.parametersOf
import ru.olegivo.repeatodo.domain.models.ToDoList
import ru.olegivo.repeatodo.main.presentation.DrawerToDoListsCustomItemViewModel
import ru.olegivo.repeatodo.main.presentation.drawerToDoListsCustomItemViewModelFakes
import ru.olegivo.repeatodo.preview.fakeOrInjectKoin
import ru.olegivo.repeatodo.utils.PreviewEnvironment
import ru.olegivo.repeatodo.utils.newUuid

@Composable
fun DrawerToDoListsCustomItem(
    modifier: Modifier = Modifier,
    item: ToDoList.Custom,
    onCloseDrawerNeeded: () -> Unit = {},
    previewEnvironment: PreviewEnvironment? = null,
) {
    val itemViewModel: DrawerToDoListsCustomItemViewModel =
        previewEnvironment.fakeOrInjectKoin(rememberKey = item.uuid) {
            parametersOf(item)
        }

    val title = itemViewModel.title.collectAsState()
    val isEditing = itemViewModel.isEditing.collectAsState()
    val showDeleteConfirmation = itemViewModel.showDeleteConfirmation.collectAsState()
    Column {
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.List, contentDescription = null) },
            label = {
                if (isEditing.value) {
                    TextField(
                        value = title.value,
                        onValueChange = { itemViewModel.title.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                } else {
                    Text(title.value)
                }
            },
            selected = false,
            onClick = {
                onCloseDrawerNeeded()
            },
            modifier = modifier,
            badge = {
                Row {
                    if (isEditing.value) {
                        IconButton(
                            enabled = itemViewModel.canSave.collectAsState().value,
                            onClick = { itemViewModel.onSaveClicked() }
                        ) {
                            Icon(Icons.Outlined.Done, null)
                        }
                        IconButton(onClick = { itemViewModel.onCancelEditClicked() }) {
                            Icon(Icons.Outlined.Close, null)
                        }
                    } else {
                        IconButton(onClick = { itemViewModel.onBeginEditClicked() }) {
                            Icon(Icons.Outlined.Edit, contentDescription = null)
                        }
                        IconButton(onClick = { itemViewModel.onDeleteClicked() }) {
                            Icon(Icons.Outlined.Delete, null)
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun CustomItemPreview() {
    MaterialTheme {
        val item = ToDoList.Custom(newUuid(), "Item 1")
        DrawerToDoListsCustomItem(
            item = item,
            previewEnvironment = PreviewEnvironment {
                drawerToDoListsCustomItemViewModelFakes()
            }
        )
    }
}

@Preview
@Composable
private fun CustomItemEditingPreview() {
    MaterialTheme {
        val item = ToDoList.Custom(newUuid(), "Item 1")
        DrawerToDoListsCustomItem(
            item = item,
            previewEnvironment = PreviewEnvironment {
                drawerToDoListsCustomItemViewModelFakes(isEditing = true)
            }
        )
    }
}

@Preview
@Composable
private fun CustomItemConfirmDeletePreview() {
    MaterialTheme {
        val item = ToDoList.Custom(newUuid(), "Item 1")
        DrawerToDoListsCustomItem(
            item = item,
            previewEnvironment = PreviewEnvironment {
                drawerToDoListsCustomItemViewModelFakes(showDeleteConfirmation = true)
            }
        )
    }
}

@Preview
@Composable
private fun CustomItemDeletingPreview() {
    MaterialTheme {
        val item = ToDoList.Custom(newUuid(), "Item 1")
        DrawerToDoListsCustomItem(
            item = item,
            previewEnvironment = PreviewEnvironment {
                drawerToDoListsCustomItemViewModelFakes(isDeleting = true)
            }
        )
    }
}
