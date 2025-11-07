package com.arduinocompiler.model

import java.util.Date

sealed class CompilationStatus {
    object Idle : CompilationStatus()
    data class Preparing(val message: String = "Preparing compilation...") : CompilationStatus()
    data class InProgress(
        val progress: Int = 0,
        val message: String = "Compiling..."
    ) : CompilationStatus()
    data class Success(
        val hexFile: String,
        val binarySize: Long,
        val dataSize: Long,
        val duration: Long,
        val message: String = "Compilation successful"
    ) : CompilationStatus()
    data class Error(
        val message: String,
        val errors: List<CompilationError> = emptyList(),
        val duration: Long = 0
    ) : CompilationStatus()
}

data class CompilationResult(
    val success: Boolean,
    val hexFilePath: String = "",
    val elfFilePath: String = "",
    val output: String = "",
    val errors: List<CompilationError> = emptyList(),
    val warnings: List<String> = emptyList(),
    val binarySize: Long = 0,
    val dataSize: Long = 0,
    val maxBinarySize: Long = 0,
    val maxDataSize: Long = 0,
    val duration: Long = 0,
    val timestamp: Date = Date()
) {
    fun getBinaryPercentage(): Int {
        return if (maxBinarySize > 0) {
            ((binarySize.toDouble() / maxBinarySize) * 100).toInt()
        } else 0
    }

    fun getDataPercentage(): Int {
        return if (maxDataSize > 0) {
            ((dataSize.toDouble() / maxDataSize) * 100).toInt()
        } else 0
    }

    fun hasWarnings(): Boolean = warnings.isNotEmpty()
    fun hasErrors(): Boolean = errors.isNotEmpty()
}

data class CompilationError(
    val file: String,
    val line: Int,
    val column: Int,
    val message: String,
    val severity: ErrorSeverity = ErrorSeverity.ERROR
) {
    fun getFormattedMessage(): String {
        return "$file:$line:$column: ${severity.name.lowercase()}: $message"
    }
}

enum class ErrorSeverity {
    ERROR,
    WARNING,
    INFO
}
