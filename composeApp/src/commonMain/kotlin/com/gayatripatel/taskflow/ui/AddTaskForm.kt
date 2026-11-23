package com.gayatripatel.taskflow.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gayatripatel.taskflow.shared.Priority
import com.gayatripatel.taskflow.shared.TaskAction
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

enum class DueOption(val label: String, val days: Int?) {
    NONE("No date", null),
    TODAY("Today", 0),
    TOMORROW("Tomorrow", 1),
    NEXT_WEEK("Next week", 7),
}

@Composable
fun AddTaskDialog(today: LocalDate, onDismiss: () -> Unit, onAdd: (TaskAction.Add) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        AddTaskForm(today = today, onCancel = onDismiss, onAdd = onAdd)
    }
}

/** The form itself is a plain composable so it can be screenshot-tested without a window. */
@Composable
fun AddTaskForm(
    today: LocalDate,
    onCancel: () -> Unit,
    onAdd: (TaskAction.Add) -> Unit,
    initialTitle: String = "",
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf(initialTitle) }
    var tag by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var due by remember { mutableStateOf(DueOption.TOMORROW) }

    Surface(modifier = modifier, shape = MaterialTheme.shapes.extraLarge, tonalElevation = 6.dp) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("New task", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = tag,
                onValueChange = { tag = it },
                label = { Text("Tag (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Text("Priority", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.entries.forEach { option ->
                    FilterChip(selected = priority == option, onClick = { priority = option }, label = { Text(option.label) })
                }
            }
            Text("Due", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DueOption.entries.forEach { option ->
                    FilterChip(selected = due == option, onClick = { due = option }, label = { Text(option.label) })
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Cancel") }
                Button(
                    enabled = title.isNotBlank(),
                    onClick = {
                        val dueDate = due.days?.let { today.plus(DatePeriod(days = it)) }
                        onAdd(TaskAction.Add(title, priority, dueDate, tag))
                    },
                ) { Text("Add task") }
            }
        }
    }
}
