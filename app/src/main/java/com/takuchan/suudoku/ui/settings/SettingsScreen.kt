package com.takuchan.suudoku.ui.settings

import com.takuchan.suudoku.logic.GameMode
import com.takuchan.suudoku.logic.Language
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val language = uiState.language

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(getSettingsTitle(language), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = getTimerTitle(language),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getTimerDesc(language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.isTimerEnabled,
                        onCheckedChange = viewModel::toggleTimer
                    )
                }
            }

            SettingsCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = getMistakesTitle(language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${getMistakesAllowed(language)}: ${uiState.maxMistakes}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = uiState.maxMistakes.toFloat(),
                        onValueChange = { viewModel.setMaxMistakes(it.toInt()) },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                }
            }

            SettingsCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = getGameModeTitle(language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = getGameModeDesc(language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GameMode.values().forEach { mode ->
                            FilterChip(
                                selected = uiState.gameMode == mode,
                                onClick = { viewModel.setGameMode(mode) },
                                label = { Text(getGameModeName(mode, language)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            SettingsCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = getLanguageTitle(language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Language.values().forEach { lang ->
                            FilterChip(
                                selected = uiState.language == lang,
                                onClick = { viewModel.setLanguage(lang) },
                                label = { Text(lang.displayName) }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getSettingsTitle(language: Language) = when(language) {
    Language.ENGLISH -> "SETTINGS"
    Language.JAPANESE -> "設定"
    Language.CHINESE -> "设置"
    Language.KOREAN -> "설정"
}

fun getTimerTitle(language: Language) = when(language) {
    Language.ENGLISH -> "Enable Timer"
    Language.JAPANESE -> "タイマーを表示"
    Language.CHINESE -> "启用计时器"
    Language.KOREAN -> "타이머 활성화"
}

fun getTimerDesc(language: Language) = when(language) {
    Language.ENGLISH -> "Track your time while solving"
    Language.JAPANESE -> "解くまでの時間を計測します"
    Language.CHINESE -> "追踪解题时间"
    Language.KOREAN -> "해결 시간을 추적합니다"
}

fun getMistakesTitle(language: Language) = when(language) {
    Language.ENGLISH -> "Maximum Mistakes"
    Language.JAPANESE -> "ミスの許容回数"
    Language.CHINESE -> "最大错误次数"
    Language.KOREAN -> "최대 실수 횟수"
}

fun getMistakesAllowed(language: Language) = when(language) {
    Language.ENGLISH -> "Allowed mistakes"
    Language.JAPANESE -> "許容されるミス"
    Language.CHINESE -> "允许的错误"
    Language.KOREAN -> "허용된 실수"
}

fun getGameModeTitle(language: Language) = when(language) {
    Language.ENGLISH -> "Game Mode"
    Language.JAPANESE -> "ゲームモード"
    Language.CHINESE -> "游戏模式"
    Language.KOREAN -> "게임 모드"
}

fun getGameModeDesc(language: Language) = when(language) {
    Language.ENGLISH -> "Choose how you want to play"
    Language.JAPANESE -> "遊び方を選択してください"
    Language.CHINESE -> "选择您的游戏方式"
    Language.KOREAN -> "플레이 방식을 선택하세요"
}

fun getGameModeName(mode: GameMode, language: Language) = when(mode) {
    GameMode.INSTANT -> when(language) {
        Language.ENGLISH -> "Instant"
        Language.JAPANESE -> "即時判定"
        Language.CHINESE -> "即时判定"
        Language.KOREAN -> "즉시 판정"
    }
    GameMode.MANUAL -> when(language) {
        Language.ENGLISH -> "Manual"
        Language.JAPANESE -> "採点モード"
        Language.CHINESE -> "手动检查"
        Language.KOREAN -> "수동 채점"
    }
}

fun getLanguageTitle(language: Language) = when(language) {
    Language.ENGLISH -> "Language"
    Language.JAPANESE -> "言語"
    Language.CHINESE -> "语言"
    Language.KOREAN -> "언어"
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        content = content
    )
}
