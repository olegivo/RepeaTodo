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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ru.olegivo.repeatodo.add.presentation.AddTaskUiState
import ru.olegivo.repeatodo.add.presentation.AddTaskViewModel
import ru.olegivo.repeatodo.add.presentation.FakeAddTaskViewModel

@Composable
internal fun AddTaskInlined(
    modifier: Modifier = Modifier,
    isPreview: Boolean = false,
    viewModel: AddTaskViewModel = if (isPreview) FakeAddTaskViewModel(AddTaskUiState()) else koinInject(),
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val title = viewModel.title.collectAsState()
        val enabled = viewModel.canAdd.collectAsState()
        TextField(
            value = title.value,
            onValueChange = { viewModel.title.value = it },
            modifier = Modifier
                .padding(16.dp)
                .weight(1f),
            placeholder = { Text("Enter A Title Here") },
            label = { Text("Title") }
        )
        Button(
            onClick = { viewModel.onAddClicked() },
            Modifier
                .padding(end = 16.dp),
            enabled = enabled.value
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add",
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
private fun AddTaskInlinedPreview() {
    MaterialTheme {
        AddTaskInlined(isPreview = true)
    }
}
