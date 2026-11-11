package com.lab3.battleship

/**
 * A single ship on the board.
 */
data class Ship(
    val id: Int,
    val length: Int,
    val cells: MutableList<Cell> = mutableListOf(),
    var sunk: Boolean = false
) {

    fun hitCell(cell: Cell) {
        cell.hit = true
        if (cells.all { it.hit }) {
            sunk = true
        }
    }

    fun place(startRow: Int, startCol: Int, horizontal: Boolean) {
        cells.clear()
        for (i in 0 until length) {
            val r = if (horizontal) startRow else startRow + i
            val c = if (horizontal) startCol + i else startCol
            cells.add(Cell(r, c))
        }
    }

    fun contains(row: Int, col: Int): Boolean {
        return cells.any { it.row == row && it.col == col }
    }

    override fun toString(): String {
        return "Ship(id=$id, length=$length, sunk=$sunk, cells=${cells.size})"
    }
}

/**
 * A single board cell.
 */
data class Cell(
    val row: Int,
    val col: Int,
    var hit: Boolean = false,
    var ship: Ship? = null
)
