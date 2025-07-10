package com.example.chatandroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Chat application.
 * Initialized with Hilt for dependency injection.
 */
@HiltAndroidApp
class ChatApplication : Application()
{
    override fun onCreate() {
        super.onCreate()
    }
}
