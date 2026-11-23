package com.gayatripatel.taskflow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gayatripatel.taskflow.ui.AddTaskDialog
import com.gayatripatel.taskflow.ui.TaskFlowTheme
import com.gayatripatel.taskflow.ui.TaskListScreen

/** Entry point shared by every platform. */
@Composable
fun App(viewModel: TaskViewModel) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    TaskFlowTheme {
        TaskListScreen(
            state = state,
            platform = platformName(),
            onAction = viewModel::dispatch,
            onAddClick = { showAddDialog = true },
        )
        val today = state.today
        if (showAddDialog && today != null) {
            AddTaskDialog(
                today = today,
                onDismiss = { showAddDialog = false },
                onAdd = { action ->
                    viewModel.dispatch(action)
                    showAddDialog = false
                },
            )
        }
    }
}
