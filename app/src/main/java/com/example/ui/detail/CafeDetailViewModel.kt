package com.example.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CafeExperience
import com.example.data.CafeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CafeDetailViewModel(private val cafeId: String) : ViewModel() {
    private val repository = CafeRepository()

    val experience: StateFlow<CafeExperience?> = repository.getExperiences()
        .map { experiences -> experiences.find { it.id == cafeId } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updatePhotoCaption(photoUri: String, caption: String) {
        viewModelScope.launch {
            repository.updatePhotoCaption(cafeId, photoUri, caption)
        }
    }
}

class CafeDetailViewModelFactory(private val cafeId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CafeDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CafeDetailViewModel(cafeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
