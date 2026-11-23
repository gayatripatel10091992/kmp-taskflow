package com.gayatripatel.taskflow.shared

import kotlinx.datetime.LocalDate

enum class Priority(val label: String) {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low"),
}

data class Task(
    val id: Long,
    val title: String,
    val priority: Priority,
    val dueDate: LocalDate? = null,
    val tag: String? = null,
    val isDone: Boolean = false,
)

enum class TaskFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active"),
    DONE("Done"),
}

enum class DueStatus { NONE, OVERDUE, TODAY, UPCOMING }

fun Task.dueStatus(today: LocalDate): DueStatus = when {
    dueDate == null || isDone -> DueStatus.NONE
    dueDate < today -> DueStatus.OVERDUE
    dueDate == today -> DueStatus.TODAY
    else -> DueStatus.UPCOMING
}

data class TaskStats(
    val total: Int = 0,
    val done: Int = 0,
    val overdue: Int = 0,
    val dueToday: Int = 0,
) {
    val active: Int get() = total - done
    val progress: Float get() = if (total == 0) 0f else done.toFloat() / total
}
