package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String,
    viewModel: PersonaViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("PersonaPrefs", Context.MODE_PRIVATE) }
    
    val userPersonas by viewModel.userPersonas.collectAsState()
    
    var currentUsername by remember { mutableStateOf(username) }
    var currentPfpUri by remember { mutableStateOf(sharedPrefs.getString("pfpUri", null)) }
    
    var showEditNameDialog by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf(currentUsername) }
    
    var showCreatePersonaDialog by remember { mutableStateOf(false) }
    var newPersonaName by remember { mutableStateOf("") }
    var newPersonaDesc by remember { mutableStateOf("") }
    
    var activePersonaId by remember { mutableStateOf(sharedPrefs.getInt("activePersonaId", -1)) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                currentPfpUri = uri.toString()
                sharedPrefs.edit().putString("pfpUri", uri.toString()).apply()
            }
        }
    )

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text(stringResource(id = R.string.change_username)) },
            text = {
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newUsername.isNotBlank()) {
                        currentUsername = newUsername
                        sharedPrefs.edit().putString("username", newUsername).apply()
                    }
                    showEditNameDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCreatePersonaDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePersonaDialog = false },
            title = { Text("Create Persona") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newPersonaName,
                        onValueChange = { newPersonaName = it },
                        label = { Text("Persona Name (e.g., Rover)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newPersonaDesc,
                        onValueChange = { newPersonaDesc = it },
                        label = { Text("Description/Identity") },
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newPersonaName.isNotBlank() && newPersonaDesc.isNotBlank()) {
                        viewModel.saveUserPersona(
                            name = newPersonaName,
                            description = newPersonaDesc
                        ) {}
                        newPersonaName = ""
                        newPersonaDesc = ""
                        showCreatePersonaDialog = false
                    }
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePersonaDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile_title),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreatePersonaDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, "Add Persona")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (currentPfpUri != null) {
                    AsyncImage(
                        model = currentPfpUri,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = currentUsername,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Image,
                    text = stringResource(id = R.string.set_pfp),
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
                
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Edit,
                    text = stringResource(id = R.string.change_username),
                    onClick = {
                        newUsername = currentUsername
                        showEditNameDialog = true
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "My Personas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (userPersonas.isEmpty()) {
                Text(
                    text = "No personas created yet. Create one to change who you roleplay as!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(userPersonas) { persona ->
                        val isSelected = persona.id == activePersonaId
                        ListItem(
                            headlineContent = { Text(persona.name, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(persona.description, maxLines = 2) },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSelected) {
                                        Icon(Icons.Filled.CheckCircle, contentDescription = "Active", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { viewModel.deleteUserPersona(persona.id) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = (if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant).copy(alpha = 0.85f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    activePersonaId = persona.id
                                    sharedPrefs.edit().putInt("activePersonaId", persona.id).apply()
                                }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
