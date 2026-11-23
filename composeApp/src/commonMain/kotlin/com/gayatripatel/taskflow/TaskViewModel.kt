package com.gayatripatel.taskflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gayatripatel.taskflow.shared.InMemoryTaskRepository
import com.gayatripatel.taskflow.shared.SampleTasks
import com.gayatripatel.taskflow.shared.TaskAction
import com.gayatripatel.taskflow.shared.TaskListState
import com.gayatripatel.taskflow.shared.TaskListStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/** Multiplatform ViewModel (JetBrains lifecycle): survives configuration changes on Android, plain object on desktop. */
class TaskViewModel(
    today: () -> LocalDate = { Clock.System.todayIn(TimeZone.currentSystemDefault()) },
) : ViewModel() {

    private val store = TaskListStore(
        repository = InMemoryTaskRepository(SampleTasks.create(today())),
        today = today,
        scope = viewModelScope,
    )

    val state: StateFlow<TaskListState> = store.state

    fun dispatch(action: TaskAction) = store.dispatch(action)
}
