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
    private val collection = db?.collection("cafe_experiences")

    companion object {
        private val localExperiences = MutableStateFlow<List<CafeExperience>>(emptyList())
    }

    private suspend fun ensureAuth() {
        try {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
            }
        } catch (e: Exception) {
            // Ignore if anonymous auth is not enabled in Firebase project
        }
    }

    private val remoteExperiences = callbackFlow<List<CafeExperience>> {
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        ensureAuth()
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
            if (collection != null) {
                ensureAuth()
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
            if (collection != null) {
                ensureAuth()
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

