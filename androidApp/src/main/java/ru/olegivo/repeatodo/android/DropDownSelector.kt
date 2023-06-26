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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Clear
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun <T: Any> DropDownSelector(
    modifier: Modifier,
    items: State<List<T>>,
    initialValue: State<T?>,
    textSelector: T.() -> String,
    noneSelectedItem: T? = null,
    canClear: Boolean = true,
    onSelected: (T?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItemIndex by remember { mutableStateOf(items.value.indexOf(initialValue.value)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(modifier, verticalAlignment = Alignment.CenterVertically) {
                val selectedItem = items.value
                    .takeIf { selectedItemIndex in it.indices }
                    ?.get(selectedItemIndex)
                    ?: noneSelectedItem
                TextField(
                    value = selectedItem?.textSelector() ?: "",
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                )
                if (canClear) {
                    IconButton(onClick = {
                        onSelected(null)
                        selectedItemIndex = -1
                    }) {
                        Icon(Icons.Sharp.Clear, contentDescription = "Clear selection")
                    }
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                if (noneSelectedItem != null) {
                    DropdownMenuItem(
                        text = {
                            Row {
                                val isSelected = -1 == selectedItemIndex
                                Text(
                                    text = noneSelectedItem.textSelector(),
                                    fontWeight = if (isSelected) FontWeight.Bold else null
                                )
                                Spacer(Modifier.weight(1f))
                                if (isSelected) {
                                    Checkbox(
                                        checked = true,
                                        enabled = false,
                                        onCheckedChange = null
                                    )
                                }
                            }
                        },
                        onClick = {
                            selectedItemIndex = -1
                            expanded = false
                        }
                    )
                }
                items.value.forEachIndexed { index, item ->
                    val title = item.textSelector()
                    DropdownMenuItem(
                        text = {
                            Row {
                                val isSelected = index == selectedItemIndex
                                Text(
                                    text = title,
                                    fontWeight = if (isSelected) FontWeight.Bold else null
                                )
                                Spacer(Modifier.weight(1f))
                                if (isSelected) {
                                    Checkbox(
                                        checked = true,
                                        enabled = false,
                                        onCheckedChange = null
                                    )
                                }
                            }
                        },
                        onClick = {
                            selectedItemIndex = index
                            expanded = false
                            onSelected(item)
                        }
                    )
                }
            }
        }
    }
}
