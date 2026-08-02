package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("theme_prefs")

class ThemeRepository(private val context: Context) {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    private val JOURNAL_VIEW_KEY = stringPreferencesKey("journal_view")

    val isDarkMode: Flow<Boolean?> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY]
    }

    val journalView: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[JOURNAL_VIEW_KEY] ?: "grid"
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = isDark
        }
    }

    suspend fun setJournalView(view: String) {
        context.dataStore.edit { prefs ->
            prefs[JOURNAL_VIEW_KEY] = view
        }
    }
}
