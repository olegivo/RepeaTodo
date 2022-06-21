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
