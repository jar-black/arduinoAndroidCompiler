# Arduino Android Compiler - Architecture Document

## Implementation Status

✅ **Phase 1: COMPLETE** - Foundation and project setup
🔄 **Phase 2-8: IN PROGRESS** - Full implementation

## Overview

This document outlines the complete architecture of the Arduino Android Compiler application. The app enables on-device Arduino sketch compilation and uploading via USB OTG on Android devices.

## Technology Stack

- **Language**: Kotlin 1.9.20
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt (Dagger)
- **Database**: Room
- **Preferences**: DataStore
- **Async**: Coroutines + Flow
- **USB**: usb-serial-for-android library
- **Build**: Gradle 8.2 with Kotlin DSL

## Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                     │
│              (Jetpack Compose UI + ViewModels)           │
├─────────────────────────────────────────────────────────┤
│                     Domain Layer                         │
│           (Use Cases, Business Logic, Models)            │
├─────────────────────────────────────────────────────────┤
│                      Data Layer                          │
│     (Repositories, Data Sources, Database, Services)     │
├─────────────────────────────────────────────────────────┤
│                   Infrastructure Layer                   │
│    (Arduino CLI, USB Hardware, File System, Network)     │
└─────────────────────────────────────────────────────────┘
```

## Core Components

### 1. Compilation System

#### ArduinoCLIWrapper
- Manages Arduino CLI binary execution
- Handles toolchain installation
- Board platform management
- Library installation

#### CompilerService
- Orchestrates compilation process
- Progress tracking via Kotlin Flow
- Error parsing and reporting
- Build artifact management

#### CompilationErrorParser
- Parses GCC/Clang error messages
- Extracts file, line, column information
- Categorizes errors vs warnings
- Provides user-friendly messages

### 2. USB Communication System

#### UsbDeviceManager
- Detects connected Arduino devices
- Manages USB permissions
- Device-to-board matching
- Connection lifecycle management

#### UsbSerialService
- Serial port communication
- DTR/RTS control for reset
- Read/write with flow control
- Timeout and error handling

### 3. Upload System

#### UploadService
- Coordinates upload process
- Protocol selection (AVR/ESP)
- Progress tracking
- Verification

#### STK500Protocol
- Implements STK500v1/v2 for AVR boards
- Programmer communication
- Bootloader interaction

#### ESPToolProtocol
- ESP32/ESP8266 upload
- Stub loader execution
- Flash memory management

### 4. Serial Monitor System

#### SerialMonitorService
- Real-time serial data streaming
- Configurable baud rates
- Line ending handling
- Log persistence

### 5. Project Management System

#### ProjectFileService
- Sketch file management
- Multi-file support (.ino, .cpp, .h)
- Import/Export functionality
- Template management

#### ProjectRepository
- CRUD operations on projects
- Metadata persistence
- Board association
- File path management

### 6. Board Management System

#### BoardRepository
- Board definition storage
- Platform installation tracking
- Board-device matching
- Core package management

## Data Flow

### Compilation Flow
```
User triggers compile
    ↓
CompileViewModel.compile()
    ↓
CompilerRepository.compileProject()
    ↓
CompilerService.compile()
    ↓
ArduinoCLIWrapper.executeCompile()
    ↓
Process execution with Arduino CLI
    ↓
CompilationErrorParser.parse()
    ↓
Result emitted via Flow
    ↓
UI updates with status
```

### Upload Flow
```
User selects device and triggers upload
    ↓
UploadViewModel.upload()
    ↓
UploadRepository.uploadFirmware()
    ↓
UsbDeviceManager.requestPermission()
    ↓
UsbSerialService.connect()
    ↓
UploadService.upload()
    ↓
STK500Protocol/ESPToolProtocol.writeFlash()
    ↓
Progress emitted via Flow
    ↓
UI shows upload progress
```

### Serial Monitor Flow
```
User starts monitoring
    ↓
MonitorViewModel.startMonitoring()
    ↓
SerialMonitorService.connect()
    ↓
Continuous data reading
    ↓
Data emitted via Flow
    ↓
UI displays messages
```

## Dependency Injection Structure

### AppModule
- Application context
- System services (UsbManager)
- Coroutine dispatchers
- AppPreferences

### DatabaseModule
- Room database
- DAOs (ProjectDao, BoardDao)

### ServiceModule
- CompilerService
- UsbSerialService
- UploadService
- SerialMonitorService
- ProjectFileService

### RepositoryModule
- ProjectRepository
- BoardRepository
- CompilerRepository
- UploadRepository

## State Management

All ViewModels expose UI state via StateFlow/Flow:

```kotlin
data class CompileUiState(
    val status: CompilationStatus = CompilationStatus.Idle,
    val progress: Int = 0,
    val output: List<String> = emptyList(),
    val errors: List<CompilationError> = emptyList()
)
```

## Error Handling Strategy

1. **Service Layer**: Throws specific exceptions
2. **Repository Layer**: Catches and wraps in Result<T>
3. **ViewModel Layer**: Converts to UI-friendly messages
4. **UI Layer**: Displays error dialogs/snackbars

## Testing Strategy

### Unit Tests
- ViewModels (business logic)
- Repositories (data operations)
- Parsers (error parsing)
- Utilities (extensions, formatters)

### Integration Tests
- Full compilation pipeline
- USB communication
- Upload process
- Database operations

### UI Tests
- Navigation flows
- User interactions
- State updates
- Error scenarios

## Performance Considerations

1. **Compilation**: Off main thread via IO dispatcher
2. **USB**: Separate thread with flow-based updates
3. **Database**: All operations use suspend functions
4. **UI**: Compose recomposition optimization
5. **Memory**: LRU caching for build artifacts

## Security Considerations

1. **File Access**: Scoped storage (Android 10+)
2. **USB**: Explicit permission requests
3. **Network**: Only for downloading toolchain/boards
4. **Data**: No sensitive information collected

## Supported Boards

### AVR Architecture
- Arduino Uno (ATmega328P)
- Arduino Nano (ATmega328P)
- Arduino Mega 2560 (ATmega2560)
- Arduino Leonardo (ATmega32U4)
- Arduino Micro (ATmega32U4)

### ESP Architecture
- ESP32 Dev Module
- ESP32-S2
- ESP32-S3
- ESP32-C3
- ESP8266 Generic
- NodeMCU

### Extensibility
The architecture supports adding new boards by:
1. Adding board definition to database
2. Ensuring core platform is installed
3. Implementing upload protocol if needed

## File Structure

```
app/src/main/java/com/arduinocompiler/
├── data/
│   ├── database/           # Room database, DAOs
│   └── preferences/        # DataStore preferences
├── di/                     # Hilt modules
├── model/                  # Data models
├── repository/             # Data repositories
├── service/
│   ├── compiler/           # Compilation services
│   ├── upload/             # Upload services
│   ├── usb/                # USB communication
│   ├── monitor/            # Serial monitoring
│   └── filesystem/         # File management
├── ui/
│   ├── screens/            # Compose screens
│   ├── components/         # Reusable UI components
│   ├── theme/              # Material theme
│   └── navigation/         # Navigation graph
├── util/                   # Utilities and extensions
├── viewmodel/              # ViewModels
├── ArduinoCompilerApp.kt   # Application class
└── MainActivity.kt         # Main activity
```

## Build Configuration

- **Debug**: Full logging, no minification
- **Release**: ProGuard enabled, optimized, signed

## Future Enhancements

1. Cloud compilation for faster builds
2. OTA updates for ESP boards
3. Git integration for version control
4. Collaborative editing
5. Bluetooth serial monitor
6. Custom board definitions
7. Graphical serial plotter
8. Breakpoint debugging support

## References

- [Arduino CLI Documentation](https://arduino.github.io/arduino-cli/)
- [usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Arduino Build Process](https://arduino.github.io/arduino-cli/latest/sketch-build-process/)

---

**Last Updated**: 2025-11-07
**Version**: 1.0.0
**Status**: Phase 1 Complete, Phases 2-8 In Progress
