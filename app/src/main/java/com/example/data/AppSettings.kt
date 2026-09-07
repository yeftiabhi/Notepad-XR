package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class AppSettings(private val context: Context) {
    private val USER_ID_KEY = intPreferencesKey("user_id")
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    val currentUserId: Flow<Int?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY]
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY] ?: true // default to dark mode for "XTREME" app
    }

    suspend fun setUserId(userId: Int?) {
        context.dataStore.edit { prefs ->
            if (userId == null) {
                prefs.remove(USER_ID_KEY)
            } else {
                prefs[USER_ID_KEY] = userId
            }
        }
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = isDark
        }
    }
}
