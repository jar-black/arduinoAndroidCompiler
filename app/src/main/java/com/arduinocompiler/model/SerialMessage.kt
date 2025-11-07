package com.arduinocompiler.model

import java.util.Date

data class SerialMessage(
    val data: String,
    val timestamp: Date = Date(),
    val direction: Direction = Direction.RECEIVED,
    val type: MessageType = MessageType.TEXT
) {
    enum class Direction {
        SENT,
        RECEIVED
    }

    enum class MessageType {
        TEXT,
        HEX,
        BINARY
    }

    fun getFormattedMessage(showTimestamp: Boolean = true): String {
        val prefix = if (direction == Direction.SENT) ">> " else "<< "
        val timeStr = if (showTimestamp) {
            "[${timestamp.formatTime()}] "
        } else ""
        return "$timeStr$prefix$data"
    }

    private fun Date.formatTime(): String {
        val hours = this.hours.toString().padStart(2, '0')
        val minutes = this.minutes.toString().padStart(2, '0')
        val seconds = this.seconds.toString().padStart(2, '0')
        return "$hours:$minutes:$seconds"
    }
}

data class SerialConfig(
    val baudRate: Int = 9600,
    val dataBits: Int = 8,
    val stopBits: Int = 1,
    val parity: Parity = Parity.NONE,
    val flowControl: FlowControl = FlowControl.NONE,
    val lineEnding: LineEnding = LineEnding.NEWLINE
) {
    enum class Parity {
        NONE,
        ODD,
        EVEN,
        MARK,
        SPACE
    }

    enum class FlowControl {
        NONE,
        HARDWARE,
        SOFTWARE
    }

    enum class LineEnding {
        NONE,
        NEWLINE,
        CARRIAGE_RETURN,
        BOTH
    }

    fun getLineEndingString(): String {
        return when (lineEnding) {
            LineEnding.NONE -> ""
            LineEnding.NEWLINE -> "\n"
            LineEnding.CARRIAGE_RETURN -> "\r"
            LineEnding.BOTH -> "\r\n"
        }
    }
}
