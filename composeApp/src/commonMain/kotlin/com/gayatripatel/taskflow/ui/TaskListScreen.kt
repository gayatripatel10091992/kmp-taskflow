package com.gayatripatel.taskflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gayatripatel.taskflow.shared.DueStatus
import com.gayatripatel.taskflow.shared.Task
import com.gayatripatel.taskflow.shared.TaskAction
import com.gayatripatel.taskflow.shared.TaskFilter
import com.gayatripatel.taskflow.shared.TaskListState
import com.gayatripatel.taskflow.shared.TaskStats
import com.gayatripatel.taskflow.shared.dueStatus
import kotlinx.datetime.LocalDate

/** Stateless: renders [state] and reports user intents through [onAction]. Shared by Android and Desktop. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    state: TaskListState,
    platform: String,
    onAction: (TaskAction) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("TaskFlow", fontWeight = FontWeight.Bold)
                        Text(
                            "Kotlin Multiplatform · $platform",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("New task") },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { StatsCard(state.stats) }
            item {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = { onAction(TaskAction.SetQuery(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search tasks or tags") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskFilter.entries.forEach { filter ->
                        FilterChip(
                            selected = state.filter == filter,
                            onClick = { onAction(TaskAction.SetFilter(filter)) },
                            label = { Text(filter.label) },
                        )
                    }
                }
            }
            if (state.visibleTasks.isEmpty()) {
                item {
                    Text(
                        "Nothing here. Enjoy the calm.",
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(state.visibleTasks, key = { it.id }) { task ->
                TaskRow(
                    task = task,
                    today = state.today,
                    onToggle = { onAction(TaskAction.Toggle(task.id)) },
                    onDelete = { onAction(TaskAction.Delete(task.id)) },
                )
            }
        }
    }
}

@Composable
private fun StatsCard(stats: TaskStats) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "${stats.done} of ${stats.total} done",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { stats.progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatPill("${stats.active} active", MaterialTheme.colorScheme.primary)
                StatPill("${stats.dueToday} due today", Color(0xFFF5A524))
                StatPill("${stats.overdue} overdue", Color(0xFFE5484D))
            }
        }
    }
}

@Composable
private fun StatPill(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.15f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun TaskRow(
    task: Task,
    today: LocalDate?,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Row(Modifier.padding(start = 4.dp, end = 4.dp, top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = task.isDone, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                    color = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(task.priority.color()))
                    Text(task.priority.label, style = MaterialTheme.typography.labelMedium)
                    task.tag?.let { Text("#$it", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary) }
                    if (today != null) DueLabel(task, today)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DueLabel(task: Task, today: LocalDate) {
    val due = task.dueDate ?: return
    val (text, color) = when (task.dueStatus(today)) {
        DueStatus.OVERDUE -> "Overdue · ${due.short()}" to Color(0xFFE5484D)
        DueStatus.TODAY -> "Today" to Color(0xFFF5A524)
        DueStatus.UPCOMING -> due.short() to MaterialTheme.colorScheme.onSurfaceVariant
        DueStatus.NONE -> due.short() to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Spacer(Modifier.width(2.dp))
    Text(text, style = MaterialTheme.typography.labelMedium, color = color)
}

private fun LocalDate.short(): String =
    month.name.take(3).lowercase().replaceFirstChar { it.uppercase() } + " " + dayOfMonth
