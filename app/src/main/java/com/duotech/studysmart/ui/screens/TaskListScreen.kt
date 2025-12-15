package com.duotech.studysmart.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.duotech.studysmart.data.TaskEntity
import com.duotech.studysmart.repo.TaskRepository
import com.duotech.studysmart.reminders.ReminderScheduler
import com.duotech.studysmart.util.formatEpochMillis
import kotlinx.coroutines.launch

private enum class Tab { UPCOMING, ALL, COMPLETED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    repo: TaskRepository,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onAbout: () -> Unit
) {
    var tab by remember { mutableStateOf(Tab.UPCOMING) }

    val tasksFlow = remember(tab) {
        when (tab) {
            Tab.UPCOMING -> repo.observeUpcoming()
            Tab.ALL -> repo.observeAll()
            Tab.COMPLETED -> repo.observeCompleted()
        }
    }

    val tasks by tasksFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StudySmart") },
                actions = {
                    TextButton(onClick = onAbout) { Text("About")}
                }
            )
        },

        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Text("+") } }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = tab.ordinal) {
                Tab.entries.forEach { t ->
                    Tab(
                        selected = tab == t,
                        onClick = { tab = t },
                        text = {
                            Text(t.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            TaskList(
                tasks = tasks,
                onEdit = onEdit,
                repo = repo
            )
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<TaskEntity>,
    onEdit: (Long) -> Unit,
    repo: TaskRepository
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (tasks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("No tasks yet. Tap + to add one.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(tasks) { task ->
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEdit(task.id) }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(task.title, style = MaterialTheme.typography.titleMedium)

                        if (task.description.isNotBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text(task.description, style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "Due: ${formatEpochMillis(task.dueAtMillis)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Type: ${task.type} • Priority: ${task.priority}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Reminder: ${task.reminderMinutesBefore?.let { "$it min before" } ?: "Off"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {

                        TextButton(
                            onClick = {
                                scope.launch {
                                    val updated = task.copy(
                                        isCompleted = !task.isCompleted,
                                        updatedAtMillis = System.currentTimeMillis()
                                    )
                                    repo.upsert(updated)

                                    if (updated.isCompleted) {
                                        ReminderScheduler.scheduleOrCancel(
                                            context,
                                            updated.id,
                                            updated.dueAtMillis,
                                            null
                                        )
                                    } else {
                                        ReminderScheduler.scheduleOrCancel(
                                            context,
                                            updated.id,
                                            updated.dueAtMillis,
                                            updated.reminderMinutesBefore
                                        )
                                    }
                                }
                            }
                        ) {
                            Text(if (task.isCompleted) "Uncomplete" else "Complete")
                        }

                        TextButton(
                            onClick = {
                                scope.launch {
                                    // cancel reminders
                                    ReminderScheduler.scheduleOrCancel(
                                        context,
                                        task.id,
                                        task.dueAtMillis,
                                        null
                                    )
                                    // delete
                                    repo.deleteById(task.id)
                                }
                            }
                        ) {
                            Text("Delete")
                        }
                    }
                }

            }

        }

    }

}
