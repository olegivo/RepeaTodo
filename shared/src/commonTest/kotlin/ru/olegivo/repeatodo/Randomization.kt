package ru.olegivo.repeatodo

import kotlin.random.Random

fun randomString(size: Int = 10) = Random.azstring(size)
fun Random.azchar(): Char = nextInt(from = 97, until = 123).toChar()
fun Random.azstring(size: Int): String {
    val chars = List(size) { azchar() }.toCharArray()
    return chars.concatToString()
}