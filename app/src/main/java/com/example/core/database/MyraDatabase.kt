package com.example.core.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val category: String, // Preferences, People, Dates, General
    val isPrivate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "prime_contacts")
data class PrimeContactEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val phoneNumber: String,
    val nickname: String,
    val relationship: String,
    val priority: String,
    val customGreeting: String,
    val isVip: Boolean = true
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val timeMillis: Long,
    val timeDisplay: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "automations")
data class AutomationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val triggerType: String,
    val condition: String,
    val actionType: String,
    val isEnabled: Boolean = true
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val userId: String,
    val role: String, // "user" or "myra"
    val text: String,
    val emotion: String = "Neutral",
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface MyraDao {
    // Memories
    @Query("SELECT * FROM memories WHERE isPrivate = 0 ORDER BY createdAt DESC")
    fun getPublicMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isPrivate = 1 ORDER BY createdAt DESC")
    fun getPrivateMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: String)

    @Query("DELETE FROM memories")
    suspend fun deleteAllMemories()

    // Prime Contacts
    @Query("SELECT * FROM prime_contacts ORDER BY name ASC")
    fun getContacts(): Flow<List<PrimeContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: PrimeContactEntity)

    @Query("DELETE FROM prime_contacts WHERE id = :id")
    suspend fun deleteContact(id: String)

    // Reminders
    @Query("SELECT * FROM reminders ORDER BY timeMillis ASC")
    fun getReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: String)

    @Query("UPDATE reminders SET isCompleted = :completed WHERE id = :id")
    suspend fun setReminderCompleted(id: String, completed: Boolean)

    // Automations
    @Query("SELECT * FROM automations")
    fun getAutomations(): Flow<List<AutomationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomation(automation: AutomationEntity)

    @Query("DELETE FROM automations WHERE id = :id")
    suspend fun deleteAutomation(id: String)

    // Chat History
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()
}

@Database(
    entities = [
        MemoryEntity::class,
        PrimeContactEntity::class,
        ReminderEntity::class,
        AutomationEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MyraDatabase : RoomDatabase() {
    abstract fun myraDao(): MyraDao

    companion object {
        @Volatile
        private var INSTANCE: MyraDatabase? = null

        fun getDatabase(context: Context): MyraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyraDatabase::class.java,
                    "myra_assistant.db"
                ).fallbackToDestructiveMigration(false)
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
