package com.gayatripatel.taskflow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.gayatripatel.taskflow.shared.SampleTasks
import com.gayatripatel.taskflow.shared.TaskFilter
import com.gayatripatel.taskflow.shared.TaskListState
import com.gayatripatel.taskflow.ui.AddTaskForm
import com.gayatripatel.taskflow.ui.TaskFlowTheme
import com.gayatripatel.taskflow.ui.TaskListScreen
import kotlinx.datetime.LocalDate
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Renders the shared Compose Multiplatform UI headlessly on the JVM (Skia) at phone size and writes PNGs.
 * CI copies them into docs/screenshots. Same composables as the Android app.
 */
@OptIn(ExperimentalComposeUiApi::class)
class ScreenshotTest {

    private val today = LocalDate(2026, 11, 23)
    private val tasks = SampleTasks.create(today)

    @Test
    fun taskList_light() = capture("01_task_list_light") {
        TaskFlowTheme(darkTheme = false) { Screen(TaskListState.from(tasks, TaskFilter.ALL, "", today)) }
    }

    @Test
    fun taskList_dark() = capture("02_task_list_dark") {
        TaskFlowTheme(darkTheme = true) { Screen(TaskListState.from(tasks, TaskFilter.ALL, "", today)) }
    }

    @Test
    fun taskList_activeFilter() = capture("03_active_filter") {
        TaskFlowTheme(darkTheme = false) { Screen(TaskListState.from(tasks, TaskFilter.ACTIVE, "", today)) }
    }

    @Test
    fun addTaskForm() = capture("04_add_task") {
        TaskFlowTheme(darkTheme = false) {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f)) {
                Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
                    AddTaskForm(today = today, onCancel = {}, onAdd = {}, initialTitle = "Prepare release notes")
                }
            }
        }
    }

    @Composable
    private fun Screen(state: TaskListState) {
        TaskListScreen(state = state, platform = "shared UI", onAction = {}, onAddClick = {})
    }

    private fun capture(name: String, content: @Composable () -> Unit) {
        val density = 2.625f // Pixel-class density
        val widthDp = 411
        val heightDp = 891
        val file = File("build/screenshots/$name.png")
        val scene = ImageComposeScene(
            width = (widthDp * density).toInt(),
            height = (heightDp * density).toInt(),
            density = Density(density),
            content = content,
        )
        try {
            scene.render() // first frame: composition
            val image = scene.render(nanoTime = 500_000_000L) // second frame: let effects settle
            val png = image.encodeToData(EncodedImageFormat.PNG) ?: error("PNG encoding failed")
            file.parentFile.mkdirs()
            file.writeBytes(png.bytes)
        } finally {
            scene.close()
        }
        assertTrue(file.length() > 0, "Screenshot $name was not written")
    }
}
