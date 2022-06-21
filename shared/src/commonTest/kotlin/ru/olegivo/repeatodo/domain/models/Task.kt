package ru.olegivo.repeatodo.domain.models

import ru.olegivo.repeatodo.randomString

fun createTask() = Task(
    uuid = randomString(),
    title = randomString()
)
