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

enum class GameMode {
    INSTANT, MANUAL
}

enum class Language(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    JAPANESE("日本語", "ja"),
    CHINESE("中文", "zh"),
    KOREAN("한국어", "ko")
}

enum class GameStatus {
    PLAYING,
    WON,
    LOST
}
