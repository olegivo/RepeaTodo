package ru.olegivo.repeatodo.utils

import platform.Foundation.NSUUID

actual fun newUuid(): String = NSUUID.UUID().toString()
