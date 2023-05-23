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

package ru.olegivo.repeatodo.domain.models

sealed interface ToDoList {
    val uuid: String
    val title: String

    data class Predefined(override val uuid: String, override val title: String): ToDoList {
        override fun equals(other: Any?) =
            other != null && other is Predefined && other.uuid == uuid

        override fun hashCode() = uuid.hashCode()

        enum class Kind(val uuid: String) {
            INBOX("df210f40-1b27-4675-8a82-9e2f1e6de302")
        }
    }

    data class Custom(override val uuid: String, override val title: String): ToDoList
}
