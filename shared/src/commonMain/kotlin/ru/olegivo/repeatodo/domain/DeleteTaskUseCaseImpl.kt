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

package ru.olegivo.repeatodo.domain

import kotlinx.coroutines.flow.flow
import ru.olegivo.repeatodo.domain.models.Task

class DeleteTaskUseCaseImpl(private val tasksRepository: TasksRepository) : DeleteTaskUseCase {
    override suspend fun invoke(task: Task) = flow {
        try {
            emit(WorkState.InProgress())
            tasksRepository.delete(task.uuid)
            emit(WorkState.Completed(Unit))
        } catch (e: Throwable) {
            emit(WorkState.Error())
        }
    }
}
