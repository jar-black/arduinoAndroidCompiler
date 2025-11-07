package com.arduinocompiler.service.compiler

import android.content.Context
import com.arduinocompiler.model.*
import com.arduinocompiler.util.Constants
import com.arduinocompiler.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompilerService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val arduinoCLIWrapper: ArduinoCLIWrapper,
    private val errorParser: CompilationErrorParser
) {

    private val toolchainDir: File
        get() = File(context.filesDir, Constants.TOOLCHAIN_DIR).apply { mkdirs() }

    suspend fun isToolchainInstalled(): Boolean = withContext(Dispatchers.IO) {
        val cliPath = File(toolchainDir, Constants.ARDUINO_CLI_BINARY)
        return@withContext cliPath.exists() && cliPath.canExecute()
    }

    fun installToolchain(): Flow<ToolchainInstallStatus> = flow {
        emit(ToolchainInstallStatus.Downloading(0))

        try {
            // TODO: Download Arduino CLI binary for Android ARM
            // This needs to be implemented based on:
            // 1. Detect device architecture (arm64-v8a vs armeabi-v7a)
            // 2. Download appropriate binary from Arduino CLI releases
            // 3. Extract and set executable permissions
            // 4. Verify installation

            // For now, check if binary exists in assets
            val arch = getDeviceArchitecture()
            val assetPath = "toolchain/arduino-cli-$arch"

            context.assets.open(assetPath).use { input ->
                val cliFile = File(toolchainDir, Constants.ARDUINO_CLI_BINARY)
                cliFile.outputStream().use { output ->
                    input.copyTo(output)
                }
                cliFile.setExecutable(true)
            }

            emit(ToolchainInstallStatus.Installing)

            // Initialize Arduino CLI configuration
            arduinoCLIWrapper.initConfig()

            // Install core Arduino platforms
            arduinoCLIWrapper.installCore(Constants.CorePackages.AVR)

            emit(ToolchainInstallStatus.Success)
        } catch (e: Exception) {
            Logger.e("Failed to install toolchain", e)
            emit(ToolchainInstallStatus.Error(e.message ?: "Installation failed"))
        }
    }.flowOn(Dispatchers.IO)

    fun compile(
        project: ArduinoProject,
        board: BoardDefinition
    ): Flow<CompilationStatus> = flow {
        val startTime = System.currentTimeMillis()

        emit(CompilationStatus.Preparing("Setting up compilation environment..."))

        try {
            // Validate project
            val projectDir = File(project.path)
            if (!projectDir.exists()) {
                throw IllegalArgumentException("Project directory not found")
            }

            val mainFile = File(project.getMainFilePath())
            if (!mainFile.exists()) {
                throw IllegalArgumentException("Main sketch file not found")
            }

            // Create build directory
            val buildDir = File(project.getBuildDirectory())
            buildDir.mkdirs()

            emit(CompilationStatus.InProgress(10, "Compiling sketch..."))

            // Execute compilation via Arduino CLI
            val result = arduinoCLIWrapper.compile(
                sketchPath = projectDir.absolutePath,
                fqbn = board.fqbn,
                buildPath = buildDir.absolutePath,
                onProgress = { progress, message ->
                    // Emit progress updates
                }
            )

            val duration = System.currentTimeMillis() - startTime

            if (result.success) {
                // Find the generated hex file
                val hexFile = buildDir.listFiles()?.firstOrNull { it.extension == "hex" }
                    ?: throw IllegalStateException("Compilation succeeded but no hex file found")

                emit(CompilationStatus.Success(
                    hexFile = hexFile.absolutePath,
                    binarySize = result.binarySize,
                    dataSize = result.dataSize,
                    duration = duration
                ))
            } else {
                // Parse errors
                val errors = errorParser.parse(result.output)
                emit(CompilationStatus.Error(
                    message = "Compilation failed with ${errors.size} error(s)",
                    errors = errors,
                    duration = duration
                ))
            }

        } catch (e: Exception) {
            Logger.e("Compilation error", e)
            val duration = System.currentTimeMillis() - startTime
            emit(CompilationStatus.Error(
                message = e.message ?: "Compilation failed",
                duration = duration
            ))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun installCore(corePlatform: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            arduinoCLIWrapper.installCore(corePlatform)
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.e("Failed to install core: $corePlatform", e)
            Result.failure(e)
        }
    }

    suspend fun installLibrary(libraryName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            arduinoCLIWrapper.installLibrary(libraryName)
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.e("Failed to install library: $libraryName", e)
            Result.failure(e)
        }
    }

    suspend fun listInstalledLibraries(): Result<List<Library>> = withContext(Dispatchers.IO) {
        try {
            val libraries = arduinoCLIWrapper.listLibraries()
            Result.success(libraries)
        } catch (e: Exception) {
            Logger.e("Failed to list libraries", e)
            Result.failure(e)
        }
    }

    private fun getDeviceArchitecture(): String {
        val arch = System.getProperty("os.arch") ?: ""
        return when {
            arch.contains("aarch64") || arch.contains("arm64") -> "arm64"
            arch.contains("arm") -> "arm"
            else -> "arm64" // default to arm64
        }
    }
}

sealed class ToolchainInstallStatus {
    data class Downloading(val progress: Int) : ToolchainInstallStatus()
    object Installing : ToolchainInstallStatus()
    object Success : ToolchainInstallStatus()
    data class Error(val message: String) : ToolchainInstallStatus()
}
