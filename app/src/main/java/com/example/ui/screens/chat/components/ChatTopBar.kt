package com.example.ui.screens.chat.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.Character
import com.example.data.UserPersona

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    title: String,
    isGroupChat: Boolean,
    participantCount: Int = 0,
    character: Character? = null,
    boundPersonaId: Int?,
    userPersonas: List<UserPersona>,
    onNavigateBack: () -> Unit,
    onTitleClick: () -> Unit,
    onAddParticipantsClick: () -> Unit,
    onSelectPersona: (Int) -> Unit,
    onClearChat: () -> Unit,
    onOpenChatMemory: () -> Unit,
    onDeleteGroupChat: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var showPersonaMenu by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onTitleClick() }
            ) {
                if (!isGroupChat && character != null) {
                    val avatarColor = Color(
                        android.graphics.Color.HSVToColor(
                            floatArrayOf((character.name.hashCode() % 360).toFloat(), 0.6f, 0.9f)
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (character.avatarUri != null) {
                            AsyncImage(
                                model = android.net.Uri.parse(character.avatarUri),
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = character.name.take(1).uppercase(),
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                } else if (isGroupChat) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Group",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (isGroupChat && participantCount > 0) {
                        Text(
                            text = "$participantCount participants",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            if (isGroupChat) {
                IconButton(onClick = onAddParticipantsClick) {
                    Icon(
                        Icons.Filled.GroupAdd,
                        contentDescription = "Add Characters",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Box {
                val rotation by animateFloatAsState(
                    targetValue = if (showPersonaMenu) 180f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "PersonaIconRotation"
                )
                IconButton(onClick = { showPersonaMenu = true }) {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = "Switch Persona",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.graphicsLayer { rotationZ = rotation }
                    )
                }
                DropdownMenu(
                    expanded = showPersonaMenu,
                    onDismissRequest = { showPersonaMenu = false },
                    modifier = Modifier.animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Default User",
                                fontWeight = if (boundPersonaId == -1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onSelectPersona(-1)
                            showPersonaMenu = false
                        }
                    )
                    userPersonas.forEach { persona ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    persona.name,
                                    fontWeight = if (boundPersonaId == persona.id) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSelectPersona(persona.id)
                                showPersonaMenu = false
                            }
                        )
                    }
                }
            }

            IconButton(onClick = { showMenu = true }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "More Options",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Clear Chat") },
                    onClick = {
                        showMenu = false
                        onClearChat()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Chat Memory") },
                    onClick = {
                        showMenu = false
                        onOpenChatMemory()
                    }
                )
                if (isGroupChat && onDeleteGroupChat != null) {
                    DropdownMenuItem(
                        text = { Text("Delete Group Chat") },
                        onClick = {
                            showMenu = false
                            onDeleteGroupChat()
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
