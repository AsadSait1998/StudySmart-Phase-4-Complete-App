package com.duotech.studysmart.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.duotech.studysmart.data.Priority
import com.duotech.studysmart.data.TaskEntity
import com.duotech.studysmart.data.TaskType
import com.duotech.studysmart.repo.TaskRepository
import com.duotech.studysmart.reminders.ReminderScheduler
import com.duotech.studysmart.util.formatEpochMillis
import com.duotech.studysmart.util.toEpochMillis
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    repo: TaskRepository,
    taskId: Long,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(taskId != 0L) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TaskType.TASK) }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }

    val nowZdt = remember {
        Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault())
    }
    var dueDate by remember { mutableStateOf(nowZdt.toLocalDate()) }
    var dueTime by remember { mutableStateOf(nowZdt.toLocalTime().withSecond(0).withNano(0).plusHours(1)) }

    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderMinutesText by remember { mutableStateOf("30") }

    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(taskId) {
        if (taskId == 0L) {
            loading = false
            return@LaunchedEffect
        }

        val existing = repo.getById(taskId)
        if (existing != null) {
            title = existing.title
            description = existing.description
            type = existing.type
            priority = existing.priority

            val zdt = Instant.ofEpochMilli(existing.dueAtMillis).atZone(ZoneId.systemDefault())
            dueDate = zdt.toLocalDate()
            dueTime = zdt.toLocalTime().withSecond(0).withNano(0)

            reminderEnabled = existing.reminderMinutesBefore != null
            reminderMinutesText = (existing.reminderMinutesBefore ?: 30).toString()
        }

        loading = false
    }

    fun pickDate() {
        DatePickerDialog(
            context,
            { _, y, m, d -> dueDate = LocalDate.of(y, m + 1, d) },
            dueDate.year,
            dueDate.monthValue - 1,
            dueDate.dayOfMonth
        ).show()
    }

    fun pickTime() {
        TimePickerDialog(
            context,
            { _, h, min -> dueTime = LocalTime.of(h, min) },
            dueTime.hour,
            dueTime.minute,
            false
        ).show()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (taskId == 0L) "Add Task" else "Edit Task") }) }
    ) { padding ->
        if (loading) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = type == TaskType.TASK,
                    onClick = { type = TaskType.TASK },
                    label = { Text("Task") }
                )
                FilterChip(
                    selected = type == TaskType.ASSESSMENT,
                    onClick = { type = TaskType.ASSESSMENT },
                    label = { Text("Assessment") }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(selected = priority == Priority.LOW, onClick = { priority = Priority.LOW }, label = { Text("Low") })
                FilterChip(selected = priority == Priority.MEDIUM, onClick = { priority = Priority.MEDIUM }, label = { Text("Medium") })
                FilterChip(selected = priority == Priority.HIGH, onClick = { priority = Priority.HIGH }, label = { Text("High") })
            }

            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Due date & time", style = MaterialTheme.typography.titleMedium)
                    Text("Currently: ${formatEpochMillis(toEpochMillis(dueDate, dueTime))}")

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { pickDate() }) { Text("Pick date") }
                        Button(onClick = { pickTime() }) { Text("Pick time") }
                    }
                }
            }

            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Reminder", style = MaterialTheme.typography.titleMedium)

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Switch(checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
                        Text(if (reminderEnabled) "On" else "Off")
                    }

                    if (reminderEnabled) {
                        OutlinedTextField(
                            value = reminderMinutesText,
                            onValueChange = { reminderMinutesText = it.filter(Char::isDigit) },
                            label = { Text("Minutes before (e.g., 30)") }
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            error = null

                            val trimmed = title.trim()
                            if (trimmed.isBlank()) {
                                error = "Title is required."
                                return@launch
                            }

                            val dueAt = toEpochMillis(dueDate, dueTime)
                            val minutesBefore =
                                if (reminderEnabled) reminderMinutesText.toIntOrNull()?.coerceAtLeast(0) else null

                            val existing = if (taskId != 0L) repo.getById(taskId) else null

                            val toSave = TaskEntity(
                                id = taskId,
                                title = trimmed,
                                description = description.trim(),
                                dueAtMillis = dueAt,
                                type = type,
                                priority = priority,
                                reminderMinutesBefore = minutesBefore,
                                isCompleted = existing?.isCompleted ?: false,
                                createdAtMillis = existing?.createdAtMillis ?: System.currentTimeMillis(),
                                updatedAtMillis = System.currentTimeMillis()
                            )

                            val savedId = repo.upsert(toSave)

                            ReminderScheduler.scheduleOrCancel(
                                context = context,
                                taskId = savedId,
                                dueAtMillis = dueAt,
                                minutesBefore = minutesBefore
                            )

                            onDone()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }

                OutlinedButton(
                    onClick = onDone,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
