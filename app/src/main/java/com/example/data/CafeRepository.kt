package com.example.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CafeRepository {
    private val db = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
    private val collection = db?.collection("cafe_experiences")

    fun getExperiences(): Flow<List<CafeExperience>> = callbackFlow {
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val subscription = collection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val experiences = snapshot?.toObjects(CafeExperience::class.java) ?: emptyList()
                trySend(experiences)
            }
        awaitClose { subscription.remove() }
    }

    fun getLocations(): Flow<List<String>> = callbackFlow {
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val subscription = collection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val experiences = snapshot?.toObjects(CafeExperience::class.java) ?: emptyList()
                val locations = experiences.map { it.location }.filter { it.isNotBlank() }.distinct()
                trySend(locations)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addExperience(experience: CafeExperience): Result<Unit> {
        return try {
            if (collection == null) {
                return Result.failure(Exception("Firebase not configured. Please add google-services.json"))
            }
            collection.add(experience).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
