package com.gayatripatel.taskflow

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() {
    val viewModel = TaskViewModel()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "TaskFlow",
            state = rememberWindowState(width = 440.dp, height = 860.dp),
        ) {
            App(viewModel)
        }
    }
}
