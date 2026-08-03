package com.example.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CafeExperience
import com.example.data.CafeRating
import com.example.data.CafeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp

class AddCafeViewModel : ViewModel() {
    private val repository = CafeRepository()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _loadedExperience = MutableStateFlow<CafeExperience?>(null)
    val loadedExperience: StateFlow<CafeExperience?> = _loadedExperience.asStateFlow()

    fun loadExperience(cafeId: String) {
        if (cafeId.isBlank()) return
        viewModelScope.launch {
            try {
                repository.getExperiences().collect { list ->
                    val found = list.find { it.id == cafeId }
                    if (found != null) {
                        _loadedExperience.value = found
                    }
                }
            } catch (e: Exception) {
                // Graceful fallback on error
            }
        }
    }

    fun saveExperience(
        id: String = "",
        cafeName: String,
        location: String,
        rating: CafeRating,
        coffeeRecommendation: String,
        priceRange: String,
        facilitiesTags: List<String>,
        notes: String,
        photoUri: String = "",
        timestamp: Timestamp? = null
    ) {
        if (cafeName.isBlank() || location.isBlank()) {
            _saveError.value = "Cafe name and area are required."
            return
        }
        viewModelScope.launch {
            _isSaving.value = true
            _saveError.value = null
            
            val experience = CafeExperience(
                id = id,
                cafeName = cafeName.trim(),
                location = location.trim(),
                rating = rating,
                coffeeRecommendation = coffeeRecommendation.trim(),
                priceRange = priceRange.trim(),
                facilitiesTags = facilitiesTags,
                notes = notes.trim(),
                photoUri = photoUri.trim(),
                timestamp = timestamp ?: Timestamp.now()
            )

            val result = repository.saveExperience(experience)
            if (result.isSuccess) {
                _saveSuccess.value = true
            } else {
                _saveError.value = result.exceptionOrNull()?.message ?: "Failed to save"
            }
            _isSaving.value = false
        }
    }
}
