package com.takuchan.suudoku.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsState(
    val isTimerEnabled: Boolean = true,
    val maxMistakes: Int = 3
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    fun toggleTimer(enabled: Boolean) {
        _uiState.update { it.copy(isTimerEnabled = enabled) }
    }

    fun setMaxMistakes(count: Int) {
        _uiState.update { it.copy(maxMistakes = count.coerceIn(1, 10)) }
    }
}
