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

package ru.olegivo.repeatodo.imterop

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

fun Flow<*>.subscribe(
    onEach: (item: Any?) -> Unit,
    onComplete: () -> Unit,
    onThrow: (error: Throwable) -> Unit
): Job = this.subscribe(Dispatchers.Main, onEach, onComplete, onThrow)

fun Flow<*>.subscribe(
    dispatcher: CoroutineDispatcher,
    onEach: (item: Any?) -> Unit,
    onComplete: () -> Unit,
    onThrow: (error: Throwable) -> Unit
): Job =
    this.onEach { onEach(it as Any?) }
        .catch { onThrow(it) }
        .onCompletion { onComplete() }
        .launchIn(CoroutineScope(Job() + dispatcher))
