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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ru.olegivo.repeatodo.android.preview.PreviewsDayNight
import ru.olegivo.repeatodo.android.ui.theme.AppTheme
import ru.olegivo.repeatodo.list.presentation.PriorityInList
import ru.olegivo.repeatodo.list.presentation.TaskUi
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
    val isAllTasksFilter = viewModel.isShowCompleted.collectAsState()
    Column {
        IsAllTasksFilter(
            Modifier
                .align(Alignment.End)
                .padding(all = 16.dp),
            isAllTasksFilterState = isAllTasksFilter,
            onChange = { viewModel.isShowCompleted.value = it }
        )
        LazyColumn(modifier) {
            itemsIndexed(
                items = tasks,
                key = { _, task -> task.uuid }
            ) { index, task ->
                TaskItem(
                    Modifier.fillMaxWidth(),
                    task = task,
                    onTaskEditClicked = { viewModel.onTaskEditClicked(task) },
                    onCompleteTaskClicked = { viewModel.onTaskCompletionClicked(task) },
                )
                if (index != tasks.lastIndex) {
                    Divider()
                }
            }
        }
    }
}


@Composable
private fun IsAllTasksFilter(
    modifier: Modifier = Modifier,
    isAllTasksFilterState: State<Boolean>,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isAllTasksFilterState.value,
            onCheckedChange = onChange
        )
        Text(text = "Show completed")
    }
}

@Composable
private fun TaskItem(
    modifier: Modifier = Modifier,
    task: TaskUi,
    onTaskEditClicked: (TaskUi) -> Unit = {},
    onCompleteTaskClicked: (TaskUi) -> Unit = {},
) {
    ConstraintLayout(
        modifier = modifier
            .height(92.dp)
            .clickable { onTaskEditClicked(task) },
    ) {
        val (
            isCompleted,
            priorityView,
            title,
            lastCompletionDate,
        ) = createRefs()

        Checkbox(
            modifier = Modifier.constrainAs(isCompleted) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            },
            checked = task.isCompleted,
            onCheckedChange = { onCompleteTaskClicked(task) }
        )

        task.priority?.let { priority ->
            Text(
                modifier = Modifier.constrainAs(priorityView) {
                    width = Dimension.fillToConstraints
                    top.linkTo(isCompleted.bottom)
                    start.linkTo(parent.start, 22.dp)
                    end.linkTo(isCompleted.end)
                    bottom.linkTo(parent.bottom, 8.dp)
                },
                text = priority.title,
                color = Color(android.graphics.Color.parseColor(priority.color)),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            modifier = Modifier.constrainAs(title) {
                width = Dimension.fillToConstraints
                top.linkTo(parent.top, 12.dp)
                start.linkTo(isCompleted.end, 4.dp)
                end.linkTo(parent.end, 4.dp)
            },
            text = task.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        task.lastCompletionDate?.let {
            Text(
                modifier = Modifier.constrainAs(lastCompletionDate) {
                    top.linkTo(title.bottom, 8.dp)
                    start.linkTo(title.start)
                    end.linkTo(title.end)
                },
                text = it,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@PreviewsDayNight
@Composable
private fun TasksListPreview() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            TasksList(
                previewEnvironment = PreviewEnvironment { taskListFakes() },
            )
        }
    }
}

@PreviewsDayNight
@Composable
private fun TaskItemUncompletedPreview(
    @PreviewParameter(LoremIpsum::class) title: String,
) {
    AppTheme {
        Surface {
            TaskItem(
                task = TaskUi(
                    uuid = newUuid(),
                    title = title,
                    isCompleted = false,
                    lastCompletionDate = null,
                    priority = null,
                )
            )
        }
    }
}

@PreviewsDayNight
@Composable
private fun TaskItemCompletedPreview(
    @PreviewParameter(LoremIpsum::class) title: String
) {
    AppTheme {
        Surface {
            TaskItem(
                task = TaskUi(
                    uuid = newUuid(),
                    title = title,
                    isCompleted = true,
                    lastCompletionDate = "several seconds ago",
                    priority = null,
                )
            )
        }
    }
}

@PreviewsDayNight
@Composable
private fun TaskItemPriorityHighPreview(
    @PreviewParameter(LoremIpsum::class) title: String
) {
    AppTheme {
        Surface {
            TaskItem(
                task = TaskUi(
                    uuid = newUuid(),
                    title = title,
                    isCompleted = true,
                    lastCompletionDate = "several seconds ago",
                    priority = PriorityInList.HIGH,
                )
            )
        }
    }
}
