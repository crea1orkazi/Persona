package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.R
import com.example.data.Character
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    onNavigateToCreateGroupChat: () -> Unit = {},
    onNavigateToChat: (Int) -> Unit = {},
    viewModel: PersonaViewModel = viewModel()
) {
    val characters by viewModel.characters.collectAsState()
    var showCreateOptions by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("PersonaPrefs", Context.MODE_PRIVATE) }
    val pfpUriString = remember { sharedPrefs.getString("pfpUri", null) }

    val filteredCharacters = remember(characters, searchQuery) {
        if (searchQuery.isBlank()) {
            characters
        } else {
            val query = searchQuery.trim().lowercase()
            characters.filter { char ->
                char.name.lowercase().contains(query) ||
                char.greeting.lowercase().contains(query) ||
                char.tags.lowercase().contains(query)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.widthIn(max = 320.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                // Header with App Icon and below that App Name
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 3.dp,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = stringResource(id = R.string.app_name),
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "AI Character & Persona Studio",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Characters & Chats", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
                    selected = true,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Create Character") },
                    icon = { Icon(Icons.Filled.PersonAdd, contentDescription = null) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            onNavigateToCreate()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Create Group Chat") },
                    icon = { Icon(Icons.Filled.GroupAdd, contentDescription = null) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            onNavigateToCreateGroupChat()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            onNavigateToProfile()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Settings") },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            onNavigateToSettings()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Version 1.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 24.dp, bottom = 24.dp)
                )
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Left side: Hamburger menu icon that opens drawer
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("hamburger_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open navigation drawer",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // 2. Middle: Search bar with hint "Search"
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("search_bar_input")
                                )
                            }
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // 3. Right side: Placeholder profile pic
                        IconButton(
                            onClick = onNavigateToProfile,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("profile_button")
                        ) {
                            if (pfpUriString != null) {
                                AsyncImage(
                                    model = Uri.parse(pfpUriString),
                                    contentDescription = "Profile",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 1.5.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        )
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .border(
                                            width = 1.5.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = "Profile picture placeholder",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateOptions = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        Icons.Filled.Add, 
                        contentDescription = stringResource(id = R.string.add_character_desc)
                    )
                }
            }
        ) { innerPadding ->
            if (characters.isEmpty()) {
                EmptyCharactersScreen(modifier = Modifier.padding(innerPadding))
            } else if (filteredCharacters.isEmpty()) {
                EmptySearchScreen(
                    query = searchQuery,
                    onClearSearch = { searchQuery = "" },
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                CharacterList(
                    characters = filteredCharacters, 
                    onCharacterClick = onNavigateToChat,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
    
    if (showCreateOptions) {
        ModalBottomSheet(onDismissRequest = { showCreateOptions = false }) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                ListItem(
                    headlineContent = { Text("Create Character", fontWeight = FontWeight.Bold) },
                    leadingContent = { Icon(Icons.Filled.PersonAdd, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.clickable {
                        showCreateOptions = false
                        onNavigateToCreate()
                    }
                )
                ListItem(
                    headlineContent = { Text("Create Group Chat", fontWeight = FontWeight.Bold) },
                    leadingContent = { Icon(Icons.Filled.GroupAdd, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.clickable {
                        showCreateOptions = false
                        onNavigateToCreateGroupChat()
                    }
                )
            }
        }
    }
}

@Composable
fun EmptySearchScreen(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No characters found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "No matches found for \"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClearSearch) {
            Text("Clear search")
        }
    }
}

@Composable
fun CharacterList(
    characters: List<Character>, 
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        items(characters) { character ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onCharacterClick(character.id) },
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val avatarColor = androidx.compose.ui.graphics.Color(
                        android.graphics.Color.HSVToColor(floatArrayOf((character.name.hashCode() % 360).toFloat(), 0.6f, 0.9f))
                    )
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (character.avatarUri != null) {
                            coil.compose.AsyncImage(
                                model = android.net.Uri.parse(character.avatarUri),
                                contentDescription = "Avatar",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = character.name.take(1).uppercase(),
                                color = androidx.compose.ui.graphics.Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = character.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = character.greeting.take(60) + if(character.greeting.length > 60) "..." else "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        val tags = character.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        if (tags.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            androidx.compose.foundation.lazy.LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(tags) { tag ->
                                    androidx.compose.material3.Surface(
                                        shape = MaterialTheme.shapes.small,
                                        color = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text(
                                            text = tag,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun EmptyCharactersScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Welcome to Persona",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Create your first AI character or a group chat to start an endless conversation.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
