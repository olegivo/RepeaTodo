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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.olegivo.repeatodo.main.presentation.mainScreenFakes
import ru.olegivo.repeatodo.main.presentation.MainViewModel
import ru.olegivo.repeatodo.preview.fakeOrInjectKoin
import ru.olegivo.repeatodo.utils.PreviewEnvironment

@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    previewEnvironment: PreviewEnvironment? = null
) {
    val viewModel: MainViewModel = fakeOrInjectKoin(previewEnvironment)
    val isCompletedTasksFilter = viewModel.isShowCompleted.collectAsState()
    val isHighestPriorityTasksFilter = viewModel.isShowOnlyHighestPriority.collectAsState()

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
            AppBar()
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
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text("RepeaTodo") },
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
