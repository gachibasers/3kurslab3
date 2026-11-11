package com.lab3.battleship

import kotlin.random.Random

/**
 * Game board with ships. Handles placement, shooting and win check.
 */
class GameBoard(val size: Int = 10) {

    private val grid = Array(size) { Array(size) { Cell(it, 0) } }
    private val ships = mutableListOf<Ship>()

    fun getCell(row: Int, col: Int): Cell = grid[row][col]

    fun getShips(): List<Ship> = ships.toList()

    fun cellIsEmpty(row: Int, col: Int): Boolean {
        return grid[row][col].ship == null && !grid[row][col].hit
    }

    fun cellIsHit(row: Int, col: Int): Boolean {
        return grid[row][col].hit
    }

    /**
     * Places a ship if all its cells are free and not adjacent to another ship.
     */
    fun placeShip(ship: Ship, startRow: Int, startCol: Int, horizontal: Boolean): Boolean {
        val cells = mutableListOf<Cell>()
        for (i in 0 until ship.length) {
            val r = if (horizontal) startRow else startRow + i
            val c = if (horizontal) startCol + i else startCol
            if (r < 0 || r >= size || c < 0 || c >= size) {
                return false
            }
            if (!cellIsEmpty(r, c)) {
                return false
            }
            // No adjacent ships (including diagonals)
            for (dr in -1..1) {
                for (dc in -1..1) {
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0 until size && nc in 0 until size) {
                        val neighbor = grid[nr][nc].ship
                        if (neighbor != null && neighbor.id != ship.id) {
                            return false
                        }
                    }
                }
            }
            cells.add(grid[r][c])
        }
        ship.place(startRow, startCol, horizontal)
        for (cell in cells) {
            cell.ship = ship
        }
        ships.add(ship)
        GameLogger.d("Placed ship ${ship.id} at ($startRow,$startCol) horizontal=$horizontal")
        return true
    }

    /**
     * Fires at a cell. Returns true if hit, false if miss.
     * Throws IllegalStateException if cell already fired at.
     */
    fun shoot(row: Int, col: Int): ShotResult {
        require(!grid[row][col].hit) { "Cell already fired at" }
        grid[row][col].hit = true
        val ship = grid[row][col].ship
        return if (ship != null) {
            ship.hitCell(grid[row][col])
            if (ship.sunk) {
                GameLogger.i("Ship ${ship.id} sunk at ($row,$col)")
                ShotResult.SUNK(ship)
            } else {
                GameLogger.d("Hit at ($row,$col)")
                ShotResult.HIT
            }
        } else {
            GameLogger.d("Miss at ($row,$col)")
            ShotResult.MISS
        }
    }

    fun allShipsSunk(): Boolean {
        return ships.all { it.sunk }
    }

    fun clear() {
        for (row in grid) {
            for (cell in row) {
                cell.ship = null
                cell.hit = false
            }
        }
        ships.clear()
    }

    fun autoPlaceShips(lengths: IntArray) {
        clear()
        val random = Random
        var shipId = 1
        for (length in lengths) {
            val ship = Ship(shipId, length)
            var placed = false
            var attempts = 0
            while (!placed && attempts < 1000) {
                val r = random.nextInt(size)
                val c = random.nextInt(size)
                val horizontal = random.nextBoolean()
                placed = placeShip(ship, r, c, horizontal)
                attempts++
            }
            if (!placed) {
                GameLogger.w("Could not place ship length=$length after 1000 attempts")
            }
            shipId++
        }
        GameLogger.i("Auto-placed ${ships.size} ships")
    }
}

/**
 * Result of a shot.
 */
sealed class ShotResult {
    object MISS : ShotResult()
    object HIT : ShotResult()
    data class SUNK(val ship: Ship) : ShotResult()
}
