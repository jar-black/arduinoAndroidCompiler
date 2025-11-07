package com.arduinocompiler.util

import android.util.Log

object Logger {
    private const val TAG = "ArduinoCompiler"
    private var isDebugMode = true

    fun setDebugMode(enabled: Boolean) {
        isDebugMode = enabled
    }

    fun d(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Log.d(tag, message)
        }
    }

    fun i(message: String, tag: String = TAG) {
        Log.i(tag, message)
    }

    fun w(message: String, tag: String = TAG) {
        Log.w(tag, message)
    }

    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    fun v(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Log.v(tag, message)
        }
    }

    fun wtf(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (throwable != null) {
            Log.wtf(tag, message, throwable)
        } else {
            Log.wtf(tag, message)
        }
    }
}
