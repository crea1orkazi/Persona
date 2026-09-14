package com.example.ui.screens
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.CameraAlt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.api.generateCharacterDetails
import kotlinx.coroutines.launch

import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSettingsScreen(
    characterId: Int,
    onNavigateBack: () -> Unit,
    onDeleteComplete: () -> Unit = {},
    viewModel: PersonaViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var traits by remember { mutableStateOf("") }
    var greeting by remember { mutableStateOf("") }
    var narration by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var allowNarration by remember { mutableStateOf(false) }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    val availableTags = listOf(
        "Anime", "Romance", "Historical", "Vampire", "Fantasy", "Magical", 
        "Assistant", "BFF", "Enemy to Lover", "Roommate", "CEO", 
        "Yandere", "Tsundere", "Sci-Fi", "Mystery"
    )
    var customTags by remember { mutableStateOf(listOf<String>()) }
    var customTagInput by remember { mutableStateOf("") }
    val allTags = availableTags + customTags
    var isGenerating by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    val characters by viewModel.characters.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> if (uri != null) avatarUri = uri }
    )

    LaunchedEffect(characterId, characters) {
        val character = characters.find { it.id == characterId }
        if (character != null && name.isEmpty() && greeting.isEmpty()) {
            name = character.name
            traits = character.traits
            greeting = character.greeting
            narration = character.narration
            allowNarration = character.allowNarration
            avatarUri = character.avatarUri?.let { Uri.parse(it) }
            val tags = character.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            selectedTags = tags.toSet()
            customTags = tags.filter { !availableTags.contains(it) }
        }
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Character",
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
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Character",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    ) { innerPadding ->
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete Character") },
                text = { Text("Are you sure you want to delete this character? This will also permanently delete all chats with them.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteCharacter(characterId) {
                            onDeleteComplete()
                        }
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.character_description_label)) },
                placeholder = { Text(stringResource(id = R.string.character_description_hint)) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                shape = MaterialTheme.shapes.medium,
                maxLines = 4
            )
            
            Button(
                onClick = { 
                    if (description.isNotBlank()) {
                        isGenerating = true
                        coroutineScope.launch {
                            val result = generateCharacterDetails(description)
                            
                            val nameMatch = Regex("Name:\\s*(.*)").find(result)
                            val traitsMatch = Regex("Traits:\\s*(.*)").find(result)
                            val greetingMatch = Regex("Greeting:\\s*(.*)").find(result)
                            val narrationMatch = Regex("Narration:\\s*(.*)").find(result)
                            
                            if (nameMatch != null) name = nameMatch.groupValues[1].trim()
                            if (traitsMatch != null) traits = traitsMatch.groupValues[1].trim()
                            if (greetingMatch != null) greeting = greetingMatch.groupValues[1].trim()
                            if (narrationMatch != null) narration = narrationMatch.groupValues[1].trim()
                            
                            isGenerating = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                enabled = !isGenerating && description.isNotBlank()
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onSecondary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(id = R.string.generate_ai_btn), fontWeight = FontWeight.Bold)
                }
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUri != null) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Select Avatar",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.character_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            OutlinedTextField(
                value = traits,
                onValueChange = { traits = it },
                label = { Text(stringResource(id = R.string.character_traits_label)) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                shape = MaterialTheme.shapes.medium,
                maxLines = 4
            )

            OutlinedTextField(
                value = greeting,
                onValueChange = { greeting = it },
                label = { Text(stringResource(id = R.string.character_greeting_label)) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                shape = MaterialTheme.shapes.medium,
                maxLines = 4
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.enable_narration_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(id = R.string.enable_narration_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = allowNarration,
                        onCheckedChange = { allowNarration = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
            
            if (allowNarration) {
                OutlinedTextField(
                    value = narration,
                    onValueChange = { narration = it },
                    label = { Text(stringResource(id = R.string.character_narration_label)) },
                    placeholder = { Text(stringResource(id = R.string.character_narration_hint)) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                    ),
                    shape = MaterialTheme.shapes.medium,
                    maxLines = 4
                )
            }
            
            Text("Tags", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = customTagInput,
                    onValueChange = { customTagInput = it },
                    placeholder = { Text("Add custom tag...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                    ),
                    shape = MaterialTheme.shapes.small
                )
                Button(
                    onClick = {
                        val newTag = customTagInput.trim()
                        if (newTag.isNotEmpty() && !allTags.contains(newTag)) {
                            customTags = customTags + newTag
                            selectedTags = selectedTags + newTag
                            customTagInput = ""
                        } else if (allTags.contains(newTag)) {
                            selectedTags = selectedTags + newTag
                            customTagInput = ""
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text("Add", fontWeight = FontWeight.Bold)
                }
            }

            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allTags) { tag ->
                    FilterChip(
                        selected = selectedTags.contains(tag),
                        onClick = {
                            if (selectedTags.contains(tag)) {
                                selectedTags = selectedTags - tag
                            } else {
                                selectedTags = selectedTags + tag
                            }
                        },
                        label = { Text(tag) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f, fill = false))

            Button(
                onClick = { 
                    viewModel.saveCharacter(
                        id = characterId,
                        name = name,
                        traits = traits,
                        greeting = greeting,
                        narration = narration,
                        allowNarration = allowNarration,
                        tags = selectedTags.joinToString(","),
                        avatarUri = avatarUri?.toString(),
                        onComplete = onNavigateBack
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = name.isNotBlank() && greeting.isNotBlank()
            ) {
                Text(
                    text = stringResource(id = R.string.save_btn),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
