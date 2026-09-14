package com.example.ui.screens.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Character

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatParticipantsSheet(
    allCharacters: List<Character>,
    currentParticipantIds: List<Int>,
    maxParticipants: Int = 20,
    onAddParticipant: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Add Characters (${currentParticipantIds.size}/$maxParticipants)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(allCharacters) { character ->
                    val isSelected = currentParticipantIds.contains(character.id)
                    ListItem(
                        headlineContent = { Text(character.name) },
                        supportingContent = { Text(character.traits, maxLines = 1) },
                        trailingContent = {
                            if (isSelected) {
                                Icon(Icons.Filled.Check, contentDescription = "Added", tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (!isSelected && currentParticipantIds.size < maxParticipants) {
                                    onAddParticipant(character.id)
                                }
                            }
                    )
                }
            }
        }
    }
}
