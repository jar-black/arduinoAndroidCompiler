package com.arduinocompiler.service.compiler

import com.arduinocompiler.model.CompilationError
import com.arduinocompiler.model.ErrorSeverity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompilationErrorParser @Inject constructor() {

    fun parse(output: String): List<CompilationError> {
        val errors = mutableListOf<CompilationError>()

        output.lines().forEach { line ->
            parseErrorLine(line)?.let { errors.add(it) }
        }

        return errors
    }

    private fun parseErrorLine(line: String): CompilationError? {
        // GCC/Clang error format: file:line:column: severity: message
        // Example: sketch.ino:5:1: error: expected ';' before '}' token

        val patterns = listOf(
            // Standard GCC format
            Regex("""(.+?):(\d+):(\d+):\s+(error|warning|note):\s+(.+)"""),
            // Simplified format without column
            Regex("""(.+?):(\d+):\s+(error|warning|note):\s+(.+)"""),
            // Fatal errors
            Regex("""(.+?):(\d+):(\d+):\s+fatal error:\s+(.+)""")
        )

        for (pattern in patterns) {
            val match = pattern.find(line) ?: continue

            return when (pattern.groupCount) {
                5 -> {
                    // Full format with column
                    val (file, lineNum, column, severity, message) = match.destructured
                    CompilationError(
                        file = file.trim(),
                        line = lineNum.toIntOrNull() ?: 0,
                        column = column.toIntOrNull() ?: 0,
                        message = message.trim(),
                        severity = parseSeverity(severity)
                    )
                }
                4 -> {
                    // Format without column
                    val (file, lineNum, severity, message) = match.destructured
                    CompilationError(
                        file = file.trim(),
                        line = lineNum.toIntOrNull() ?: 0,
                        column = 0,
                        message = message.trim(),
                        severity = parseSeverity(severity)
                    )
                }
                else -> null
            }
        }

        // Check for fatal errors without line numbers
        if (line.contains("fatal error:", ignoreCase = true)) {
            return CompilationError(
                file = "unknown",
                line = 0,
                column = 0,
                message = line.substringAfter("fatal error:").trim(),
                severity = ErrorSeverity.ERROR
            )
        }

        return null
    }

    private fun parseSeverity(severityStr: String): ErrorSeverity {
        return when (severityStr.lowercase()) {
            "error", "fatal error" -> ErrorSeverity.ERROR
            "warning" -> ErrorSeverity.WARNING
            "note", "info" -> ErrorSeverity.INFO
            else -> ErrorSeverity.ERROR
        }
    }

    fun extractMainErrors(errors: List<CompilationError>): List<CompilationError> {
        // Filter out notes and focus on main errors
        return errors.filter { it.severity == ErrorSeverity.ERROR }
    }

    fun getErrorSummary(errors: List<CompilationError>): String {
        val errorCount = errors.count { it.severity == ErrorSeverity.ERROR }
        val warningCount = errors.count { it.severity == ErrorSeverity.WARNING }

        return buildString {
            if (errorCount > 0) {
                append("$errorCount error${if (errorCount != 1) "s" else ""}")
            }
            if (warningCount > 0) {
                if (errorCount > 0) append(", ")
                append("$warningCount warning${if (warningCount != 1) "s" else ""}")
            }
            if (errorCount == 0 && warningCount == 0) {
                append("No errors or warnings")
            }
        }
    }
}
