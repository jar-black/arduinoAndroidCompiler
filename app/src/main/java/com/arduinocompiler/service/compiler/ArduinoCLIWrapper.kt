package com.arduinocompiler.service.compiler

import android.content.Context
import com.arduinocompiler.model.Library
import com.arduinocompiler.util.Constants
import com.arduinocompiler.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArduinoCLIWrapper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val toolchainDir = File(context.filesDir, Constants.TOOLCHAIN_DIR)
    private val cliPath = File(toolchainDir, Constants.ARDUINO_CLI_BINARY)
    private val configDir = File(context.filesDir, "arduino_config")

    init {
        configDir.mkdirs()
    }

    suspend fun initConfig() = withContext(Dispatchers.IO) {
        executeCommand(listOf(
            cliPath.absolutePath,
            "config",
            "init",
            "--dest-dir", configDir.absolutePath
        ))
    }

    suspend fun compile(
        sketchPath: String,
        fqbn: String,
        buildPath: String,
        onProgress: (Int, String) -> Unit = { _, _ -> }
    ): CompilationResult = withContext(Dispatchers.IO) {
        val command = listOf(
            cliPath.absolutePath,
            "compile",
            "--fqbn", fqbn,
            "--build-path", buildPath,
            "--output-dir", buildPath,
            "--verbose",
            sketchPath
        )

        Logger.d("Executing compile: ${command.joinToString(" ")}")

        val processBuilder = ProcessBuilder(command)
        processBuilder.directory(File(sketchPath).parentFile)
        processBuilder.redirectErrorStream(true)

        val output = StringBuilder()
        var binarySize = 0L
        var dataSize = 0L

        try {
            val process = processBuilder.start()

            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.let {
                        output.appendLine(it)
                        Logger.d("Compile output: $it")

                        // Parse progress
                        when {
                            it.contains("Sketch uses") -> {
                                binarySize = extractSize(it, "Sketch uses")
                            }
                            it.contains("Global variables use") -> {
                                dataSize = extractSize(it, "Global variables use")
                            }
                        }
                    }
                }
            }

            val exitCode = process.waitFor()

            CompilationResult(
                success = exitCode == 0,
                output = output.toString(),
                exitCode = exitCode,
                binarySize = binarySize,
                dataSize = dataSize
            )
        } catch (e: Exception) {
            Logger.e("Compilation failed", e)
            CompilationResult(
                success = false,
                output = output.toString() + "\n" + e.message,
                exitCode = -1
            )
        }
    }

    suspend fun installCore(corePlatform: String) = withContext(Dispatchers.IO) {
        executeCommand(listOf(
            cliPath.absolutePath,
            "core",
            "install",
            corePlatform
        ))
    }

    suspend fun installLibrary(libraryName: String) = withContext(Dispatchers.IO) {
        executeCommand(listOf(
            cliPath.absolutePath,
            "lib",
            "install",
            libraryName
        ))
    }

    suspend fun listLibraries(): List<Library> = withContext(Dispatchers.IO) {
        val output = executeCommand(listOf(
            cliPath.absolutePath,
            "lib",
            "list",
            "--format", "json"
        ))

        // TODO: Parse JSON output and return list of libraries
        // For now, return empty list
        emptyList()
    }

    suspend fun listBoards(search: String = ""): List<String> = withContext(Dispatchers.IO) {
        val command = mutableListOf(
            cliPath.absolutePath,
            "board",
            "listall"
        )

        if (search.isNotEmpty()) {
            command.add(search)
        }

        val output = executeCommand(command)
        output.lines().filter { it.isNotBlank() }
    }

    suspend fun updateIndex() = withContext(Dispatchers.IO) {
        executeCommand(listOf(
            cliPath.absolutePath,
            "core",
            "update-index"
        ))
    }

    private fun executeCommand(command: List<String>): String {
        Logger.d("Executing: ${command.joinToString(" ")}")

        val processBuilder = ProcessBuilder(command)
        processBuilder.directory(configDir)
        processBuilder.redirectErrorStream(true)

        val output = StringBuilder()

        try {
            val process = processBuilder.start()

            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.let {
                        output.appendLine(it)
                        Logger.d("CLI output: $it")
                    }
                }
            }

            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw RuntimeException("Command failed with exit code $exitCode: $output")
            }

            return output.toString()
        } catch (e: Exception) {
            Logger.e("Command execution failed", e)
            throw e
        }
    }

    private fun extractSize(line: String, prefix: String): Long {
        return try {
            val regex = Regex("""(\d+)\s+bytes""")
            regex.find(line)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}

data class CompilationResult(
    val success: Boolean,
    val output: String,
    val exitCode: Int,
    val binarySize: Long = 0,
    val dataSize: Long = 0
)
