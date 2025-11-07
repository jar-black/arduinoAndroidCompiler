package com.arduinocompiler.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boards")
data class BoardDefinition(
    @PrimaryKey
    val fqbn: String, // Fully Qualified Board Name (e.g., "arduino:avr:uno")
    val name: String,
    val platform: String, // e.g., "arduino:avr"
    val architecture: String, // e.g., "avr", "esp32"
    val processorType: String, // e.g., "atmega328p", "esp32"
    val uploadProtocol: String, // e.g., "arduino", "esptool"
    val uploadSpeed: Int = 115200,
    val maximumSize: Int = 0, // Flash size in bytes
    val maximumDataSize: Int = 0, // RAM size in bytes
    val vid: String = "", // USB Vendor ID
    val pid: String = "", // USB Product ID
    val requiresCore: String = "", // Core package required (e.g., "arduino:avr")
    val additionalUrls: String = "", // Additional board manager URLs
    val isInstalled: Boolean = false
) {
    fun getDisplayName(): String {
        return name
    }

    fun getPlatformPackage(): String {
        return platform
    }

    fun isESP(): Boolean {
        return architecture.contains("esp", ignoreCase = true)
    }

    fun isAVR(): Boolean {
        return architecture.equals("avr", ignoreCase = true)
    }

    fun isSAMD(): Boolean {
        return architecture.equals("samd", ignoreCase = true)
    }

    companion object {
        // Pre-defined popular boards
        fun getDefaultBoards(): List<BoardDefinition> {
            return listOf(
                BoardDefinition(
                    fqbn = "arduino:avr:uno",
                    name = "Arduino Uno",
                    platform = "arduino:avr",
                    architecture = "avr",
                    processorType = "atmega328p",
                    uploadProtocol = "arduino",
                    uploadSpeed = 115200,
                    maximumSize = 32256,
                    maximumDataSize = 2048,
                    vid = "2341",
                    pid = "0043",
                    requiresCore = "arduino:avr",
                    isInstalled = true
                ),
                BoardDefinition(
                    fqbn = "arduino:avr:nano",
                    name = "Arduino Nano",
                    platform = "arduino:avr",
                    architecture = "avr",
                    processorType = "atmega328p",
                    uploadProtocol = "arduino",
                    uploadSpeed = 57600,
                    maximumSize = 30720,
                    maximumDataSize = 2048,
                    vid = "2341",
                    pid = "0043",
                    requiresCore = "arduino:avr",
                    isInstalled = true
                ),
                BoardDefinition(
                    fqbn = "arduino:avr:mega",
                    name = "Arduino Mega 2560",
                    platform = "arduino:avr",
                    architecture = "avr",
                    processorType = "atmega2560",
                    uploadProtocol = "wiring",
                    uploadSpeed = 115200,
                    maximumSize = 253952,
                    maximumDataSize = 8192,
                    vid = "2341",
                    pid = "0042",
                    requiresCore = "arduino:avr",
                    isInstalled = true
                ),
                BoardDefinition(
                    fqbn = "arduino:avr:leonardo",
                    name = "Arduino Leonardo",
                    platform = "arduino:avr",
                    architecture = "avr",
                    processorType = "atmega32u4",
                    uploadProtocol = "avr109",
                    uploadSpeed = 57600,
                    maximumSize = 28672,
                    maximumDataSize = 2560,
                    vid = "2341",
                    pid = "8036",
                    requiresCore = "arduino:avr",
                    isInstalled = true
                ),
                BoardDefinition(
                    fqbn = "esp32:esp32:esp32",
                    name = "ESP32 Dev Module",
                    platform = "esp32:esp32",
                    architecture = "esp32",
                    processorType = "esp32",
                    uploadProtocol = "esptool",
                    uploadSpeed = 921600,
                    maximumSize = 1310720,
                    maximumDataSize = 327680,
                    vid = "10c4",
                    pid = "ea60",
                    requiresCore = "esp32:esp32",
                    additionalUrls = "https://raw.githubusercontent.com/espressif/arduino-esp32/gh-pages/package_esp32_index.json"
                ),
                BoardDefinition(
                    fqbn = "esp8266:esp8266:generic",
                    name = "ESP8266 Generic",
                    platform = "esp8266:esp8266",
                    architecture = "esp8266",
                    processorType = "esp8266",
                    uploadProtocol = "esptool",
                    uploadSpeed = 115200,
                    maximumSize = 1044464,
                    maximumDataSize = 81920,
                    vid = "1a86",
                    pid = "7523",
                    requiresCore = "esp8266:esp8266",
                    additionalUrls = "http://arduino.esp8266.com/stable/package_esp8266com_index.json"
                )
            )
        }
    }
}
