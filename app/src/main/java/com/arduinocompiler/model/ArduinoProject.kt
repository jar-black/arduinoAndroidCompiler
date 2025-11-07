package com.arduinocompiler.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "projects")
data class ArduinoProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val path: String,
    val boardId: String,
    val createdAt: Date,
    val modifiedAt: Date,
    val description: String = "",
    val mainFileName: String = "main.ino",
    val isExample: Boolean = false
) {
    fun getMainFilePath(): String {
        return "$path/$mainFileName"
    }

    fun getProjectDirectory(): String {
        return path
    }

    fun getBuildDirectory(): String {
        return "$path/build"
    }
}
