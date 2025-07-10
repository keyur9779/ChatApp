package com.example.chatandroid.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatandroid.data.model.Chat as ChatModel
import com.example.chatandroid.data.model.Message
import com.example.chatandroid.presentation.ui.components.ChatListItem
import com.example.chatandroid.presentation.ui.components.MessageItem
import com.example.chatandroid.presentation.ui.components.NetworkStatusBar
import com.example.chatandroid.ui.theme.ChatAndroidTheme
import java.util.UUID

/**
 * Preview screen that showcases all UI components with mock data
 */
@Composable
fun ChatPreviewScreen() {
    ChatAndroidTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Preview network status bar in different states
                NetworkStatusBarPreviews()
                
                Divider()
                
                // Preview chat list with mock chats
                Text(
                    text = "Chat List Preview",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                ChatListPreview()
                
                Divider()
                
                // Preview message items in a conversation
                Text(
                    text = "Messages Preview",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                MessageListPreview()
                
                Divider()
                
                // Chat input field preview
                Text(
                    text = "Input Field Preview",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                ChatInputPreview()
            }
        }
    }
}

@Composable
fun NetworkStatusBarPreviews() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Network Status Bars:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // Connected state
        NetworkStatusBar(
            isOnline = true,
            isConnected = true,
            showNetworkError = false,
            showConnectionError = false
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Connecting state
        NetworkStatusBar(
            isOnline = true,
            isConnected = false,
            showNetworkError = false,
            showConnectionError = false
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Offline state
        NetworkStatusBar(
            isOnline = false,
            isConnected = false,
            showNetworkError = true,
            showConnectionError = false
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Error state
        NetworkStatusBar(
            isOnline = true,
            isConnected = false,
            showNetworkError = false,
            showConnectionError = true,
            errorMessage = "Failed to connect: Server unavailable"
        )
    }
}

@Composable
fun ChatListPreview() {
    val mockMessages = listOf(
        Message(
            content = "Hey, how's your day going?",
            isSent = false,
            timestamp = System.currentTimeMillis() - 300000, // 5 minutes ago
            isRead = false
        ),
        Message(
            content = "Meeting starts in 5 minutes",
            isSent = true,
            timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
            isRead = true
        ),
        Message(
            content = "Thanks for your help with the issue",
            isSent = false,
            timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
            isRead = true
        )
    )
    
    val mockChats = listOf(
        ChatModel(
            id = UUID.randomUUID().toString(),
            name = "Alice Johnson",
            messages = listOf(mockMessages[0]),
            unreadCount = 2,
            lastUpdated = System.currentTimeMillis() - 300000
        ),
        ChatModel(
            id = UUID.randomUUID().toString(),
            name = "Team Standup",
            messages = listOf(mockMessages[1]),
            unreadCount = 0,
            lastUpdated = System.currentTimeMillis() - 3600000
        ),
        ChatModel(
            id = UUID.randomUUID().toString(),
            name = "Support Group",
            messages = listOf(mockMessages[2]),
            unreadCount = 0,
            lastUpdated = System.currentTimeMillis() - 86400000
        )
    )
    
    LazyColumn(
        modifier = Modifier.height(200.dp)
    ) {
        items(mockChats) { chat ->
            ChatListItem(
                chat = chat,
                isSelected = false,
                onClick = {}
            )
        }
    }
}

@Composable
fun MessageListPreview() {
    val mockMessages = listOf(
        Message(
            content = "Hey there! How are you doing?",
            isSent = false,
            timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
            isRead = true
        ),
        Message(
            content = "I'm doing well, thanks for asking! Just working on this new app.",
            isSent = true,
            timestamp = System.currentTimeMillis() - 3500000, // ~58 minutes ago
            isRead = true
        ),
        Message(
            content = "That sounds interesting. What kind of app is it?",
            isSent = false,
            timestamp = System.currentTimeMillis() - 3400000, // ~57 minutes ago
            isRead = true
        ),
        Message(
            content = "It's a real-time chat app with websocket communication and offline queuing.",
            isSent = true,
            timestamp = System.currentTimeMillis() - 3300000, // ~55 minutes ago
            isRead = true
        ),
        Message(
            content = "Just sent you the mockups too.",
            isSent = true,
            timestamp = System.currentTimeMillis() - 3200000, // ~53 minutes ago
            isSending = true,
            isRead = false
        )
    )
    
    LazyColumn(
        modifier = Modifier.height(300.dp)
    ) {
        items(mockMessages) { message ->
            MessageItem(
                message = message,
                onLikeMessage = {},
                onRetryMessage = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ChatInputPreview() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TextField(
            value = "Type a message...",
            onValueChange = {},
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Button(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text("Send")
        }
    }
}

@Preview(
    showBackground = true, 
    name = "Full UI Preview", 
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420",
    fontScale = 1.0f
)
@Composable
fun ChatPreviewScreenPreview() {
    ChatPreviewScreen()
}

@Preview(showBackground = true, name = "Network Status - Connected")
@Composable
fun NetworkStatusBarConnectedPreview() {
    NetworkStatusBar(
        isOnline = true,
        isConnected = true,
        showNetworkError = false,
        showConnectionError = false
    )
}

@Preview(showBackground = true, name = "Network Status - Offline")
@Composable
fun NetworkStatusBarOfflinePreview() {
    NetworkStatusBar(
        isOnline = false,
        isConnected = false,
        showNetworkError = false,
        showConnectionError = false
    )
}

@Preview(showBackground = true, name = "Chat List Item")
@Composable
fun ChatListItemPreview() {
    val mockMessage = Message(
        content = "Hey, how's your day going?",
        isSent = false,
        timestamp = System.currentTimeMillis() - 300000, // 5 minutes ago
        isRead = false
    )
    
    val chat = ChatModel(
        id = UUID.randomUUID().toString(),
        name = "Alice Johnson",
        messages = listOf(mockMessage),
        unreadCount = 2,
        lastUpdated = System.currentTimeMillis() - 300000
    )
    
    ChatListItem(
        chat = chat,
        isSelected = false,
        onClick = {}
    )
}

@Preview(showBackground = true, name = "Message Item - Received")
@Composable
fun MessageItemReceivedPreview() {
    val message = Message(
        content = "Hey there! How are you doing?",
        isSent = false,
        timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
        isRead = true
    )
    
    MessageItem(
        message = message,
        onLikeMessage = {},
        onRetryMessage = {}
    )
}

@Preview(showBackground = true, name = "Message Item - Sent")
@Composable
fun MessageItemSentPreview() {
    val message = Message(
        content = "I'm doing well, thanks for asking! Just working on this new app.",
        isSent = true,
        timestamp = System.currentTimeMillis() - 3500000, // ~58 minutes ago
        isRead = true
    )
    
    MessageItem(
        message = message,
        onLikeMessage = {},
        onRetryMessage = {}
    )
}

@Preview(showBackground = true, name = "Message Item - Error")
@Composable
fun MessageItemErrorPreview() {
    val message = Message(
        content = "This message failed to send.",
        isSent = true,
        timestamp = System.currentTimeMillis() - 1800000, // 30 minutes ago
        isError = true,
        isRead = false
    )
    
    MessageItem(
        message = message,
        onLikeMessage = {},
        onRetryMessage = {}
    )
}

@Preview(showBackground = true, name = "Chat Input Field")
@Composable
fun ChatInputFieldPreview() {
    ChatInputPreview()
}
