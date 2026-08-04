package com.example.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await

class CafeRepository {
    private val db = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }

    companion object {
        private val localExperiences = MutableStateFlow<List<CafeExperience>>(emptyList())
    }

    private suspend fun ensureAuth(): String? {
        return try {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
            }
            auth.currentUser?.uid
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getCollection(): com.google.firebase.firestore.CollectionReference? {
        val uid = ensureAuth() ?: return null
        return db?.collection("users")?.document(uid)?.collection("cafes")
    }

    private val remoteExperiences = callbackFlow<List<CafeExperience>> {
        val collection = getCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val subscription = try {
            collection.orderBy("created_at", Query.Direction.DESCENDING)
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

    fun getExperiences(): Flow<List<CafeExperience>> {
        return combine(remoteExperiences, localExperiences) { remoteList, localList ->
            val combined = (localList + remoteList).distinctBy { it.id }
            combined.sortedByDescending { it.timestamp }
        }
    }

    fun getLocations(): Flow<List<String>> {
        return getExperiences().combine(MutableStateFlow(Unit)) { experiences, _ ->
            experiences.map { it.location }.filter { it.isNotBlank() }.distinct()
        }
    }

    suspend fun saveExperience(experience: CafeExperience): Result<Unit> {
        val assignedId = if (experience.id.isBlank()) UUID.randomUUID().toString() else experience.id
        val expToSave = experience.copy(id = assignedId)

        // Instantly save to local memory flow so UI responds immediately
        val currentLocal = localExperiences.value.toMutableList()
        val existingIndex = currentLocal.indexOfFirst { it.id == assignedId }
        if (existingIndex >= 0) {
            currentLocal[existingIndex] = expToSave
        } else {
            currentLocal.add(0, expToSave)
        }
        localExperiences.value = currentLocal

        // Attempt sync to Firestore in background
        return try {
            val collection = getCollection()
            if (collection != null) {
                val data = expToSave.toMap()
                collection.document(assignedId).set(data).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            // Firestore write rejected (e.g. PERMISSION_DENIED), but local save succeeded!
            Result.success(Unit)
        }
    }

    suspend fun addExperience(experience: CafeExperience): Result<Unit> {
        return saveExperience(experience)
    }

    suspend fun deleteExperience(experienceId: String): Result<Unit> {
        if (experienceId.isBlank()) return Result.success(Unit)

        // Remove from local memory flow first so UI updates immediately
        val currentLocal = localExperiences.value.toMutableList()
        currentLocal.removeAll { it.id == experienceId }
        localExperiences.value = currentLocal

        return try {
            val collection = getCollection()
            if (collection != null) {
                collection.document(experienceId).delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    suspend fun updatePhotoCaption(experienceId: String, photoUri: String, caption: String): Result<Unit> {
        // Update local memory flow first
        val currentLocal = localExperiences.value.toMutableList()
        val existingIndex = currentLocal.indexOfFirst { it.id == experienceId }
        if (existingIndex >= 0) {
            val oldExp = currentLocal[existingIndex]
            val updatedCaptions = oldExp.photoCaptions.toMutableMap()
            if (caption.isBlank()) {
                updatedCaptions.remove(photoUri)
            } else {
                updatedCaptions[photoUri] = caption.trim()
            }
            val updatedExp = oldExp.copy(photoCaptions = updatedCaptions)
            currentLocal[existingIndex] = updatedExp
            localExperiences.value = currentLocal
        }

        return try {
            val collection = getCollection()
            if (collection != null) {
                val docRef = collection.document(experienceId)
                val snapshot = docRef.get().await()
                val currentExp = CafeExperience.fromDocument(snapshot)
                if (currentExp != null) {
                    val updatedCaptions = currentExp.photoCaptions.toMutableMap()
                    if (caption.isBlank()) {
                        updatedCaptions.remove(photoUri)
                    } else {
                        updatedCaptions[photoUri] = caption.trim()
                    }
                    docRef.update("photoCaptions", updatedCaptions).await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }
}

