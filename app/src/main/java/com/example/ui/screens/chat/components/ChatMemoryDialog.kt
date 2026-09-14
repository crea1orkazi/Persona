package com.example.ui.screens.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.UserPersona

@Composable
fun ChatMemoryDialog(
    initialMemoryText: String,
    onSaveMemory: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var editingMemoryText by remember(initialMemoryText) { mutableStateOf(initialMemoryText) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Chat Memory") },
        text = {
            Column {
                Text(
                    "Add details, plot points, or rules here that you want the AI to never forget. This is constantly injected into their memory.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = editingMemoryText,
                    onValueChange = { editingMemoryText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    placeholder = { Text("Example: The user and character are currently stranded on an island.") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSaveMemory(editingMemoryText)
                onDismissRequest()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ChoosePersonaDialog(
    userPersonas: List<UserPersona>,
    onSelectPersona: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Choose your Persona") },
        text = {
            Column {
                Text(
                    "Select a persona to roleplay as. It will be permanently bound to this chat.",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = true
                ) {
                    item {
                        ListItem(
                            headlineContent = { Text("Default User", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.clickable { onSelectPersona(-1) },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                            )
                        )
                    }
                    items(userPersonas) { persona ->
                        ListItem(
                            headlineContent = { Text(persona.name, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(persona.description, maxLines = 2) },
                            modifier = Modifier.clickable { onSelectPersona(persona.id) },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun DeleteChatConfirmDialog(
    title: String = "Delete Group Chat",
    message: String = "Are you sure you want to delete this group chat? This action cannot be undone.",
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                onConfirm()
            }) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
