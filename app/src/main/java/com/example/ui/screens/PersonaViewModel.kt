package com.example.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Character
import com.example.data.PersonaDatabase
import com.example.data.PersonaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PersonaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PersonaRepository

    init {
        val personaDao = PersonaDatabase.getDatabase(application).personaDao()
        repository = PersonaRepository(personaDao)
        
        viewModelScope.launch {
            if (repository.getCharactersSync().isEmpty()) {
                val sampleCharacter = Character(
                    name = "Alice",
                    traits = "Helpful, cheerful, knowledgeable, polite",
                    greeting = "Hello there! I'm Alice. How can I help you shape our story today?",
                    narration = "A bright, sunny room filled with floating holographic books.",
                    allowNarration = true,
                    tags = "Assistant,Friendly"
                )
                repository.insertCharacter(sampleCharacter)
                
                if (repository.getMessagesForChatSync(0).isEmpty()) {
                    repository.insertMessage(
                        com.example.data.Message(
                            chatId = 0,
                            role = sampleCharacter.name,
                            content = sampleCharacter.greeting
                        )
                    )
                }
            }
        }
    }

    val characters = repository.allCharacters.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val userPersonas = repository.allUserPersonas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveUserPersona(id: Int = 0, name: String, description: String, avatarUri: String? = null, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertUserPersona(
                com.example.data.UserPersona(
                    id = id,
                    name = name,
                    description = description,
                    avatarUri = avatarUri
                )
            )
            onComplete()
        }
    }

    fun deleteUserPersona(id: Int) {
        viewModelScope.launch {
            repository.deleteUserPersona(id)
        }
    }

    fun saveCharacter(id: Int = 0, name: String, traits: String, greeting: String, narration: String, allowNarration: Boolean, tags: String, avatarUri: String? = null, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertCharacter(
                Character(
                    id = id,
                    name = name,
                    traits = traits,
                    greeting = greeting,
                    narration = narration,
                    allowNarration = allowNarration,
                    tags = tags,
                    avatarUri = avatarUri
                )
            )
            onComplete()
        }
    }

    suspend fun createGroupChat(participantIds: List<Int>): Int {
        val groupChat = com.example.data.GroupChat(
            participantIds = participantIds.joinToString(",")
        )
        val id = repository.insertGroupChat(groupChat)
        return -id.toInt() // Return negative ID for Group Chat
    }

    fun deleteCharacter(id: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCharacter(id)
            onComplete()
        }
    }
}
