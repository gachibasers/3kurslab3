package com.lab3.battleship

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val playerBoard = GameBoard()
    private val enemyBoard = GameBoard()
    private var playerTurn = true
    private var gameOver = false

    private lateinit var txtStatus: TextView
    private lateinit var btnNewGame: Button
    private lateinit var btnShoot: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        txtStatus = binding.txtStatus
        btnNewGame = binding.btnNewGame
        btnShoot = binding.btnShoot

        GameLogger.i("MainActivity created")

        setupBoards()

        btnNewGame.setOnClickListener { startNewGame() }
        btnShoot.setOnClickListener { onShootClicked() }

        startNewGame()
    }

    private fun setupBoards() {
        binding.boardPlayer.removeAllViews()
        binding.boardEnemy.removeAllViews()
        for (r in 0 until 10) {
            for (c in 0 until 10) {
                val cellView = createCellView(r, c, isPlayer = true)
                binding.boardPlayer.addView(cellView)
                val enemyCell = createCellView(r, c, isPlayer = false)
                binding.boardEnemy.addView(enemyCell)
            }
        }
    }

    private fun createCellView(row: Int, col: Int, isPlayer: Boolean): View {
        val view = View(this)
        view.setBackgroundResource(android.R.drawable.btn_default)
        view.tag = Pair(row, col)
        if (!isPlayer) {
            view.setOnClickListener { onEnemyCellClicked(row, col) }
        }
        return view
    }

    private fun startNewGame() {
        playerBoard.autoPlaceShips(intArrayOf(4, 3, 3, 2, 2, 2, 1, 1))
        enemyBoard.autoPlaceShips(intArrayOf(4, 3, 3, 2, 2, 2, 1, 1))
        playerTurn = true
        gameOver = false
        updateStatus(getString(R.string.status_turn))
        btnShoot.isEnabled = false
        GameLogger.i("New game started")
        refreshBoardViews()
    }

    private fun onEnemyCellClicked(row: Int, col: Int) {
        if (gameOver || !playerTurn) {
            GameLogger.w("Not player turn or game over")
            Toast.makeText(this, getString(R.string.msg_wait), Toast.LENGTH_SHORT).show()
            return
        }
        if (enemyBoard.cellIsHit(row, col)) {
            Toast.makeText(this, getString(R.string.msg_invalid_cell), Toast.LENGTH_SHORT).show()
            return
        }
        val result = enemyBoard.shoot(row, col)
        refreshBoardViews()
        when (result) {
            is ShotResult.MISS -> {
                showToast(getString(R.string.msg_miss))
                playerTurn = false
                updateStatus(getString(R.string.status_enemy_turn))
                btnShoot.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({ computerTurn() }, 1200)
            }
            is ShotResult.HIT -> {
                showToast(getString(R.string.msg_hit))
                if (enemyBoard.allShipsSunk()) endGame(playerWon = true)
            }
            is ShotResult.SUNK -> {
                showToast(getString(R.string.msg_sunk))
                if (enemyBoard.allShipsSunk()) endGame(playerWon = true)
            }
        }
    }

    private fun computerTurn() {
        if (gameOver) return
        // Pick a random unshot cell on player board
        var row: Int
        var col: Int
        do {
            row = (Math.random() * 10).toInt()
            col = (Math.random() * 10).toInt()
        } while (playerBoard.cellIsHit(row, col))

        val result = playerBoard.shoot(row, col)
        refreshBoardViews()
        GameLogger.i("Computer shot ($row,$col) -> $result")
        when (result) {
            is ShotResult.MISS -> {
                showToast(getString(R.string.msg_miss))
                playerTurn = true
                updateStatus(getString(R.string.status_turn))
                btnShoot.isEnabled = true
            }
            is ShotResult.HIT, is ShotResult.SUNK -> {
                if (playerBoard.allShipsSunk()) {
                    endGame(playerWon = false)
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({ computerTurn() }, 1000)
                }
            }
        }
    }

    private fun onShootClicked() {
        // Shooting is handled via cell taps; button kept for UI parity.
    }

    private fun endGame(playerWon: Boolean) {
        gameOver = true
        btnShoot.isEnabled = false
        if (playerWon) {
            updateStatus(getString(R.string.msg_win))
            showToast(getString(R.string.msg_win))
            GameLogger.i("PLAYER WON")
        } else {
            updateStatus(getString(R.string.msg_lose))
            showToast(getString(R.string.msg_lose))
            GameLogger.i("COMPUTER WON")
        }
    }

    private fun updateStatus(text: String) {
        txtStatus.text = text
        GameLogger.d("Status: $text")
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun refreshBoardViews() {
        // Rebuild views to reflect current hit/ship state
        binding.boardPlayer.removeAllViews()
        binding.boardEnemy.removeAllViews()
        for (r in 0 until 10) {
            for (c in 0 until 10) {
                binding.boardPlayer.addView(createCellView(r, c, isPlayer = true))
                binding.boardEnemy.addView(createCellView(r, c, isPlayer = false))
            }
        }
    }
}
