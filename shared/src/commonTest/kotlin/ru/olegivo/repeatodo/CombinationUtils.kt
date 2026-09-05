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

@file:Suppress("unused")

package ru.olegivo.repeatodo

import io.kotest.core.test.TestScope

val booleans = listOf(true, false)

data class Row2<A, B>(val a: A, val b: B)
data class Row3<A, B, C>(val a: A, val b: B, val c: C)
data class Row4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
data class Row5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
data class Row6<A, B, C, D, E, F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F)

fun <A, B> row(a: A, b: B) = Row2(a, b)
fun <A, B, C> row(a: A, b: B, c: C) = Row3(a, b, c)
fun <A, B, C, D> row(a: A, b: B, c: C, d: D) = Row4(a, b, c, d)
fun <A, B, C, D, E> row(a: A, b: B, c: C, d: D, e: E) = Row5(a, b, c, d, e)
fun <A, B, C, D, E, F> row(a: A, b: B, c: C, d: D, e: E, f: F) = Row6(a, b, c, d, e, f)

// 2
fun <T1, T2> combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    except2: Set<Row2<T1, T2>> = emptySet(),
): List<Row2<T1, T2>> =
    inputs1
        .flatMap { t1 ->
            inputs2.map { t2 ->
                row(t1, t2)
            }
        }
        .filter { !except2.contains(it) }

suspend fun <T1, T2> TestScope.combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    except2: Set<Row2<T1, T2>> = emptySet(),
    block: suspend (Row2<T1, T2>) -> Unit,
) {
    combinator(inputs1, inputs2, except2).forEach { block(it) }
}

fun <T1, T2> combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    except2: Set<Row2<T1, T2>> = emptySet(),
    block: (Row2<T1, T2>) -> Unit,
) {
    combinator(inputs1, inputs2, except2).forEach { block(it) }
}

// 3
fun <T1, T2, T3> combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    inputs3: List<T3>,
    except3: Set<Row3<T1, T2, T3>> = emptySet(),
): List<Row3<T1, T2, T3>> =
    combinator(inputs1, inputs2)
        .flatMap { (t1, t2) ->
            inputs3.map { t3 ->
                row(t1, t2, t3)
            }
        }
        .filter { !except3.contains(it) }

suspend fun <T1, T2, T3> TestScope.combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    inputs3: List<T3>,
    except3: Set<Row3<T1, T2, T3>> = emptySet(),
    block: suspend (Row3<T1, T2, T3>) -> Unit,
) {
    combinator(inputs1, inputs2, inputs3, except3).forEach { block(it) }
}

fun <T1, T2, T3> combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    inputs3: List<T3>,
    except3: Set<Row3<T1, T2, T3>> = emptySet(),
    block: (Row3<T1, T2, T3>) -> Unit,
) {
    combinator(inputs1, inputs2, inputs3, except3).forEach { block(it) }
}

// 4
fun <T1, T2, T3, T4> combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    inputs3: List<T3>,
    inputs4: List<T4>,
    except4: Set<Row4<T1, T2, T3, T4>> = emptySet(),
): List<Row4<T1, T2, T3, T4>> =
    combinator(inputs1, inputs2, inputs3)
        .flatMap { (t1, t2, t3) ->
            inputs4.map { t4 ->
                row(t1, t2, t3, t4)
            }
        }
        .filter { !except4.contains(it) }

suspend fun <T1, T2, T3, T4> TestScope.combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    inputs3: List<T3>,
    inputs4: List<T4>,
    except4: Set<Row4<T1, T2, T3, T4>> = emptySet(),
    block: suspend (Row4<T1, T2, T3, T4>) -> Unit,
) {
    combinator(inputs1, inputs2, inputs3, inputs4, except4).forEach { block(it) }
}

// 5
fun <T1, T2, T3, T4, T5> combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    inputs3: List<T3>,
    inputs4: List<T4>,
    inputs5: List<T5>,
    except5: Set<Row5<T1, T2, T3, T4, T5>> = emptySet(),
): List<Row5<T1, T2, T3, T4, T5>> =
    combinator(inputs1, inputs2, inputs3, inputs4)
        .flatMap { (t1, t2, t3, t4) ->
            inputs5.map { t5 ->
                row(t1, t2, t3, t4, t5)
            }
        }
        .filter { !except5.contains(it) }

suspend fun <T1, T2, T3, T4, T5> TestScope.combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    inputs3: List<T3>,
    inputs4: List<T4>,
    inputs5: List<T5>,
    except5: Set<Row5<T1, T2, T3, T4, T5>> = emptySet(),
    block: suspend (Row5<T1, T2, T3, T4, T5>) -> Unit,
) {
    combinator(inputs1, inputs2, inputs3, inputs4, inputs5, except5).forEach { block(it) }
}

// 6
fun <T1, T2, T3, T4, T5, T6> combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    inputs3: List<T3>,
    inputs4: List<T4>,
    inputs5: List<T5>,
    inputs6: List<T6>,
    except6: Set<Row6<T1, T2, T3, T4, T5, T6>> = emptySet(),
): List<Row6<T1, T2, T3, T4, T5, T6>> =
    combinator(inputs1, inputs2, inputs3, inputs4, inputs5)
        .flatMap { (t1, t2, t3, t4, t5) ->
            inputs6.map { t6 ->
                row(t1, t2, t3, t4, t5, t6)
            }
        }
        .filter { !except6.contains(it) }

suspend fun <T1, T2, T3, T4, T5, T6> TestScope.combinator(
    inputs1: List<T1>,
    inputs2: List<T2>,
    inputs3: List<T3>,
    inputs4: List<T4>,
    inputs5: List<T5>,
    inputs6: List<T6>,
    except6: Set<Row6<T1, T2, T3, T4, T5, T6>> = emptySet(),
    block: suspend (Row6<T1, T2, T3, T4, T5, T6>) -> Unit,
) {
    combinator(inputs1, inputs2, inputs3, inputs4, inputs5, inputs6, except6).forEach { block(it) }
}
