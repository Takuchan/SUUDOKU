package com.takuchan.suudoku.ui.game

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.takuchan.suudoku.logic.GameMode
import com.takuchan.suudoku.logic.Language
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.takuchan.suudoku.logic.Difficulty
import com.takuchan.suudoku.logic.GameStatus
import com.takuchan.suudoku.logic.SudokuBoard
import com.takuchan.suudoku.logic.SudokuCell
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SUUDOKU", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetGame() }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Reset Game")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GameHud(
                timerSeconds = uiState.timerSeconds,
                mistakes = uiState.mistakes,
                maxMistakes = uiState.maxMistakes,
                difficulty = uiState.difficulty,
                isTimerEnabled = uiState.isTimerEnabled,
                language = uiState.language,
                gameMode = uiState.gameMode
            )

            SudokuGrid(
                board = uiState.board,
                selectedCell = uiState.selectedCell,
                onCellClick = viewModel::onCellSelected
            )

            if (uiState.gameMode == GameMode.MANUAL) {
                Button(
                    onClick = { viewModel.submitBoard() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(getSubmitText(uiState.language), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            NumberPad(
                onNumberClick = viewModel::onNumberInput,
                onEraseClick = { viewModel.onNumberInput(0) },
                showErase = uiState.gameMode == GameMode.MANUAL
            )
        }
    }

    if (uiState.gameStatus != GameStatus.PLAYING) {
        GameResultDialog(
            status = uiState.gameStatus,
            language = uiState.language,
            onRestart = { viewModel.resetGame() },
            onExit = onBack
        )
    }
}

fun getLocalizedDifficulty(difficulty: Difficulty, language: Language) = when (difficulty) {
    Difficulty.BEGINNER -> when (language) {
        Language.ENGLISH -> "Beginner"
        Language.JAPANESE -> "初級"
        Language.CHINESE -> "初学者"
        Language.KOREAN -> "초보자"
    }
    Difficulty.INTERMEDIATE -> when (language) {
        Language.ENGLISH -> "Intermediate"
        Language.JAPANESE -> "中級"
        Language.CHINESE -> "中级"
        Language.KOREAN -> "중급자"
    }
    Difficulty.ADVANCED -> when (language) {
        Language.ENGLISH -> "Advanced"
        Language.JAPANESE -> "上級"
        Language.CHINESE -> "高级"
        Language.KOREAN -> "상급자"
    }
}

fun getMistakesLabel(language: Language) = when (language) {
    Language.ENGLISH -> "Mistakes"
    Language.JAPANESE -> "ミス"
    Language.CHINESE -> "错误"
    Language.KOREAN -> "실수"
}

fun getSubmissionMistakesLabel(language: Language) = when (language) {
    Language.ENGLISH -> "Failed Submissions"
    Language.JAPANESE -> "採点失敗"
    Language.CHINESE -> "提交失败"
    Language.KOREAN -> "제출 실패"
}

fun getSubmitText(language: Language) = when (language) {
    Language.ENGLISH -> "Submit"
    Language.JAPANESE -> "採点する"
    Language.CHINESE -> "提交"
    Language.KOREAN -> "제출"
}

@Composable
fun GameHud(
    timerSeconds: Long,
    mistakes: Int,
    maxMistakes: Int,
    difficulty: Difficulty,
    isTimerEnabled: Boolean,
    language: Language,
    gameMode: GameMode
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = getLocalizedDifficulty(difficulty, language),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
                val mistakesLabel = if (gameMode == GameMode.INSTANT) {
                    getMistakesLabel(language)
                } else {
                    getSubmissionMistakesLabel(language)
                }
                Text(
                    text = "$mistakesLabel: $mistakes/$maxMistakes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (mistakes >= maxMistakes) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
// ...
            
            if (isTimerEnabled) {
                val minutes = timerSeconds / 60
                val seconds = timerSeconds % 60
                Text(
                    text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SudokuGrid(
    board: SudokuBoard,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit
) {
    val outlineColor = MaterialTheme.colorScheme.outline
    
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(2.dp, outlineColor, RoundedCornerShape(4.dp))
            .drawBehind {
                val cellSize = size.width / 9
                // Draw 3x3 block lines
                for (i in 1..2) {
                    val pos = cellSize * i * 3
                    drawLine(
                        color = outlineColor,
                        start = Offset(pos, 0f),
                        end = Offset(pos, size.height),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = outlineColor,
                        start = Offset(0f, pos),
                        end = Offset(size.width, pos),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            }
    ) {
        val cellSize = maxWidth / 9

        Column {
            for (row in 0 until 9) {
                Row {
                    for (col in 0 until 9) {
                        val cell = board[row, col]
                        val isSelected = selectedCell?.first == row && selectedCell?.second == col
                        val isHighlighted = isRelated(row, col, selectedCell)
                        
                        SudokuCellView(
                            cell = cell,
                            isSelected = isSelected,
                            isHighlighted = isHighlighted,
                            modifier = Modifier
                                .size(cellSize)
                                .clickable { onCellClick(row, col) }
                        )
                    }
                }
            }
        }
    }
}

fun isRelated(row: Int, col: Int, selectedCell: Pair<Int, Int>?): Boolean {
    if (selectedCell == null) return false
    val (selRow, selCol) = selectedCell
    if (row == selRow || col == selCol) return true
    if (row / 3 == selRow / 3 && col / 3 == selCol / 3) return true
    return false
}

@Composable
fun SudokuCellView(
    cell: SudokuCell,
    isSelected: Boolean,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isHighlighted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        cell.isError -> MaterialTheme.colorScheme.error
        cell.isFixed -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .background(backgroundColor)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        val displayValue = if (cell.isFixed) cell.value else cell.userInput
        if (displayValue != null && displayValue != 0) {
            Text(
                text = displayValue.toString(),
                fontSize = 22.sp,
                fontWeight = if (cell.isFixed) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit,
    onEraseClick: () -> Unit,
    showErase: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (i in 1..5) {
                NumberButton(
                    number = i,
                    onClick = { onNumberClick(i) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (i in 6..9) {
                NumberButton(
                    number = i,
                    onClick = { onNumberClick(i) },
                    modifier = Modifier.weight(1f)
                )
            }
            if (showErase) {
                Surface(
                    onClick = onEraseClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.AutoMirrored.Rounded.Backspace,
                            contentDescription = "Erase",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun NumberButton(
    number: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = number.toString(), fontSize = 28.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun GameResultDialog(
    status: GameStatus,
    language: Language,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    val title = when (status) {
        GameStatus.WON -> when (language) {
            Language.ENGLISH -> "🌟 MAGNIFICENT VICTORY! 🌟"
            Language.JAPANESE -> "🌟 素晴らしい！完全勝利です！ 🌟"
            Language.CHINESE -> "🌟 绝妙的胜利！ 🌟"
            Language.KOREAN -> "🌟 멋진 승리입니다! 🌟"
        }
        else -> when (language) {
            Language.ENGLISH -> "Game Over"
            Language.JAPANESE -> "ゲームオーバー"
            Language.CHINESE -> "游戏结束"
            Language.KOREAN -> "게임 오버"
        }
    }

    val message = when (status) {
        GameStatus.WON -> when (language) {
            Language.ENGLISH -> "You are a true Sudoku Master! Your logical thinking is unparalleled. The world stands in awe of your brilliance!"
            Language.JAPANESE -> "あなたは真の数独マスターです！その論理的思考は類を見ません。あなたの才能に世界が驚いています！"
            Language.CHINESE -> "你是真正的数独大师！你的逻辑思维举世无双。全世界都为你闪耀的才华感到惊叹！"
            Language.KOREAN -> "당신은 진정한 스도쿠 마스터입니다! 당신의 논리적 사고는 타의 추종을 불허합니다. 당신의 천재성에 온 세상이 경탄하고 있습니다!"
        }
        else -> when (language) {
            Language.ENGLISH -> "Don't lose heart! Every mistake is a step toward perfection. Try once more!"
            Language.JAPANESE -> "諦めないで！失敗は成功への一歩です。もう一度挑戦しましょう！"
            Language.CHINESE -> "不要灰心！每一次错误都是通向完美的一步。再试一次吧！"
            Language.KOREAN -> "낙심하지 마세요! 모든 실수는 완벽을 향한 한 걸음입니다. 다시 한 번 도전해 보세요!"
        }
    }

    val buttonText = when (language) {
        Language.ENGLISH -> "Play Again"
        Language.JAPANESE -> "もう一度遊ぶ"
        Language.CHINESE -> "再玩一次"
        Language.KOREAN -> "다시 플레이"
    }

    val exitText = when (language) {
        Language.ENGLISH -> "Exit"
        Language.JAPANESE -> "終了"
        Language.CHINESE -> "退出"
        Language.KOREAN -> "나가기"
    }

    AlertDialog(
        onDismissRequest = { },
        icon = {
            if (status == GameStatus.WON) {
                Text("👑", fontSize = 64.sp)
            } else {
                Text("💫", fontSize = 64.sp)
            }
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.ExtraBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 18.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 24.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(buttonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onExit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(exitText)
            }
        }
    )
}
