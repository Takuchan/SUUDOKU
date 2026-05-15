package com.takuchan.suudoku.logic

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private object PreferencesKeys {
        val IS_TIMER_ENABLED = booleanPreferencesKey("is_timer_enabled")
        val MAX_MISTAKES = intPreferencesKey("max_mistakes")
        val GAME_MODE = stringPreferencesKey("game_mode")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val settingsFlow: Flow<SettingsData> = context.dataStore.data
        .map { preferences ->
            SettingsData(
                isTimerEnabled = preferences[PreferencesKeys.IS_TIMER_ENABLED] ?: true,
                maxMistakes = preferences[PreferencesKeys.MAX_MISTAKES] ?: 3,
                gameMode = GameMode.valueOf(preferences[PreferencesKeys.GAME_MODE] ?: GameMode.INSTANT.name),
                language = Language.valueOf(preferences[PreferencesKeys.LANGUAGE] ?: Language.JAPANESE.name)
            )
        }

    suspend fun updateTimerEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_TIMER_ENABLED] = enabled
        }
    }

    suspend fun updateMaxMistakes(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MAX_MISTAKES] = count
        }
    }

    suspend fun updateGameMode(mode: GameMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GAME_MODE] = mode.name
        }
    }

    suspend fun updateLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language.name
        }
    }
}

data class SettingsData(
    val isTimerEnabled: Boolean,
    val maxMistakes: Int,
    val gameMode: GameMode,
    val language: Language
)
