package com.arduinocompiler.util

object Constants {
    // Directories
    const val PROJECTS_DIR = "arduino_projects"
    const val TOOLCHAIN_DIR = "toolchain"
    const val BUILD_DIR = "build"
    const val LIBRARIES_DIR = "libraries"

    // Arduino CLI
    const val ARDUINO_CLI_BINARY = "arduino-cli"
    const val ARDUINO_CLI_VERSION = "0.35.2"

    // USB
    const val USB_PERMISSION_ACTION = "com.arduinocompiler.USB_PERMISSION"
    const val USB_READ_BUFFER_SIZE = 1024
    const val USB_WRITE_TIMEOUT_MS = 1000
    const val USB_READ_TIMEOUT_MS = 1000

    // Serial Monitor
    val BAUD_RATES = listOf(
        300, 1200, 2400, 4800, 9600, 19200, 38400, 57600,
        74880, 115200, 230400, 250000, 500000, 1000000, 2000000
    )
    const val DEFAULT_BAUD_RATE = 9600

    // Compilation
    const val MAX_COMPILATION_TIME_MS = 300000L // 5 minutes
    const val COMPILATION_BUFFER_SIZE = 4096

    // Upload
    const val MAX_UPLOAD_TIME_MS = 60000L // 1 minute
    const val UPLOAD_RETRY_ATTEMPTS = 3
    const val UPLOAD_RETRY_DELAY_MS = 1000L

    // File extensions
    const val EXTENSION_INO = ".ino"
    const val EXTENSION_CPP = ".cpp"
    const val EXTENSION_H = ".h"
    const val EXTENSION_HEX = ".hex"
    const val EXTENSION_ELF = ".elf"

    // Board IDs (Arduino CLI format)
    object BoardIds {
        const val UNO = "arduino:avr:uno"
        const val NANO = "arduino:avr:nano"
        const val MEGA = "arduino:avr:mega"
        const val LEONARDO = "arduino:avr:leonardo"
        const val ESP32 = "esp32:esp32:esp32"
        const val ESP8266 = "esp8266:esp8266:generic"
        const val MICRO = "arduino:avr:micro"
        const val MINI = "arduino:avr:mini"
        const val PRO = "arduino:avr:pro"
    }

    // Processor types
    object ProcessorTypes {
        const val AVR = "avr"
        const val ESP32 = "esp32"
        const val ESP8266 = "esp8266"
        const val ARM = "arm"
        const val SAMD = "samd"
    }

    // Upload protocols
    object UploadProtocols {
        const val ARDUINO = "arduino"
        const val STK500V1 = "stk500v1"
        const val STK500V2 = "stk500v2"
        const val AVRDUDE = "avrdude"
        const val ESPTOOL = "esptool"
        const val BOSSAC = "bossac"
    }

    // Core packages
    object CorePackages {
        const val AVR = "arduino:avr"
        const val ESP32 = "esp32:esp32"
        const val ESP8266 = "esp8266:esp8266"
        const val SAMD = "arduino:samd"
    }

    // Additional board URLs
    object BoardUrls {
        const val ESP32 = "https://raw.githubusercontent.com/espressif/arduino-esp32/gh-pages/package_esp32_index.json"
        const val ESP8266 = "http://arduino.esp8266.com/stable/package_esp8266com_index.json"
    }

    // Preferences keys
    object PrefsKeys {
        const val DEFAULT_BOARD = "default_board"
        const val DEFAULT_BAUD_RATE = "default_baud_rate"
        const val EDITOR_FONT_SIZE = "editor_font_size"
        const val EDITOR_THEME = "editor_theme"
        const val TOOLCHAIN_INSTALLED = "toolchain_installed"
        const val FIRST_RUN = "first_run"
    }

    // Error messages
    object ErrorMessages {
        const val NO_PROJECT = "No project selected"
        const val NO_DEVICE = "No device connected"
        const val COMPILATION_FAILED = "Compilation failed"
        const val UPLOAD_FAILED = "Upload failed"
        const val USB_PERMISSION_DENIED = "USB permission denied"
        const val FILE_NOT_FOUND = "File not found"
        const val INVALID_BOARD = "Invalid board configuration"
        const val TOOLCHAIN_NOT_INSTALLED = "Arduino toolchain not installed"
    }
}
