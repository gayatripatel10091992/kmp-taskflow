package com.gayatripatel.taskflow.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * Shared presentation logic. Android, Desktop and iOS all use this exact class;
 * each platform only supplies a [CoroutineScope] (e.g. `viewModelScope`) and a clock.
 */
class TaskListStore(
    private val repository: TaskRepository,
    private val today: () -> LocalDate,
    private val scope: CoroutineScope,
) {
    private val filter = MutableStateFlow(TaskFilter.ALL)
    private val query = MutableStateFlow("")

    val state: StateFlow<TaskListState> =
        combine(repository.observeTasks(), filter, query) { tasks, filter, query ->
            TaskListState.from(tasks, filter, query, today())
        }.stateIn(scope, SharingStarted.Eagerly, TaskListState(today = today()))

    fun dispatch(action: TaskAction) {
        when (action) {
            is TaskAction.SetFilter -> filter.value = action.filter
            is TaskAction.SetQuery -> query.value = action.query
            is TaskAction.Toggle -> scope.launch { repository.toggle(action.id) }
            is TaskAction.Delete -> scope.launch { repository.delete(action.id) }
            is TaskAction.Add -> {
                val title = action.title.trim()
                if (title.isEmpty()) return
                scope.launch {
                    repository.add(
                        Task(
                            id = 0,
                            title = title,
                            priority = action.priority,
                            dueDate = action.dueDate,
                            tag = action.tag?.trim()?.ifEmpty { null },
                        ),
                    )
                }
            }
        }
    }
}
