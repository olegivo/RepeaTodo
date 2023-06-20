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

package ru.olegivo.repeatodo.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.koinInject
import org.koin.core.parameter.ParametersDefinition
import ru.olegivo.repeatodo.utils.PreviewEnvironment


@Composable
internal inline fun <reified T> PreviewEnvironment?.fakeOrInjectKoin(
    rememberKey: Any? = null,
    noinline parameters: ParametersDefinition? = null
): T =
    this?.get(parameters?.invoke()?.values?.firstOrNull())
        ?: run {
            val result = koinInject<T>(parameters = parameters)
            if (rememberKey != null) remember(rememberKey) {
                result
            } else {
                result
            }
        }
