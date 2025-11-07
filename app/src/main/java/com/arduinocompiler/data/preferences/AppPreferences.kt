package com.arduinocompiler.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.arduinocompiler.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "arduino_compiler_prefs")

class AppPreferences(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val DEFAULT_BOARD = stringPreferencesKey(Constants.PrefsKeys.DEFAULT_BOARD)
        val DEFAULT_BAUD_RATE = intPreferencesKey(Constants.PrefsKeys.DEFAULT_BAUD_RATE)
        val EDITOR_FONT_SIZE = intPreferencesKey(Constants.PrefsKeys.EDITOR_FONT_SIZE)
        val EDITOR_THEME = stringPreferencesKey(Constants.PrefsKeys.EDITOR_THEME)
        val TOOLCHAIN_INSTALLED = booleanPreferencesKey(Constants.PrefsKeys.TOOLCHAIN_INSTALLED)
        val FIRST_RUN = booleanPreferencesKey(Constants.PrefsKeys.FIRST_RUN)
    }

    val defaultBoard: Flow<String> = dataStore.data.map { prefs ->
        prefs[DEFAULT_BOARD] ?: Constants.BoardIds.UNO
    }

    val defaultBaudRate: Flow<Int> = dataStore.data.map { prefs ->
        prefs[DEFAULT_BAUD_RATE] ?: Constants.DEFAULT_BAUD_RATE
    }

    val editorFontSize: Flow<Int> = dataStore.data.map { prefs ->
        prefs[EDITOR_FONT_SIZE] ?: 14
    }

    val editorTheme: Flow<String> = dataStore.data.map { prefs ->
        prefs[EDITOR_THEME] ?: "dark"
    }

    val isToolchainInstalled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[TOOLCHAIN_INSTALLED] ?: false
    }

    val isFirstRun: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[FIRST_RUN] ?: true
    }

    suspend fun setDefaultBoard(boardId: String) {
        dataStore.edit { prefs ->
            prefs[DEFAULT_BOARD] = boardId
        }
    }

    suspend fun setDefaultBaudRate(baudRate: Int) {
        dataStore.edit { prefs ->
            prefs[DEFAULT_BAUD_RATE] = baudRate
        }
    }

    suspend fun setEditorFontSize(size: Int) {
        dataStore.edit { prefs ->
            prefs[EDITOR_FONT_SIZE] = size
        }
    }

    suspend fun setEditorTheme(theme: String) {
        dataStore.edit { prefs ->
            prefs[EDITOR_THEME] = theme
        }
    }

    suspend fun setToolchainInstalled(installed: Boolean) {
        dataStore.edit { prefs ->
            prefs[TOOLCHAIN_INSTALLED] = installed
        }
    }

    suspend fun setFirstRun(isFirst: Boolean) {
        dataStore.edit { prefs ->
            prefs[FIRST_RUN] = isFirst
        }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
