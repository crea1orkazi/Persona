package com.example.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatInputBar(
    input: String,
    onInputChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    isTyping: Boolean,
    modifier: Modifier = Modifier,
    activePersonaName: String? = null
) {
    val inputShape = RoundedCornerShape(28.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                placeholder = { 
                    Text(
                        if (!activePersonaName.isNullOrBlank()) "Message as $activePersonaName..." 
                        else "Type a message..."
                    ) 
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = inputShape,
                maxLines = 5,
                minLines = 1,
                trailingIcon = if (isTyping || input.isNotBlank()) {
                    {
                        Row(
                            modifier = Modifier.padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isTyping) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(2.dp))
                                )
                            } else if (input.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        if (input.isNotBlank()) {
                                            onSendMessage(input)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else null
            )
        }
    }
}
