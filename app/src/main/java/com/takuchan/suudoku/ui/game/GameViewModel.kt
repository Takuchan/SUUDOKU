package com.takuchan.suudoku.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takuchan.suudoku.logic.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val board: SudokuBoard = SudokuBoard(emptyList()),
    val selectedCell: Pair<Int, Int>? = null,
    val mistakes: Int = 0,
    val maxMistakes: Int = 3,
    val timerSeconds: Long = 0,
    val gameStatus: GameStatus = GameStatus.PLAYING,
    val difficulty: Difficulty = Difficulty.BEGINNER,
    val isTimerRunning: Boolean = false,
    val isTimerEnabled: Boolean = true,
    val gameMode: GameMode = GameMode.INSTANT,
    val language: Language = Language.JAPANESE
)

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val generator = SudokuGenerator()
    private var timerJob: Job? = null

    fun startGame(
        difficulty: Difficulty,
        maxMistakes: Int = 3,
        isTimerEnabled: Boolean = true,
        gameMode: GameMode = GameMode.INSTANT,
        language: Language = Language.JAPANESE
    ) {
        val (puzzle, _) = generator.generateBoard(difficulty)
        _uiState.update {
            it.copy(
                board = puzzle,
                difficulty = difficulty,
                maxMistakes = maxMistakes,
                mistakes = 0,
                timerSeconds = 0,
                gameStatus = GameStatus.PLAYING,
                selectedCell = null,
                isTimerRunning = true,
                isTimerEnabled = isTimerEnabled,
                gameMode = gameMode,
                language = language
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_uiState.value.isTimerRunning &&
                    _uiState.value.gameStatus == GameStatus.PLAYING &&
                    _uiState.value.isTimerEnabled
                ) {
                    _uiState.update { it.copy(timerSeconds = it.timerSeconds + 1) }
                }
            }
        }
    }

    fun onCellSelected(row: Int, col: Int) {
        if (_uiState.value.gameStatus != GameStatus.PLAYING) return
        _uiState.update { it.copy(selectedCell = row to col) }
    }

    fun onNumberInput(number: Int) {
        val state = _uiState.value
        if (state.gameStatus != GameStatus.PLAYING) return
        val selected = state.selectedCell ?: return
        val (row, col) = selected
        val cell = state.board[row, col]

        if (cell.isFixed) return

        // Handle Erase
        if (number == 0) {
            val newBoard = SudokuBoard(
                state.board.cells.map { r ->
                    r.map { c ->
                        if (c.row == row && c.col == col) {
                            c.copy(userInput = null, isError = false)
                        } else {
                            c
                        }
                    }
                }
            )
            _uiState.update { it.copy(board = newBoard) }
            return
        }

        if (state.gameMode == GameMode.INSTANT) {
            // If the same incorrect number is already there, don't penalize again
            if (cell.userInput == number && cell.isError) return

            val isCorrect = number == cell.value
            val newMistakes = if (!isCorrect) state.mistakes + 1 else state.mistakes

            val newBoard = SudokuBoard(
                state.board.cells.map { r ->
                    r.map { c ->
                        if (c.row == row && c.col == col) {
                            c.copy(
                                userInput = number,
                                isError = !isCorrect
                            )
                        } else {
                            c
                        }
                    }
                }
            )

            val newStatus = when {
                newMistakes >= state.maxMistakes -> GameStatus.LOST
                checkWin(newBoard) -> GameStatus.WON
                else -> GameStatus.PLAYING
            }

            _uiState.update {
                it.copy(
                    board = newBoard,
                    mistakes = newMistakes,
                    gameStatus = newStatus,
                    isTimerRunning = newStatus == GameStatus.PLAYING
                )
            }
        } else {
            // Manual Mode: Just input the number
            val newBoard = SudokuBoard(
                state.board.cells.map { r ->
                    r.map { c ->
                        if (c.row == row && c.col == col) {
                            c.copy(userInput = number, isError = false)
                        } else {
                            c
                        }
                    }
                }
            )
            _uiState.update { it.copy(board = newBoard) }
        }
    }

    fun submitBoard() {
        val state = _uiState.value
        if (state.gameMode != GameMode.MANUAL || state.gameStatus != GameStatus.PLAYING) return

        var hasErrors = false
        val newBoard = SudokuBoard(
            state.board.cells.map { r ->
                r.map { c ->
                    if (!c.isFixed && c.userInput != null) {
                        val isError = c.userInput != c.value
                        if (isError) hasErrors = true
                        c.copy(isError = isError)
                    } else {
                        c
                    }
                }
            }
        )

        val isAllFilled = newBoard.cells.all { r -> r.all { it.isFixed || it.userInput != null } }
        val isNoErrors = !hasErrors

        val newMistakes = if (hasErrors) state.mistakes + 1 else state.mistakes
        
        val newStatus = when {
            isAllFilled && isNoErrors -> GameStatus.WON
            newMistakes >= state.maxMistakes -> GameStatus.LOST
            else -> GameStatus.PLAYING
        }

        _uiState.update {
            it.copy(
                board = newBoard,
                mistakes = newMistakes,
                gameStatus = newStatus,
                isTimerRunning = newStatus == GameStatus.PLAYING
            )
        }
    }

    private fun checkWin(board: SudokuBoard): Boolean {
        return board.cells.all { row ->
            row.all { cell ->
                cell.isFixed || cell.userInput == cell.value
            }
        }
    }

    fun pauseGame() {
        _uiState.update { it.copy(isTimerRunning = false) }
    }

    fun resumeGame() {
        if (_uiState.value.gameStatus == GameStatus.PLAYING) {
            _uiState.update { it.copy(isTimerRunning = true) }
        }
    }

    fun resetGame() {
        val state = _uiState.value
        startGame(
            difficulty = state.difficulty,
            maxMistakes = state.maxMistakes,
            isTimerEnabled = state.isTimerEnabled,
            gameMode = state.gameMode,
            language = state.language
        )
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
