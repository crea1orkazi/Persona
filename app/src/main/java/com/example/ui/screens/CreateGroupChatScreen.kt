package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupChatScreen(
    onNavigateBack: () -> Unit,
    onGroupChatCreated: (Int) -> Unit,
    viewModel: PersonaViewModel = viewModel()
) {
    val characters by viewModel.characters.collectAsState()
    val selectedCharacterIds = remember { mutableStateListOf<Int>() }
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Select Characters (${selectedCharacterIds.size}/20)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (selectedCharacterIds.isNotEmpty()) {
                                coroutineScope.launch {
                                    val newChatId = viewModel.createGroupChat(selectedCharacterIds.toList())
                                    onGroupChatCreated(newChatId)
                                }
                            }
                        },
                        enabled = selectedCharacterIds.isNotEmpty() && selectedCharacterIds.size <= 20
                    ) {
                        Text("Start", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(characters) { character ->
                val isSelected = selectedCharacterIds.contains(character.id)
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        if (isSelected) {
                            selectedCharacterIds.remove(character.id)
                        } else if (selectedCharacterIds.size < 20) {
                            selectedCharacterIds.add(character.id)
                        }
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = character.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        if (isSelected) {
                            Icon(Icons.Filled.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
