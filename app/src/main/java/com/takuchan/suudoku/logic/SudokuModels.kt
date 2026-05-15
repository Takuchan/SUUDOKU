package com.takuchan.suudoku.logic

enum class Difficulty(val cellsToRemove: Int) {
    BEGINNER(20),
    INTERMEDIATE(40),
    ADVANCED(60)
}

data class SudokuCell(
    val row: Int,
    val col: Int,
    val value: Int,
    val isFixed: Boolean = false,
    val userInput: Int? = null,
    val isError: Boolean = false
)

data class SudokuBoard(
    val cells: List<List<SudokuCell>>
) {
    operator fun get(row: Int, col: Int): SudokuCell = cells[row][col]
}

enum class GameStatus {
    PLAYING,
    WON,
    LOST
}
