package com.example.core.database

import android.content.Context
import android.util.Log
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val db: FirebaseFirestore) {
    private val tag = "FirestoreRepository"
    private val auth: FirebaseAuth = Firebase.auth

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid ?: error("User must be authenticated for cloud operation")
    }

    suspend fun syncMemoryToCloud(memory: MemoryEntity) {
        val uid = auth.currentUser?.uid ?: return
        try {
            val map = hashMapOf(
                "id" to memory.id,
                "userId" to uid,
                "title" to memory.title,
                "content" to memory.content,
                "category" to memory.category,
                "isPrivate" to memory.isPrivate,
                "createdAt" to memory.createdAt.toString()
            )
            db.collection("users").document(uid)
                .collection("memories").document(memory.id)
                .set(map).await()
            Log.d(tag, "Memory successfully synced to Firestore")
        } catch (e: Exception) {
            Log.w(tag, "Failed to sync memory to Firestore", e)
        }
    }

    suspend fun syncContactToCloud(contact: PrimeContactEntity) {
        val uid = auth.currentUser?.uid ?: return
        try {
            val map = hashMapOf(
                "id" to contact.id,
                "userId" to uid,
                "name" to contact.name,
                "phoneNumber" to contact.phoneNumber,
                "nickname" to contact.nickname,
                "relationship" to contact.relationship,
                "priority" to contact.priority,
                "customGreeting" to contact.customGreeting,
                "isVip" to contact.isVip
            )
            db.collection("users").document(uid)
                .collection("contacts").document(contact.id)
                .set(map).await()
        } catch (e: Exception) {
            Log.w(tag, "Failed to sync contact to Firestore", e)
        }
    }

    suspend fun syncReminderToCloud(reminder: ReminderEntity) {
        val uid = auth.currentUser?.uid ?: return
        try {
            val map = hashMapOf(
                "id" to reminder.id,
                "userId" to uid,
                "title" to reminder.title,
                "timeMillis" to reminder.timeMillis,
                "timeDisplay" to reminder.timeDisplay,
                "isCompleted" to reminder.isCompleted,
                "createdAt" to reminder.createdAt.toString()
            )
            db.collection("users").document(uid)
                .collection("reminders").document(reminder.id)
                .set(map).await()
        } catch (e: Exception) {
            Log.w(tag, "Failed to sync reminder to Firestore", e)
        }
    }
}
