package ru.olegivo.repeatodo

actual class Platform actual constructor() {
    actual val platform: String
        get() = "Hello, JVM!"
}