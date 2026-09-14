package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Character::class, Message::class, GroupChat::class, UserPersona::class, ChatMemory::class], version = 8, exportSchema = false)
abstract class PersonaDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao

    companion object {
        @Volatile
        private var INSTANCE: PersonaDatabase? = null
        
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE messages ADD COLUMN reaction TEXT DEFAULT NULL")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `user_personas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `avatarUri` TEXT)")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE messages ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
                db.execSQL("CREATE TABLE IF NOT EXISTS `chat_memory` (`chatId` INTEGER PRIMARY KEY NOT NULL, `memoryText` TEXT NOT NULL)")
            }
        }

        fun getDatabase(context: Context): PersonaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PersonaDatabase::class.java,
                    "persona_database"
                )
                .addMigrations(MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class PersonaRepository(private val dao: PersonaDao) {
    val allCharacters = dao.getAllCharacters()
    
    fun getMessagesForChat(chatId: Int) = dao.getMessagesForChat(chatId)
    
    suspend fun getCharactersSync() = dao.getAllCharactersSync()
    suspend fun getCharacterByIdSync(id: Int) = dao.getCharacterById(id)
    suspend fun getCharactersByIdsSync(ids: List<Int>) = dao.getCharactersByIds(ids)
    suspend fun getMessagesForChatSync(chatId: Int) = dao.getMessagesForChatSync(chatId)
    
    suspend fun getGroupChatByIdSync(id: Int) = dao.getGroupChatById(id)
    suspend fun insertGroupChat(groupChat: GroupChat) = dao.insertGroupChat(groupChat)
    suspend fun updateGroupChatParticipants(id: Int, participantIds: String) = dao.updateGroupChatParticipants(id, participantIds)

    suspend fun insertCharacter(character: Character) = dao.insertCharacter(character)
    suspend fun insertMessage(message: Message) = dao.insertMessage(message)
    suspend fun clearMessages(chatId: Int) = dao.clearMessages(chatId)
    suspend fun deleteMessageById(id: Int) = dao.deleteMessageById(id)
    suspend fun updateMessageReaction(id: Int, reaction: String?) = dao.updateMessageReaction(id, reaction)
    suspend fun deleteCharacter(id: Int) {
        dao.deleteCharacterById(id)
        dao.clearMessages(id) // single chats have positive character id
    }
    suspend fun deleteGroupChat(id: Int) {
        dao.deleteGroupChatById(id)
        dao.clearMessages(-id) // group chats have negative ids in messages
    }
    
    val allUserPersonas = dao.getAllUserPersonas()
    suspend fun insertUserPersona(persona: UserPersona) = dao.insertUserPersona(persona)
    suspend fun deleteUserPersona(id: Int) = dao.deleteUserPersona(id)
    suspend fun getUserPersonaByIdSync(id: Int) = dao.getUserPersonaById(id)

    suspend fun updateMessagePinned(id: Int, isPinned: Boolean) = dao.updateMessagePinned(id, isPinned)
    suspend fun getChatMemorySync(chatId: Int) = dao.getChatMemory(chatId)
    suspend fun insertChatMemory(chatMemory: ChatMemory) = dao.insertChatMemory(chatMemory)
}
