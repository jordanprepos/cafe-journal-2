package com.example.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
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
        val subscription = try {
            collection.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val experiences = snapshot?.documents?.mapNotNull { CafeExperience.fromDocument(it) } ?: emptyList()
                    trySend(experiences)
                }
        } catch (e: Exception) {
            trySend(emptyList())
            null
        }
        awaitClose { subscription?.remove() }
    }.catch { emit(emptyList()) }

    fun getLocations(): Flow<List<String>> = callbackFlow {
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val subscription = try {
            collection
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val experiences = snapshot?.documents?.mapNotNull { CafeExperience.fromDocument(it) } ?: emptyList()
                    val locations = experiences.map { it.location }.filter { it.isNotBlank() }.distinct()
                    trySend(locations)
                }
        } catch (e: Exception) {
            trySend(emptyList())
            null
        }
        awaitClose { subscription?.remove() }
    }.catch { emit(emptyList()) }

    suspend fun saveExperience(experience: CafeExperience): Result<Unit> {
        return try {
            if (collection == null) {
                return Result.failure(Exception("Firebase not configured. Please add google-services.json"))
            }
            if (experience.id.isBlank()) {
                collection.add(experience).await()
            } else {
                collection.document(experience.id).set(experience).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addExperience(experience: CafeExperience): Result<Unit> {
        return saveExperience(experience)
    }

    suspend fun updatePhotoCaption(experienceId: String, photoUri: String, caption: String): Result<Unit> {
        return try {
            if (collection == null) {
                return Result.failure(Exception("Firebase not configured"))
            }
            val docRef = collection.document(experienceId)
            val snapshot = docRef.get().await()
            val currentExp = CafeExperience.fromDocument(snapshot) ?: return Result.failure(Exception("Experience not found"))
            val updatedCaptions = currentExp.photoCaptions.toMutableMap()
            if (caption.isBlank()) {
                updatedCaptions.remove(photoUri)
            } else {
                updatedCaptions[photoUri] = caption.trim()
            }
            docRef.update("photoCaptions", updatedCaptions).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
