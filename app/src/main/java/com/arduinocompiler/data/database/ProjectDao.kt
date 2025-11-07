package com.arduinocompiler.data.database

import androidx.room.*
import com.arduinocompiler.model.ArduinoProject
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY modifiedAt DESC")
    fun getAllProjects(): Flow<List<ArduinoProject>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): ArduinoProject?

    @Query("SELECT * FROM projects WHERE name = :name LIMIT 1")
    suspend fun getProjectByName(name: String): ArduinoProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ArduinoProject): Long

    @Update
    suspend fun updateProject(project: ArduinoProject)

    @Delete
    suspend fun deleteProject(project: ArduinoProject)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectCount(): Int

    @Query("SELECT * FROM projects WHERE isExample = 1")
    fun getExampleProjects(): Flow<List<ArduinoProject>>

    @Query("SELECT * FROM projects WHERE boardId = :boardId")
    fun getProjectsByBoard(boardId: String): Flow<List<ArduinoProject>>
}
