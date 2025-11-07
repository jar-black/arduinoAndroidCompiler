# Arduino Android Compiler

An Android application that enables compiling Arduino projects and uploading them to Arduino devices connected via USB, all directly from your Android phone or tablet.

## 🚀 Features

- **📝 Project Management** - Create, edit, and organize Arduino sketches on your device
- **⚙️ On-Device Compilation** - Compile Arduino code directly on Android using Arduino CLI
- **📤 USB Upload** - Upload compiled firmware to Arduino boards via USB OTG
- **📊 Serial Monitor** - Monitor and send data to your Arduino in real-time
- **🎨 Modern UI** - Built with Jetpack Compose and Material Design 3
- **📱 Multiple Boards** - Support for Uno, Nano, Mega, ESP32, and more

## 📋 Requirements

### Android Device
- Android 7.0 (API 24) or higher
- USB OTG (Host mode) support
- Minimum 2GB RAM (4GB recommended)
- 200MB free storage

### Hardware
- USB OTG adapter/cable
- Supported Arduino board
- USB cable for Arduino

## 🛠️ Supported Boards (Planned)

- Arduino Uno
- Arduino Nano
- Arduino Mega 2560
- Arduino Leonardo
- ESP32
- ESP8266 (future)
- And more...

## 📖 Documentation

See [IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md) for the complete technical implementation plan, including:
- Detailed architecture
- Phase-by-phase development roadmap
- Technical challenges and solutions
- Timeline and milestones

## 🏗️ Architecture

The app follows clean architecture principles with MVVM pattern:

```
UI Layer (Jetpack Compose)
    ↓
ViewModel Layer
    ↓
Repository Layer
    ↓
Service Layer (Compiler, USB, Monitor)
```

## 🔧 Technology Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM
- **DI**: Hilt
- **Async**: Coroutines + Flow
- **Storage**: Room + File System
- **USB**: Android USB Host API + usb-serial-for-android

## 🚦 Project Status

**Status**: Planning & Initial Development

See [IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md) for the detailed 8-phase development plan.

## 🤝 Contributing

Contributions are welcome! This project is in early development. Check the implementation plan for areas where you can help.

## 📄 License

See [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Arduino CLI](https://github.com/arduino/arduino-cli) - Official Arduino command line tool
- [usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android) - USB serial communication library
- Arduino community for making embedded programming accessible

## 💡 Inspiration

This project aims to make Arduino development truly mobile, allowing you to code, compile, and upload sketches anywhere without needing a laptop.

---

**Note**: This is an independent project and is not officially affiliated with Arduino LLC.
