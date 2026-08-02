package com.example.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ThemeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val themeRepository: ThemeRepository) : ViewModel() {

    val isDarkMode: StateFlow<Boolean?> = themeRepository.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val journalView: StateFlow<String> = themeRepository.journalView
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "grid"
        )

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            themeRepository.setDarkMode(isDark)
        }
    }
}

class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(ThemeRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
