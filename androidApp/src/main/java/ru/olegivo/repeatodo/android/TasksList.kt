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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ru.olegivo.repeatodo.domain.models.Task
import ru.olegivo.repeatodo.list.presentation.FakeTasksListViewModel
import ru.olegivo.repeatodo.list.presentation.TasksListViewModel

@Composable
internal fun TasksList(
    modifier: Modifier = Modifier,
    isPreview: Boolean = false,
    viewModel: TasksListViewModel = if (isPreview) FakeTasksListViewModel() else koinInject()
) {
    val tasks = viewModel.state.collectAsState().value.tasks
    LazyColumn(
        modifier
//            .verticalScroll(rememberScrollState())
//            .background(MaterialTheme.colorScheme.primaryContainer),
    ) {
        itemsIndexed(
            items = tasks,
            key = { _, task -> task.uuid }
        ) { _, task ->
            Row {
                TaskItem(task = task)
            }
        }
    }
}

@Composable
private fun TaskItem(modifier: Modifier = Modifier, task: Task) {
    Text(
        task.title,
        modifier
            .padding(16.dp)
            .height(32.dp)
    )
}

@Preview
@Composable
private fun TasksListPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.navigationBars.only(
                        WindowInsetsSides.Start +
                                WindowInsetsSides.End +
                                WindowInsetsSides.Top +
                                WindowInsetsSides.Bottom
                    )
                ),
            color = MaterialTheme.colorScheme.primary
        ) {
            TasksList(isPreview = true)
        }
    }
}
