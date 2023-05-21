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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.core.parameter.parametersOf
import ru.olegivo.repeatodo.domain.WorkState
import ru.olegivo.repeatodo.edit.presentation.EditTaskViewModel
import ru.olegivo.repeatodo.edit.presentation.editTaskViewModelWithFakes
import ru.olegivo.repeatodo.preview.fakeOrInjectKoin
import ru.olegivo.repeatodo.utils.PreviewEnvironment

@Composable
internal fun EditTask(
    modifier: Modifier = Modifier,
    uuid: String? = null,
    previewEnvironment: PreviewEnvironment? = null,
) {
    val viewModel: EditTaskViewModel = previewEnvironment.fakeOrInjectKoin { parametersOf(uuid) }
    val showAlertDialog = remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = { AppBar(viewModel, showAlertDialog) }
    ) { contentPadding ->
        Card(
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(20.dp)
        ) {
            val isLoading = viewModel.isLoading.collectAsState(true)
            if (isLoading.value) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        Modifier
                    )
                }
            } else {
                val title = viewModel.title.collectAsState()
                Column {
                    ConfirmDeleteTaskDialog(
                        taskTitle = title,
                        showDialog = showAlertDialog,
                        onConfirm = { viewModel.onDeleteClicked() }
                    )
                    TitleEditor(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        title = title,
                        viewModel = viewModel
                    )
                    Text(
                        "Todo-list:",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .padding(horizontal = 16.dp),
                    )
                    ToDoListEditor(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .padding(horizontal = 16.dp),
                        viewModel = viewModel
                    )
                    DaysPeriodicityEditor(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        viewModel = viewModel
                    )
                    PriorityEditor(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        viewModel = viewModel
                    )
                    Spacer(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .weight(1f)
                    )
                    SaveTaskButton(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .imePadding(),
                        onClick = { viewModel.onSaveClicked() },
                        canSave = viewModel.canSave.collectAsState(false),
                        isSaving = viewModel.isSaving.collectAsState(false),
                    )
                }
            }
        }
    }
}

@Composable
private fun TitleEditor(
    modifier: Modifier = Modifier,
    title: State<String>,
    viewModel: EditTaskViewModel
) {
    TextField(
        value = title.value,
        onValueChange = { viewModel.title.value = it },
        modifier = modifier
            .focusRequester(oneTimeFocusRequester()),
        placeholder = { Text("Enter a title here") },
        label = { Text("Title") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onDone = { viewModel.onSaveClicked() })
    )
}

@Composable
fun ToDoListEditor(modifier: Modifier = Modifier, viewModel: EditTaskViewModel) {
    val initialValue = viewModel.toDoList.collectAsState()
    DropDownSelector(
        modifier = modifier,
        items = viewModel.toDoListItems.collectAsState(),
        initialValue = initialValue,
        textSelector = { title },
        canClear = false,
        onSelected = { item -> item?.let { viewModel.toDoList.value = it } }
    )
}

@Composable
private fun DaysPeriodicityEditor(
    modifier: Modifier = Modifier,
    viewModel: EditTaskViewModel
) {
    val daysPeriodicity = viewModel.daysPeriodicity.collectAsState()
    TextField(
        value = daysPeriodicity.value,
        onValueChange = { viewModel.daysPeriodicity.value = it },
        modifier = modifier,
        placeholder = { Text("Enter a days periodicity here") },
        label = { Text("Days periodicity") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { viewModel.onSaveClicked() })
    )
}

@Composable
private fun PriorityEditor(
    modifier: Modifier = Modifier,
    viewModel: EditTaskViewModel
) {
    val priority = viewModel.priority.collectAsState()
    val priorityItems = remember { mutableStateOf(viewModel.priorityItems) }
    DropDownSelector(
        modifier = modifier,
        items = priorityItems,
        initialValue = priorityItems.value.singleOrNull { it.priority == priority.value },
        textSelector = { title },
        canClear = true
    ) { viewModel.priority.value = it?.priority }
}

@Composable
private fun AppBar(
    viewModel: EditTaskViewModel,
    showAlertDialog: MutableState<Boolean>
) {
    TopAppBar(
        title = { Text("Edit Task") },
        navigationIcon = {
            IconButton(onClick = { viewModel.onCancelClicked() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { showAlertDialog.value = true }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete task")
            }
        }
    )
}

@Composable
private fun ConfirmDeleteTaskDialog(
    taskTitle: State<String>,
    showDialog: MutableState<Boolean>,
    onConfirm: () -> Unit
) {
    if (showDialog.value) {
        ConfirmDialog(
            title = "Do you want to delete the '${taskTitle.value}'?",
            text = "",
            onDismiss = { showDialog.value = false },
            onConfirm = {
                showDialog.value = false
                onConfirm()
            }
        )
    }
}

@Preview
@Composable
private fun ConfirmDeleteTaskDialogPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary
        ) {
            val showDialog = remember { mutableStateOf(true) }
            val taskTitle = remember { mutableStateOf("Task 1") }
            ConfirmDeleteTaskDialog(
                taskTitle = taskTitle,
                showDialog = showDialog,
                onConfirm = {}
            )
        }
    }
}

@Composable
private fun oneTimeFocusRequester(): FocusRequester {
    val requester = FocusRequester()
    val isFocused = remember { mutableStateOf(false) }
    SideEffect {
        if (!isFocused.value) {
            requester.requestFocus()
            isFocused.value = true
        }
    }
    return requester
}

@Composable
private fun SaveTaskButton(
    modifier: Modifier = Modifier,
    canSave: State<Boolean>,
    isSaving: State<Boolean>,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier,
        enabled = canSave.value
    ) {
        if (isSaving.value) {
            CircularProgressIndicator()
        } else {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = "Save",
                modifier = Modifier
            )
        }
    }
}


@Preview
@Composable
private fun EditTaskPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary
        ) {
            EditTask(
                previewEnvironment = PreviewEnvironment { editTaskViewModelWithFakes() }
            )
        }
    }
}

@Preview
@Composable
private fun EditTaskPreviewLoading() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary
        ) {
            EditTask(
                previewEnvironment = PreviewEnvironment {
                    editTaskViewModelWithFakes(
                        WorkState.InProgress()
                    )
                }
            )
        }
    }
}

@Composable
fun ConfirmDialog(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmButtonText: String = "Yes",
    dismissButtonText: String = "No"
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = text,
                modifier = Modifier.padding(4.dp)
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm.invoke() }) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss.invoke() }) {
                Text(text = dismissButtonText)
            }
        }
    )
}

@Preview
@Composable
private fun PopupDialogPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary
        ) {
            ConfirmDialog(
                title = "Update title here",
                text = "Update message here",
                onConfirm = {},
                onDismiss = {},
            )
        }
    }
}
