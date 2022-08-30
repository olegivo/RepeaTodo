/*
 * Copyright (C) 2022 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.repeatodo

import kotlin.random.Random

fun randomString(size: Int = 10) = Random.azstring(size)
fun Random.azchar(): Char = nextInt(from = 97, until = 123).toChar()
fun Random.azstring(size: Int): String {
    val chars = List(size) { azchar() }.toCharArray()
    return chars.concatToString()
}

data class Position(private val range: IntRange, val position: Int) {

    val isLast = position == range.last
}

fun <T> randomList(
    count: Int = Random.nextInt(from = 5, until = 10),
    producer: Position.() -> T,
): List<T> =
    (0 until count).let { range ->
        range.map { position ->
            Position(range, position).producer()
        }
    }
