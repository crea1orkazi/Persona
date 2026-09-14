package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.chat.components.ChatInputBar
import com.example.ui.screens.chat.components.ChatMemoryDialog
import com.example.ui.screens.chat.components.ChatParticipantsSheet
import com.example.ui.screens.chat.components.ChatTopBar
import com.example.ui.screens.chat.components.ChoosePersonaDialog
import com.example.ui.screens.chat.components.DeleteChatConfirmDialog
import com.example.ui.screens.chat.components.MessageBubble
import com.example.ui.screens.chat.components.TypingIndicatorBubble

@Composable
fun GroupChatScreen(
    chatId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToCharacterSettings: (Int) -> Unit = {},
    onDeleteComplete: () -> Unit = {},
    viewModel: ChatViewModel = viewModel(),
    personaViewModel: PersonaViewModel = viewModel()
) {
    LaunchedEffect(chatId) {
        viewModel.setChatId(chatId)
    }

    val messages by viewModel.messages.collectAsState()
    val allCharacters by viewModel.allCharacters.collectAsState()
    val groupChat by viewModel.groupChat.collectAsState()
    val character by viewModel.character.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val boundPersonaId by viewModel.boundPersonaId.collectAsState()
    val userPersonas by personaViewModel.userPersonas.collectAsState()
    
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    
    var showAddParticipants by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showChatMemory by remember { mutableStateOf(false) }
    val chatMemoryText by viewModel.chatMemoryText.collectAsState()
    
    val isGroupChat = chatId < 0
    val title = if (isGroupChat) (groupChat?.name ?: "Group Chat") else (character?.name ?: "Chat")
    
    val currentParticipantIds = remember(groupChat) {
        groupChat?.participantIds?.split(",")?.filter { it.isNotEmpty() }?.map { it.toInt() } ?: emptyList()
    }

    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty() || isTyping) {
            listState.animateScrollToItem(0)
        }
    }

    if (boundPersonaId == null) {
        ChoosePersonaDialog(
            userPersonas = userPersonas,
            onSelectPersona = { viewModel.bindPersona(it) },
            onDismissRequest = { onNavigateBack() }
        )
    }

    if (showChatMemory) {
        ChatMemoryDialog(
            initialMemoryText = chatMemoryText,
            onSaveMemory = { viewModel.updateChatMemory(it) },
            onDismissRequest = { showChatMemory = false }
        )
    }

    if (showDeleteConfirm) {
        DeleteChatConfirmDialog(
            onConfirm = { viewModel.deleteGroupChat(onDeleteComplete) },
            onDismissRequest = { showDeleteConfirm = false }
        )
    }

    if (showAddParticipants) {
        ChatParticipantsSheet(
            allCharacters = allCharacters,
            currentParticipantIds = currentParticipantIds,
            onAddParticipant = { viewModel.addParticipantToGroupChat(chatId, it) },
            onDismissRequest = { showAddParticipants = false }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.statusBars,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopBar(
                title = title,
                isGroupChat = isGroupChat,
                participantCount = currentParticipantIds.size,
                character = character,
                boundPersonaId = boundPersonaId,
                userPersonas = userPersonas,
                onNavigateBack = onNavigateBack,
                onTitleClick = {
                    if (!isGroupChat) {
                        onNavigateToCharacterSettings(chatId)
                    }
                },
                onAddParticipantsClick = { showAddParticipants = true },
                onSelectPersona = { viewModel.bindPersona(it) },
                onClearChat = { viewModel.clearChat() },
                onOpenChatMemory = { showChatMemory = true },
                onDeleteGroupChat = if (isGroupChat) { { showDeleteConfirm = true } } else null
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .consumeWindowInsets(innerPadding)
                .imePadding()
        ) {
            if (messages.isEmpty() && !isTyping) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val avatarColor = Color(
                        android.graphics.Color.HSVToColor(
                            floatArrayOf(((character?.name ?: "Group").hashCode() % 360).toFloat(), 0.6f, 0.9f)
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (character?.name ?: "G").take(1).uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No messages yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Send a message to start the conversation.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Top,
                    reverseLayout = true
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    if (isTyping) {
                        item(key = "typing") {
                            TypingIndicatorBubble(
                                role = if (isGroupChat) "AI" else character?.name ?: "AI",
                                avatarUri = if (isGroupChat) null else character?.avatarUri,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                    val reversedMessages = messages.reversed()
                    itemsIndexed(reversedMessages, key = { _, msg -> msg.id }) { index, message ->
                        val senderCharacter = if (isGroupChat) {
                            allCharacters.find { it.name.equals(message.role, ignoreCase = true) }
                        } else {
                            character
                        }
                        
                        val displayRole = if (!isGroupChat && message.role != "user" && message.role != "System") {
                            character?.name ?: message.role
                        } else {
                            message.role
                        }

                        val isPrevSameSender = (index + 1 < reversedMessages.size) && (reversedMessages[index + 1].role == message.role)
                        val isNextSameSender = (index - 1 >= 0) && (reversedMessages[index - 1].role == message.role)
                        
                        MessageBubble(
                            messageId = message.id,
                            role = displayRole,
                            content = message.content,
                            isUser = message.role == "user",
                            isPrevSameSender = isPrevSameSender,
                            isNextSameSender = isNextSameSender,
                            timestamp = message.timestamp,
                            reaction = message.reaction,
                            isPinned = message.isPinned,
                            avatarUri = senderCharacter?.avatarUri,
                            isLatest = index == 0,
                            onRegenerate = { viewModel.regenerateLastMessage() },
                            onEdit = { newText -> viewModel.editMessage(message.id, newText) },
                            onDelete = { viewModel.deleteMessage(message.id) },
                            onTogglePin = { viewModel.toggleMessagePin(message.id, message.isPinned) },
                            onReaction = { reaction -> viewModel.setReaction(message.id, reaction) },
                            modifier = Modifier.animateItem()
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
            
            ChatInputBar(
                input = input,
                onInputChange = { input = it },
                onSendMessage = {
                    viewModel.sendMessage(it)
                    input = ""
                },
                isTyping = isTyping,
                activePersonaName = userPersonas.find { it.id == boundPersonaId }?.name
            )
        }
    }
}
