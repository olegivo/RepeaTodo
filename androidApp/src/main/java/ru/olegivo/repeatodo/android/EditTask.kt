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
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import ru.olegivo.repeatodo.edit.presentation.EditTaskViewModel
import ru.olegivo.repeatodo.edit.presentation.FakeEditTaskViewModel

@Composable
internal fun EditTask(
    modifier: Modifier = Modifier,
    uuid: String? = null,
    viewModel: EditTaskViewModel = koinInject { parametersOf(uuid) }
) {
    Card(
        modifier = modifier
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
                TextField(
                    value = title.value,
                    onValueChange = { viewModel.title.value = it },
                    modifier = Modifier
                        .padding(16.dp)
                        .focusRequester(oneTimeFocusRequester()),
                    placeholder = { Text("Enter A Title Here") },
                    label = { Text("Title") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { viewModel.onSaveClicked() })
                )
                Spacer(
                    modifier =
                    Modifier
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
            EditTask(viewModel = FakeEditTaskViewModel())
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
            EditTask(viewModel = FakeEditTaskViewModel().also {
                it.isLoading.value = true
            })
        }
    }
}
