package com.takuchan.suudoku.ui.game

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
                isTimerEnabled = uiState.isTimerEnabled
            )

            SudokuGrid(
                board = uiState.board,
                selectedCell = uiState.selectedCell,
                onCellClick = viewModel::onCellSelected
            )

            NumberPad(
                onNumberClick = viewModel::onNumberInput,
                onEraseClick = { viewModel.onNumberInput(0) }
            )
        }
    }

    if (uiState.gameStatus != GameStatus.PLAYING) {
        GameResultDialog(
            status = uiState.gameStatus,
            onRestart = { viewModel.resetGame() },
            onExit = onBack
        )
    }
}

@Composable
fun GameHud(
    timerSeconds: Long,
    mistakes: Int,
    maxMistakes: Int,
    difficulty: Difficulty,
    isTimerEnabled: Boolean
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
                    text = difficulty.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Mistakes: $mistakes/$maxMistakes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (mistakes >= maxMistakes) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
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
    onEraseClick: () -> Unit
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
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        icon = {
            if (status == GameStatus.WON) {
                Text("🏆", fontSize = 48.sp)
            } else {
                Text("❌", fontSize = 48.sp)
            }
        },
        title = {
            Text(if (status == GameStatus.WON) "Victory!" else "Defeat")
        },
        text = {
            Text(
                if (status == GameStatus.WON) 
                    "You've mastered this Sudoku! Ready for another challenge?" 
                else 
                    "Too many mistakes! Don't give up, try again."
            )
        },
        confirmButton = {
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Play Again")
            }
        },
        dismissButton = {
            TextButton(onClick = onExit) {
                Text("Exit")
            }
        }
    )
}
