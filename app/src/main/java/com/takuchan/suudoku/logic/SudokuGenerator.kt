package com.takuchan.suudoku.logic

import kotlin.random.Random

class SudokuGenerator {

    fun generateBoard(difficulty: Difficulty): Pair<SudokuBoard, SudokuBoard> {
        val fullGrid = Array(9) { IntArray(9) { 0 } }
        fillGrid(fullGrid)
        
        val solutionBoard = SudokuBoard(
            fullGrid.mapIndexed { r, row ->
                row.mapIndexed { c, value ->
                    SudokuCell(r, c, value, isFixed = true)
                }
            }
        )

        val puzzleGrid = fullGrid.map { it.copyOf() }.toTypedArray()
        removeCells(puzzleGrid, difficulty.cellsToRemove)

        val puzzleBoard = SudokuBoard(
            puzzleGrid.mapIndexed { r, row ->
                row.mapIndexed { c, value ->
                    if (value != 0) {
                        SudokuCell(r, c, value, isFixed = true)
                    } else {
                        SudokuCell(r, c, fullGrid[r][c], isFixed = false)
                    }
                }
            }
        )

        return Pair(puzzleBoard, solutionBoard)
    }

    private fun fillGrid(grid: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] == 0) {
                    val numbers = (1..9).shuffled()
                    for (num in numbers) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num
                            if (fillGrid(grid)) return true
                            grid[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValid(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (x in 0..8) {
            if (grid[row][x] == num || grid[x][col] == num) return false
        }
        val startRow = row - row % 3
        val startCol = col - col % 3
        for (i in 0..2) {
            for (j in 0..2) {
                if (grid[i + startRow][j + startCol] == num) return false
            }
        }
        return true
    }

    private fun removeCells(grid: Array<IntArray>, count: Int) {
        var removed = 0
        while (removed < count) {
            val r = Random.nextInt(9)
            val c = Random.nextInt(9)
            if (grid[r][c] != 0) {
                grid[r][c] = 0
                removed++
            }
        }
    }
}
