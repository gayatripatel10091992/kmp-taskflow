package com.gayatripatel.taskflow.shared

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    suspend fun add(task: Task): Task
    suspend fun toggle(id: Long)
    suspend fun delete(id: Long)
}

/** Thread-safe in-memory store. Swap for SQLDelight/Room KMP without touching the UI or the store. */
class InMemoryTaskRepository(seed: List<Task> = emptyList()) : TaskRepository {

    private val tasks = MutableStateFlow(seed)

    override fun observeTasks(): Flow<List<Task>> = tasks.asStateFlow()

    override suspend fun add(task: Task): Task {
        var created = task
        tasks.update { current ->
            created = task.copy(id = (current.maxOfOrNull { it.id } ?: 0L) + 1)
            current + created
        }
        return created
    }

    override suspend fun toggle(id: Long) {
        tasks.update { current -> current.map { if (it.id == id) it.copy(isDone = !it.isDone) else it } }
    }

    override suspend fun delete(id: Long) {
        tasks.update { current -> current.filterNot { it.id == id } }
    }
}
