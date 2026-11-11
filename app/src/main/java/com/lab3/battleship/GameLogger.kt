package com.lab3.battleship

import android.util.Log

/**
 * Centralized logger for the Battleship application.
 * All log output goes through this class so tag and level
 * are configured in one place.
 */
object GameLogger {

    private const val TAG = "BattleshipLab3"

    private var logLevel: Int = Log.DEBUG

    fun setLogLevel(level: Int) {
        logLevel = level
    }

    fun d(message: String) {
        if (logLevel <= Log.DEBUG) Log.d(TAG, message)
    }

    fun i(message: String) {
        if (logLevel <= Log.INFO) Log.i(TAG, message)
    }

    fun w(message: String) {
        if (logLevel <= Log.WARN) Log.w(TAG, message)
    }

    fun e(message: String) {
        if (logLevel <= Log.ERROR) Log.e(TAG, message)
    }

    fun e(message: String, tr: Throwable) {
        if (logLevel <= Log.ERROR) Log.e(TAG, message, tr)
    }
}
