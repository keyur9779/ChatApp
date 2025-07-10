package com.example.chatandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.chatandroid.presentation.ui.ChatScreen
import com.example.chatandroid.presentation.viewmodel.ChatViewModel
import com.example.chatandroid.ui.theme.ChatAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the chat application.
 * Uses Hilt for dependency injection and hosts the ChatScreen composable.
 * Clears chat history when app is closed.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAndroidTheme {
                ChatScreen(viewModel = viewModel)
            }
        }
    }
    
    override fun onDestroy() {
        // Clear all chats when app is closed
        viewModel.clearAllChats()
        super.onDestroy()
    }
}
