package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("theme_prefs")

class ThemeRepository(private val context: Context) {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    val isDarkMode: Flow<Boolean?> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY]
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = isDark
        }
    }
}
