package com.takuchan.suudoku.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takuchan.suudoku.logic.GameMode
import com.takuchan.suudoku.logic.Language
import com.takuchan.suudoku.logic.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsState(
    val isTimerEnabled: Boolean = true,
    val maxMistakes: Int = 3,
    val gameMode: GameMode = GameMode.INSTANT,
    val language: Language = Language.JAPANESE
)

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.settingsFlow.collect { settings ->
                _uiState.update {
                    it.copy(
                        isTimerEnabled = settings.isTimerEnabled,
                        maxMistakes = settings.maxMistakes,
                        gameMode = settings.gameMode,
                        language = settings.language
                    )
                }
            }
        }
    }

    fun toggleTimer(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateTimerEnabled(enabled)
        }
    }

    fun setMaxMistakes(count: Int) {
        viewModelScope.launch {
            repository.updateMaxMistakes(count.coerceIn(1, 10))
        }
    }

    fun setGameMode(mode: GameMode) {
        viewModelScope.launch {
            repository.updateGameMode(mode)
        }
    }

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            repository.updateLanguage(language)
        }
    }
}
