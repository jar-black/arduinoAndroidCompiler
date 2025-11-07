package com.arduinocompiler.util

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun File.ensureExists(): File {
    if (!exists()) {
        mkdirs()
    }
    return this
}

fun String.isValidFileName(): Boolean {
    return this.isNotBlank() && !this.contains(Regex("[<>:\"/\\\\|?*]"))
}

fun String.toSafeFileName(): String {
    return this.replace(Regex("[<>:\"/\\\\|?*]"), "_")
}

fun Date.formatTimestamp(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(this)
}

fun Date.formatFilename(): String {
    val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    return formatter.format(this)
}

fun <T> Flow<T>.handleErrors(
    onError: suspend (Throwable) -> Unit = {}
): Flow<T> = catch { e ->
    Logger.e("Flow error", e)
    onError(e)
    throw e
}

fun <T> Flow<T>.withLoading(
    onStart: suspend () -> Unit = {}
): Flow<T> = onStart {
    onStart()
}

fun String.extractErrorMessage(): String {
    // Extract meaningful error messages from compiler output
    return when {
        contains("error:", ignoreCase = true) -> {
            split('\n').firstOrNull { it.contains("error:", ignoreCase = true) }
                ?.substringAfter("error:")
                ?.trim() ?: this
        }
        contains("fatal error:", ignoreCase = true) -> {
            split('\n').firstOrNull { it.contains("fatal error:", ignoreCase = true) }
                ?.substringAfter("fatal error:")
                ?.trim() ?: this
        }
        else -> this
    }
}

fun ByteArray.toHexString(): String {
    return joinToString("") { "%02x".format(it) }
}

fun String.fromHexString(): ByteArray {
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun Long.formatBytes(): String {
    val kb = 1024.0
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        this >= gb -> "%.2f GB".format(this / gb)
        this >= mb -> "%.2f MB".format(this / mb)
        this >= kb -> "%.2f KB".format(this / kb)
        else -> "$this B"
    }
}

fun Int.formatPercentage(): String {
    return "$this%"
}

fun String.truncate(maxLength: Int = 100, suffix: String = "..."): String {
    return if (length <= maxLength) this
    else take(maxLength - suffix.length) + suffix
}
