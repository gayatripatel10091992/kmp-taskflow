package com.gayatripatel.taskflow.shared

import kotlinx.datetime.LocalDate

/** Everything the task list screen renders. Built by a pure function so it is trivial to test on every platform. */
data class TaskListState(
    val visibleTasks: List<Task> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val query: String = "",
    val stats: TaskStats = TaskStats(),
    val today: LocalDate? = null,
) {
    companion object {
        fun from(
            tasks: List<Task>,
            filter: TaskFilter,
            query: String,
            today: LocalDate,
        ): TaskListState {
            val stats = TaskStats(
                total = tasks.size,
                done = tasks.count { it.isDone },
                overdue = tasks.count { it.dueStatus(today) == DueStatus.OVERDUE },
                dueToday = tasks.count { it.dueStatus(today) == DueStatus.TODAY },
            )
            val visible = tasks
                .filter {
                    when (filter) {
                        TaskFilter.ALL -> true
                        TaskFilter.ACTIVE -> !it.isDone
                        TaskFilter.DONE -> it.isDone
                    }
                }
                .filter { query.isBlank() || it.title.contains(query.trim(), ignoreCase = true) || it.tag?.contains(query.trim(), ignoreCase = true) == true }
                .sortedWith(taskOrder)
            return TaskListState(visible, filter, query, stats, today)
        }

        /** Open tasks first, then by priority, then earliest due date (tasks without a date last). */
        private val taskOrder = compareBy<Task> { it.isDone }
            .thenBy { it.priority.ordinal }
            .thenBy(nullsLast()) { it.dueDate }
            .thenBy { it.id }
    }
}

/** User intents (MVI style). The UI only ever sends actions; it never mutates state directly. */
sealed interface TaskAction {
    data class SetFilter(val filter: TaskFilter) : TaskAction
    data class SetQuery(val query: String) : TaskAction
    data class Toggle(val id: Long) : TaskAction
    data class Delete(val id: Long) : TaskAction
    data class Add(val title: String, val priority: Priority, val dueDate: LocalDate?, val tag: String?) : TaskAction
}
