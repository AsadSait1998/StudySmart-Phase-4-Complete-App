package com.duotech.studysmart.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("StudySmart", style = MaterialTheme.typography.headlineSmall)
            Text("Built by DuoTech", style = MaterialTheme.typography.titleMedium)

            Divider()

            Text("What it does:")
            Text("• Add tasks & assessments")
            Text("• Set due dates and priorities")
            Text("• Schedule reminder notifications")
            Text("• Mark completed / delete tasks")

            Divider()

            Text("Notes:")
            Text("• Notifications require permission on Android 13+")
            Text("• Reminders are scheduled with WorkManager")
        }
    }
}
