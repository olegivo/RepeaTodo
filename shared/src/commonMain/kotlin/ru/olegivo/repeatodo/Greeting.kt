package ru.olegivo.repeatodo

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}