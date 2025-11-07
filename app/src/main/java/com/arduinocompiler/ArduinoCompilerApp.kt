package com.arduinocompiler

import android.app.Application
import com.arduinocompiler.util.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ArduinoCompilerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Logger.setDebugMode(BuildConfig.DEBUG)
        Logger.i("Arduino Compiler App started")
    }

    override fun onTerminate() {
        super.onTerminate()
        Logger.i("Arduino Compiler App terminated")
    }
}
