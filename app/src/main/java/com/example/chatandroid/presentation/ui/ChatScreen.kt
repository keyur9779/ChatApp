package com.example.chatandroid.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.material3.BadgedBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatandroid.data.model.Chat
import com.example.chatandroid.data.model.Message
import com.example.chatandroid.presentation.ui.components.*
import com.example.chatandroid.presentation.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

/**
 * Main composable function for the chat interface.
 * 
 * @param viewModel ViewModel that manages the chat state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    // Auto-connect with delay when the screen is first loaded
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1500)
        val apiKey = "2ZilnOus6sDs7od7bbVYQgx8LlgAfabf7yGLJlUt"
        viewModel.connectToChat(apiKey)
        
        // Wait for connection to be established
        kotlinx.coroutines.delay(500)
        
        // Always create a single chat and select it
        val chat = viewModel.createChat("Chat")
        viewModel.selectChat(chat.id)
    }
    
    LaunchedEffect(uiState.isNetworkError) {
        if (uiState.isNetworkError) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Network error: You're offline",
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short
                )
                viewModel.dismissNetworkError()
            }
        }
    }
    
    // Scroll to the bottom when new messages are received or sent
    LaunchedEffect(uiState.chats, messageText) {
        uiState.selectedChatId?.let { chatId ->
            val chat = uiState.chats.find { it.id == chatId }
            chat?.let {
                if (chat.messages.isNotEmpty()) {
                    // Use a small delay to ensure the UI is updated before scrolling
                    kotlinx.coroutines.delay(100)
                    listState.animateScrollToItem(chat.messages.size - 1)
                }
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("WebSocket Chat")
                },
                actions = {
                    // Simulate online/offline status (for testing offline functionality)
                    Switch(
                        checked = uiState.isOnline,
                        onCheckedChange = { viewModel.setOnlineStatus(it) }
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Network status bar
            NetworkStatusBar(
                isOnline = uiState.isOnline,
                isConnected = uiState.isConnected,
                showNetworkError = uiState.showNetworkError,
                showConnectionError = uiState.showConnectionError,
                errorMessage = uiState.errorMessage,
                hasQueuedMessages = uiState.queuedMessages.isNotEmpty(),
                onRetryClicked = { 
                    if (uiState.isOnline) {
                        viewModel.retryQueuedMessages() 
                    }
                }
            )

            // Content based on connection status
            if (!uiState.isConnected) {
                // Show connection prompt
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome to WebSocket Chat",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Connect button removed as we're auto-connecting now
                        Text(
                            text = "Connecting to chat server...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                // Show single chat interface
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Determine which chat to display (use the first/default chat)
                    val chat = if (uiState.selectedChatId != null) {
                        uiState.chats.find { it.id == uiState.selectedChatId }
                    } else {
                        uiState.chats.firstOrNull()?.also {
                            // Auto-select the first chat if none is selected
                            viewModel.selectChat(it.id)
                        }
                    }

                    // Messages
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        reverseLayout = false
                    ) {
                        if (chat?.messages.isNullOrEmpty()) {
                            item {
                                // Empty state for no messages
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No messages yet. Start the conversation!")
                                }
                            }
                        } else {
                            chat?.let {
                                items(it.messages, key = { msg -> msg.id }) { message ->
                                    MessageItem(
                                        message = message,
                                        onLikeMessage = { messageId ->
                                            // Handle like action if needed
                                        },
                                        onRetryMessage = { messageId ->
                                            viewModel.retryMessage(messageId)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Typing indicator
                    TypingIndicator(
                        isTyping = uiState.isTyping,
                        userName = uiState.typingUser
                    )
                    
                    // Message input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("Type a message") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            maxLines = 3,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (messageText.isNotBlank() && uiState.selectedChatId != null) {
                                        viewModel.sendMessage(messageText)
                                        messageText = ""
                                    }
                                }
                            )
                        )
                        
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank() && uiState.selectedChatId != null) {
                                    viewModel.sendMessage(messageText)
                                    messageText = ""
                                }
                            },
                            enabled = messageText.isNotBlank()
                        ) {
                            if (uiState.isOnline) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = if (messageText.isNotBlank()) 
                                        MaterialTheme.colorScheme.primary
                                    else 
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                )
                            } else {
                                // Show an indicator that the message will be queued
                                BadgedBox(
                                    badge = {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.error,
                                                    shape = CircleShape
                                                )
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send (Offline Mode)",
                                        tint = if (messageText.isNotBlank()) 
                                            MaterialTheme.colorScheme.primary
                                        else 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            }
        }

        // No dialogs needed for single chat view
    }
