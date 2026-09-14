package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val traits: String,
    val greeting: String,
    val narration: String,
    val allowNarration: Boolean,
    val tags: String = "", // comma-separated tags
    val avatarUri: String? = null
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatId: Int = 0, // negative ID means group chat
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reaction: String? = null,
    val isPinned: Boolean = false
)

@Entity(tableName = "group_chats")
data class GroupChat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "Group Chat",
    val participantIds: String // comma-separated
)

@Entity(tableName = "user_personas")
data class UserPersona(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val avatarUri: String? = null
)

@Entity(tableName = "chat_memory")
data class ChatMemory(
    @PrimaryKey val chatId: Int,
    val memoryText: String
)
