package com.example.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CafeExperience
import com.example.data.CafeRepository
import com.example.data.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CafeRepository()
    private val themeRepository = ThemeRepository(application)

    val experiences: StateFlow<List<CafeExperience>> = repository.getExperiences()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val journalView: StateFlow<String> = themeRepository.journalView
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "grid"
        )

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    fun setJournalView(view: String) {
        viewModelScope.launch {
            themeRepository.setJournalView(view)
        }
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }
}
