package com.example.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CafeExperience
import com.example.data.CafeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddCafeViewModel : ViewModel() {
    private val repository = CafeRepository()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    fun saveExperience(
        cafeName: String,
        location: String,
        rating: Float,
        coffeeRecommendation: String,
        priceRange: String,
        facilitiesTags: List<String>,
        notes: String
    ) {
        if (cafeName.isBlank() || location.isBlank()) {
            _saveError.value = "Cafe name and location are required."
            return
        }
        viewModelScope.launch {
            _isSaving.value = true
            _saveError.value = null
            
            val experience = CafeExperience(
                cafeName = cafeName.trim(),
                location = location.trim(),
                rating = rating,
                coffeeRecommendation = coffeeRecommendation.trim(),
                priceRange = priceRange,
                facilitiesTags = facilitiesTags,
                notes = notes.trim()
            )

            val result = repository.addExperience(experience)
            if (result.isSuccess) {
                _saveSuccess.value = true
            } else {
                _saveError.value = result.exceptionOrNull()?.message ?: "Failed to save"
            }
            _isSaving.value = false
        }
    }
}
