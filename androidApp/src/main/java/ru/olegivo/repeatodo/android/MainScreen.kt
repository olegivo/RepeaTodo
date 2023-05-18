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

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ru.olegivo.repeatodo.main.mainScreenFakes
import ru.olegivo.repeatodo.utils.PreviewEnvironment

@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    previewEnvironment: PreviewEnvironment? = null
) {
    Surface(
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
        color = MaterialTheme.colorScheme.primary
    ) {
        ConstraintLayout {
            val (list, add) = createRefs()
            TasksList(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
                    .constrainAs(list) {
                        height = Dimension.fillToConstraints
                        top.linkTo(parent.top)
                        bottom.linkTo(add.top)
                    },
                previewEnvironment = previewEnvironment
            )
            AddTaskInlined(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(add) {
                        bottom.linkTo(parent.bottom)
                    },
                previewEnvironment = previewEnvironment
            )
        }
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
