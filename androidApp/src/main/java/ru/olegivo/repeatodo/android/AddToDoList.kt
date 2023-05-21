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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
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
import ru.olegivo.repeatodo.main.presentation.AddToDoListViewModel
import ru.olegivo.repeatodo.main.presentation.addToDoListViewModelFakes
import ru.olegivo.repeatodo.preview.fakeOrInjectKoin
import ru.olegivo.repeatodo.utils.PreviewEnvironment

@Composable
fun AddToDoList(
    modifier: Modifier = Modifier,
    previewEnvironment: PreviewEnvironment? = null
) {
    val viewModel: AddToDoListViewModel = previewEnvironment.fakeOrInjectKoin()

    val isAddingState = viewModel.isEditingNew.collectAsState()
    if (isAddingState.value) {
        val title = viewModel.title.collectAsState()
        TextField(
            value = title.value,
            onValueChange = { viewModel.title.value = it },
            trailingIcon = {
                Row {
                    IconButton(
                        enabled = viewModel.canSaveNew.collectAsState().value,
                        onClick = { viewModel.onSaveClicked() }
                    ) {
                        Icon(Icons.Outlined.Done, null)
                    }
                    IconButton(
                        onClick = { viewModel.cancelAddNew() }
                    ) {
                        Icon(Icons.Outlined.Close, null)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    } else {
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
            label = { Text("Add Todo-list") },
            selected = false,
            onClick = {
                viewModel.beginAddingNew()
            },
            modifier = modifier
        )
    }
}

@Preview
@Composable
private fun AddToDoListPreview() {
    MaterialTheme {
        AddToDoList(
            previewEnvironment = PreviewEnvironment {
                addToDoListViewModelFakes(isEditingNew = false)
            }
        )
    }
}

@Preview
@Composable
private fun AddToDoListEditingPreview() {
    MaterialTheme {
        AddToDoList(
            previewEnvironment = PreviewEnvironment {
                addToDoListViewModelFakes(isEditingNew = true)
            }
        )
    }
}
