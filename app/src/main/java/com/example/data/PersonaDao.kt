package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonaDao {
    @Query("SELECT * FROM characters")
    fun getAllCharacters(): Flow<List<Character>>
    
    @Query("SELECT * FROM characters")
    suspend fun getAllCharactersSync(): List<Character>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: Character)

    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: Int): Character?

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: Int): Flow<List<Message>>
    
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    suspend fun getMessagesForChatSync(chatId: Int): List<Message>

    @Insert
    suspend fun insertMessage(message: Message)
    
    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun clearMessages(chatId: Int)

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteCharacterById(id: Int)
    
    @Query("DELETE FROM group_chats WHERE id = :id")
    suspend fun deleteGroupChatById(id: Int)
    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: Int)
    @Query("SELECT * FROM characters WHERE id IN (:ids)")
    suspend fun getCharactersByIds(ids: List<Int>): List<Character>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupChat(groupChat: GroupChat): Long

    @Query("SELECT * FROM group_chats WHERE id = :id LIMIT 1")
    suspend fun getGroupChatById(id: Int): GroupChat?

    @Query("UPDATE group_chats SET participantIds = :participantIds WHERE id = :id")
    suspend fun updateGroupChatParticipants(id: Int, participantIds: String)

    @Query("SELECT * FROM group_chats")
    fun getAllGroupChats(): Flow<List<GroupChat>>
    
    @Query("UPDATE messages SET reaction = :reaction WHERE id = :id")
    suspend fun updateMessageReaction(id: Int, reaction: String?)

    @Query("SELECT * FROM user_personas")
    fun getAllUserPersonas(): Flow<List<UserPersona>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPersona(persona: UserPersona): Long

    @Query("DELETE FROM user_personas WHERE id = :id")
    suspend fun deleteUserPersona(id: Int)

    @Query("SELECT * FROM user_personas WHERE id = :id LIMIT 1")
    suspend fun getUserPersonaById(id: Int): UserPersona?

    @Query("UPDATE messages SET isPinned = :isPinned WHERE id = :id")
    suspend fun updateMessagePinned(id: Int, isPinned: Boolean)

    @Query("SELECT * FROM chat_memory WHERE chatId = :chatId LIMIT 1")
    suspend fun getChatMemory(chatId: Int): ChatMemory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMemory(chatMemory: ChatMemory)
}
