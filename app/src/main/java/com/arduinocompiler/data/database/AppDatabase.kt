package com.arduinocompiler.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arduinocompiler.model.ArduinoProject
import com.arduinocompiler.model.BoardDefinition

@Database(
    entities = [
        ArduinoProject::class,
        BoardDefinition::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun boardDao(): BoardDao

    companion object {
        const val DATABASE_NAME = "arduino_compiler_db"
    }
}
