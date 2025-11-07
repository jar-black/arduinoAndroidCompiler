package com.arduinocompiler.model

sealed class UploadStatus {
    object Idle : UploadStatus()
    data class Preparing(val message: String = "Preparing upload...") : UploadStatus()
    data class Connecting(val message: String = "Connecting to device...") : UploadStatus()
    data class InProgress(
        val progress: Int = 0,
        val bytesWritten: Long = 0,
        val totalBytes: Long = 0,
        val message: String = "Uploading..."
    ) : UploadStatus()
    data class Verifying(val message: String = "Verifying upload...") : UploadStatus()
    data class Success(
        val duration: Long,
        val message: String = "Upload successful"
    ) : UploadStatus()
    data class Error(
        val message: String,
        val errorType: UploadErrorType = UploadErrorType.UNKNOWN
    ) : UploadStatus()
}

enum class UploadErrorType {
    DEVICE_NOT_FOUND,
    PERMISSION_DENIED,
    CONNECTION_FAILED,
    UPLOAD_FAILED,
    VERIFICATION_FAILED,
    TIMEOUT,
    UNKNOWN
}

data class UsbDevice(
    val deviceName: String,
    val deviceId: Int,
    val vendorId: Int,
    val productId: Int,
    val manufacturer: String?,
    val product: String?,
    val serialNumber: String?,
    val deviceClass: Int,
    val interfaceCount: Int
) {
    fun getDisplayName(): String {
        return product ?: manufacturer ?: "Unknown Device (${vendorId.toHexString()}:${productId.toHexString()})"
    }

    fun getDescription(): String {
        return buildString {
            if (manufacturer != null) append(manufacturer)
            if (product != null) {
                if (isNotEmpty()) append(" - ")
                append(product)
            }
            if (serialNumber != null) {
                if (isNotEmpty()) append(" ")
                append("(SN: $serialNumber)")
            }
            if (isEmpty()) {
                append("VID:${vendorId.toHexString()} PID:${productId.toHexString()}")
            }
        }
    }

    private fun Int.toHexString(): String {
        return String.format("%04X", this)
    }

    fun matchesBoard(board: BoardDefinition): Boolean {
        if (board.vid.isBlank() || board.pid.isBlank()) return false
        val boardVid = board.vid.toIntOrNull(16) ?: return false
        val boardPid = board.pid.toIntOrNull(16) ?: return false
        return vendorId == boardVid && productId == boardPid
    }
}

data class UploadConfig(
    val hexFilePath: String,
    val board: BoardDefinition,
    val device: UsbDevice,
    val verifyAfterUpload: Boolean = true,
    val eraseFlash: Boolean = false
)
