package ru.olegivo.repeatodo.db

import ru.olegivo.repeatodo.db.Task as TaskDb
import ru.olegivo.repeatodo.domain.models.Task as TaskDomain

internal fun TaskDomain.doDb() =
    TaskDb(
        uuid = uuid,
        title = title
    )
