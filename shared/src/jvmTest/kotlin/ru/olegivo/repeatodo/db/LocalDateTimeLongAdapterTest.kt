/*
 * Copyright (C) 2023 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.repeatodo.db

import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.olegivo.repeatodo.domain.DateTimeProviderImpl
import ru.olegivo.repeatodo.domain.roundNanoseconds
import ru.olegivo.repeatodo.kotest.FreeSpec
import ru.olegivo.repeatodo.randomLocalDateTime
import kotlin.time.Duration.Companion.nanoseconds

class LocalDateTimeLongAdapterTest: FreeSpec() {
    init {
        val dateTimeProvider = DateTimeProviderImpl()
        val localDateTimeLongAdapter = LocalDateTimeLongAdapter(dateTimeProvider)

        "decode(encode(value)) should be value" {
            val localDateTime = randomLocalDateTime()

            val dbValue = localDateTimeLongAdapter.encode(localDateTime)
            val decoded = localDateTimeLongAdapter.decode(dbValue)

            decoded shouldBe localDateTime
        }

        "https://github.com/Kotlin/kotlinx-datetime/issues/270" {
            val origin = Clock.System.now().roundNanoseconds()

            val dbValue = origin.toEpochMilliseconds()
            val decoded = Instant.fromEpochMilliseconds(dbValue)

            decoded shouldBe origin
        }
    }
}