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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.list.presentation.TasksListViewModel
import ru.olegivo.repeatodo.list.presentation.taskListFakes
import ru.olegivo.repeatodo.preview.fakeOrInjectKoin
import ru.olegivo.repeatodo.utils.PreviewEnvironment
import ru.olegivo.repeatodo.utils.newUuid

@Composable
internal fun TasksList(
    modifier: Modifier = Modifier,
    previewEnvironment: PreviewEnvironment? = null,
) {
    val viewModel: TasksListViewModel = fakeOrInjectKoin(previewEnvironment)
    val tasks = viewModel.state.collectAsState().value.tasks
    LazyColumn(modifier) {
        itemsIndexed(
            items = tasks,
            key = { _, task -> task.uuid }
        ) { index, task ->
            TaskItem(
                Modifier.fillMaxWidth(),
                task = task,
                onTaskEditClicked = { viewModel.onTaskEditClicked(task) }
            )
            if (index != tasks.lastIndex) {
                Divider()
            }
        }
    }
}

@Composable
private fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    onTaskEditClicked: (Task) -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = task.title,
            modifier = Modifier
                .weight(1f)
        )
        Button(onClick = { onTaskEditClicked(task) }) {
            Icon(Icons.Rounded.Edit, "Edit")
        }
    }
}

@Preview
@Composable
private fun TasksListPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary
        ) {
            TasksList(
                previewEnvironment = PreviewEnvironment { taskListFakes() }
            )
        }
    }
}

@Preview
@Composable
private fun TaskItemPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            TaskItem(
                task = Task(
                    uuid = newUuid(),
                    title = "Todo 1",
                    daysPeriodicity = 1,
                    lastCompletionDate = null
                )
            )
        }
    }
}
