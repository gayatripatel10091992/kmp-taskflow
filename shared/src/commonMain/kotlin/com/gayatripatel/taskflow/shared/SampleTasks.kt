package com.gayatripatel.taskflow.shared

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/** Demo data relative to [today], so the app always shows overdue / today / upcoming examples. */
object SampleTasks {
    fun create(today: LocalDate): List<Task> = listOf(
        Task(1, "Fix ANR in call screen", Priority.HIGH, today.minus(DatePeriod(days = 1)), "Android"),
        Task(2, "Review Play Billing upgrade PR", Priority.HIGH, today, "Review"),
        Task(3, "Target API 35 behaviour changes", Priority.MEDIUM, today.plus(DatePeriod(days = 3)), "Android"),
        Task(4, "Write KMP shared module tests", Priority.MEDIUM, today.plus(DatePeriod(days = 5)), "KMP"),
        Task(5, "Plan sprint with client", Priority.LOW, today.plus(DatePeriod(days = 7)), "Planning"),
        Task(6, "Update LeakCanary + Crashlytics", Priority.LOW, null, "Tooling"),
        Task(7, "Migrate settings screen to Compose", Priority.MEDIUM, today.minus(DatePeriod(days = 2)), "Compose", isDone = true),
        Task(8, "Set up GitHub Actions CI", Priority.HIGH, today.minus(DatePeriod(days = 4)), "CI", isDone = true),
    )
}
