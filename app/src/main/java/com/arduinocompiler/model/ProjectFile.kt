package com.arduinocompiler.model

import java.io.File
import java.util.Date

data class ProjectFile(
    val name: String,
    val path: String,
    val type: FileType,
    val size: Long,
    val lastModified: Date
) {
    enum class FileType {
        INO,    // Arduino sketch (.ino)
        CPP,    // C++ source (.cpp)
        H,      // Header file (.h)
        TXT,    // Text file
        OTHER   // Other files
    }

    fun getExtension(): String {
        return name.substringAfterLast(".", "")
    }

    fun getNameWithoutExtension(): String {
        return name.substringBeforeLast(".")
    }

    fun isSourceFile(): Boolean {
        return type in listOf(FileType.INO, FileType.CPP, FileType.H)
    }

    fun toFile(): File {
        return File(path)
    }

    companion object {
        fun fromFile(file: File): ProjectFile {
            val extension = file.extension.lowercase()
            val type = when (extension) {
                "ino" -> FileType.INO
                "cpp" -> FileType.CPP
                "h", "hpp" -> FileType.H
                "txt" -> FileType.TXT
                else -> FileType.OTHER
            }

            return ProjectFile(
                name = file.name,
                path = file.absolutePath,
                type = type,
                size = file.length(),
                lastModified = Date(file.lastModified())
            )
        }

        fun getFileTypeFromExtension(extension: String): FileType {
            return when (extension.lowercase()) {
                "ino" -> FileType.INO
                "cpp" -> FileType.CPP
                "h", "hpp" -> FileType.H
                "txt" -> FileType.TXT
                else -> FileType.OTHER
            }
        }
    }
}

data class Library(
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val path: String,
    val isInstalled: Boolean = false,
    val isBuiltIn: Boolean = false
) {
    fun getDisplayName(): String {
        return "$name ($version)"
    }
}
