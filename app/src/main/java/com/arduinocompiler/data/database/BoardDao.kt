package com.arduinocompiler.data.database

import androidx.room.*
import com.arduinocompiler.model.BoardDefinition
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDao {
    @Query("SELECT * FROM boards ORDER BY name ASC")
    fun getAllBoards(): Flow<List<BoardDefinition>>

    @Query("SELECT * FROM boards WHERE isInstalled = 1 ORDER BY name ASC")
    fun getInstalledBoards(): Flow<List<BoardDefinition>>

    @Query("SELECT * FROM boards WHERE fqbn = :fqbn")
    suspend fun getBoardByFqbn(fqbn: String): BoardDefinition?

    @Query("SELECT * FROM boards WHERE architecture = :arch ORDER BY name ASC")
    fun getBoardsByArchitecture(arch: String): Flow<List<BoardDefinition>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: BoardDefinition)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoards(boards: List<BoardDefinition>)

    @Update
    suspend fun updateBoard(board: BoardDefinition)

    @Delete
    suspend fun deleteBoard(board: BoardDefinition)

    @Query("DELETE FROM boards")
    suspend fun deleteAllBoards()

    @Query("UPDATE boards SET isInstalled = :isInstalled WHERE fqbn = :fqbn")
    suspend fun updateBoardInstallStatus(fqbn: String, isInstalled: Boolean)

    @Query("SELECT COUNT(*) FROM boards WHERE isInstalled = 1")
    suspend fun getInstalledBoardCount(): Int
}
