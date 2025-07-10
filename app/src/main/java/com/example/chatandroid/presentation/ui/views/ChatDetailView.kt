package com.example.chatandroid.presentation.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatandroid.data.model.Message
import com.example.chatandroid.presentation.ui.components.ConnectionStatusIndicator
import com.example.chatandroid.presentation.ui.components.EmptyStateView
import com.example.chatandroid.presentation.ui.components.MessageItem
import com.example.chatandroid.presentation.ui.components.TypingIndicator
import kotlinx.coroutines.launch

/**
 * Composable for displaying the chat detail view.
 *
 * @param messages List of messages in this chat
 * @param chatName Name of the chat
 * @param isConnected Whether the app is connected to the chat service
 * @param isTyping Whether someone is typing
 * @param typingUser Name of the user who is typing
 * @param errorMessage Error message to display
 * @param showErrorMessage Whether to show the error message
 * @param onBackClick Callback for when the back button is clicked
 * @param onSendMessage Callback for when a message is sent
 * @param onLikeMessage Callback for when a message is liked
 * @param onRetryMessage Callback for when a failed message should be retried
 * @param onDismissError Callback for when the error message is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailView(
    messages: List<Message>,
    chatName: String,
    isConnected: Boolean,
    isTyping: Boolean = false,
    typingUser: String = "",
    errorMessage: String = "",
    showErrorMessage: Boolean = false,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onLikeMessage: (String) -> Unit,
    onRetryMessage: (String) -> Unit,
    onDismissError: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Chat header
        TopAppBar(
            title = { Text(chatName) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                ConnectionStatusIndicator(
                    isConnected = isConnected,
                    error = errorMessage,
                    isErrorVisible = showErrorMessage,
                    onDismissError = onDismissError
                )
            }
        )
        
        // Messages
        if (messages.isEmpty()) {
            EmptyStateView(isConnected = isConnected)
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = false
                ) {
                    items(messages, key = { it.id }) { message ->
                        MessageItem(
                            message = message,
                            onLikeMessage = onLikeMessage,
                            onRetryMessage = onRetryMessage
                        )
                    }
                }
            }
        }
        
        // Typing indicator
        TypingIndicator(
            isTyping = isTyping,
            userName = typingUser
        )
        
        // Message input
        Surface(
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Message input field
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Send button
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText)
                            messageText = ""
                            // Scroll to bottom when sending a message
                            coroutineScope.launch {
                                if (messages.isNotEmpty()) {
                                    listState.animateScrollToItem(messages.size)
                                }
                            }
                        }
                    },
                    enabled = messageText.isNotBlank() && isConnected
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Message",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
