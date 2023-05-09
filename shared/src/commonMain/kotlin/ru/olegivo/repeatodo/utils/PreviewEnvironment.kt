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

package ru.olegivo.repeatodo.utils

import kotlin.reflect.KClass

class PreviewEnvironment {

    val registry = mutableMapOf<KClass<*>, () -> Any>()

    inline fun <reified T> get(): T = get(T::class)

    inline fun <reified T> get(kClass: KClass<*>) =
        registry.getValue(kClass).invoke() as T

    inline fun <reified T: Any> register(crossinline block: () -> T) {
        registry[T::class] = { block() }
    }

    companion object {
        operator fun invoke(block: PreviewEnvironment.() -> Unit) =
            PreviewEnvironment().apply(block)
    }
}
