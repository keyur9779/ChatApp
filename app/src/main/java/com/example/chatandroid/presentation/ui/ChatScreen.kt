package com.example.chatandroid.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
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
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
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
    
    // Scroll to the bottom when new messages are received
    LaunchedEffect(uiState.chats) {
        uiState.selectedChatId?.let { chatId ->
            val chat = uiState.chats.find { it.id == chatId }
            chat?.let {
                if (chat.messages.isNotEmpty()) {
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
                    Text(
                        text = uiState.chats.find { it.id == uiState.selectedChatId }?.name
                            ?: "WebSocket Chat"
                    )
                },
                actions = {
                    IconButton(onClick = { showApiKeyDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "API Settings")
                    }
                    
                    // Simulate online/offline status (for testing offline functionality)
                    Switch(
                        checked = uiState.isOnline,
                        onCheckedChange = { viewModel.setOnlineStatus(it) }
                    )
                }
            )
        },
        floatingActionButton = {
            if (uiState.isConnected) {
                FloatingActionButton(
                    onClick = { showNewChatDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Chat")
                }
            }
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
                errorMessage = uiState.errorMessage
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
                        
                        Button(onClick = { showApiKeyDialog = true }) {
                            Text("Connect to Chat Server")
                        }
                    }
                }
            } else {
                // Show chat interface
                Row(modifier = Modifier.fillMaxSize()) {
                    // Chat list (conversations)
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            if (uiState.chats.isEmpty()) {
                                item {
                                    // Empty state for no chats
                                    EmptyStateView(isConnected = uiState.isConnected)
                                }
                            } else {
                                items(uiState.chats, key = { it.id }) { chat ->
                                    ChatListItem(
                                        chat = chat,
                                        isSelected = chat.id == uiState.selectedChatId,
                                        onClick = { 
                                            viewModel.selectChat(chat.id)
                                            viewModel.markMessagesAsRead()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Selected chat detail view
                uiState.selectedChatId?.let { chatId ->
                    val selectedChat = uiState.chats.find { it.id == chatId }
                    selectedChat?.let { chat ->
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(2f)
                        ) {
                            // Messages
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                reverseLayout = false
                            ) {
                                if (chat.messages.isEmpty()) {
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
                                    items(chat.messages, key = { it.id }) { message ->
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
                                    maxLines = 3
                                )
                                
                                IconButton(
                                    onClick = {
                                        if (messageText.isNotBlank()) {
                                            viewModel.sendMessage(messageText)
                                            messageText = ""
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Dialogs
    ApiKeyDialog(
        isVisible = showApiKeyDialog,
        onConnect = { apiKey ->
            viewModel.connectToChat(apiKey)
            showApiKeyDialog = false
            // Create default chat if none exists
            if (uiState.chats.isEmpty()) {
                viewModel.createChat("General")
            }
        },
        onDismiss = { showApiKeyDialog = false }
    )
    
    NewChatDialog(
        isVisible = showNewChatDialog,
        onDismiss = { showNewChatDialog = false },
        onCreateChat = { chatName ->
            val chat = viewModel.createChat(chatName)
            viewModel.selectChat(chat.id)
            showNewChatDialog = false
        }
    )
}
