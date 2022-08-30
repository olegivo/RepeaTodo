/*
 * Copyright (C) 2022 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.repeatodo.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.olegivo.repeatodo.domain.models.Task

class GetTaskUseCaseImpl(private val tasksRepository: TasksRepository) : GetTaskUseCase {

    override fun invoke(uuid: String): Flow<WorkState<Task>> = flow<WorkState<Task>> {
        try {
            emit(WorkState.InProgress())
            emitAll(
                tasksRepository.getTask(uuid).map { task ->
                    task?.let { WorkState.Completed(it) }
                        ?: WorkState.Error()
                }
            )
        } catch (e: Throwable) {
            emit(WorkState.Error())
        }
    }
}
