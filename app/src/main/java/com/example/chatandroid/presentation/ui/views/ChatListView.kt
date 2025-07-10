package com.example.chatandroid.presentation.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatandroid.data.model.Chat
import com.example.chatandroid.presentation.ui.components.ChatListItem
import com.example.chatandroid.presentation.ui.components.EmptyStateView

/**
 * Composable for displaying the chat list view.
 *
 * @param chats List of available chats
 * @param selectedChatId ID of the currently selected chat
 * @param onChatSelected Callback for when a chat is selected
 * @param onNewChat Callback for when the user wants to create a new chat
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListView(
    chats: List<Chat>,
    selectedChatId: String?,
    onChatSelected: (String) -> Unit,
    onNewChat: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Chat list header
        TopAppBar(
            title = { Text("Conversations") },
            actions = {
                // New chat button
                TextButton(
                    onClick = onNewChat
                ) {
                    Text("New Chat")
                }
            }
        )
        
        if (chats.isEmpty()) {
            // Empty state
            EmptyStateView(isConnected = true)
        } else {
            // Chat list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
            ) {
                items(chats, key = { it.id }) { chat ->
                    ChatListItem(
                        chat = chat,
                        isSelected = chat.id == selectedChatId,
                        onClick = { onChatSelected(chat.id) }
                    )
                }
            }
        }
    }
}
