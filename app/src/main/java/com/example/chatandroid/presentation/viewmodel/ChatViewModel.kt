package com.example.chatandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatandroid.data.model.Chat
import com.example.chatandroid.data.model.Message
import com.example.chatandroid.domain.repository.ChatEvent
import com.example.chatandroid.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class for chat functionality.
 * Manages UI state and business logic for chat operations.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val connectToChatUseCase: ConnectToChatUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val isConnectedUseCase: IsConnectedUseCase,
    private val disconnectChatUseCase: DisconnectChatUseCase,
    private val getAllChatsUseCase: GetAllChatsUseCase,
    private val getMessageHistoryUseCase: GetMessageHistoryUseCase,
    private val getSelectedChatIdUseCase: GetSelectedChatIdUseCase,
    private val selectChatUseCase: SelectChatUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val clearAllChatsUseCase: ClearAllChatsUseCase,
    private val getNetworkStatusUseCase: GetNetworkStatusUseCase,
    private val setNetworkStatusUseCase: SetNetworkStatusUseCase,
    private val getQueuedMessagesUseCase: GetQueuedMessagesUseCase,
    private val retryQueuedMessagesUseCase: RetryQueuedMessagesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    init {
        observeChats()
        observeSelectedChat()
        observeNetworkStatus()
        observeQueuedMessages()
    }

    /**
     * Connects to the chat service with the given API key.
     * 
     * @param apiKey The API key for authentication
     */
    fun connectToChat(apiKey: String) {
        viewModelScope.launch {
            connectToChatUseCase(apiKey).collect { event ->
                when (event) {
                    is ChatEvent.Connected -> {
                        _uiState.update { 
                            it.copy(
                                isConnected = true,
                                errorMessage = "",
                                showConnectionError = false
                            )
                        }
                    }
                    is ChatEvent.MessageReceived -> {
                        // Message handling is now done in the repository
                        // We keep track of the last received message for UI purposes
                        _uiState.update {
                            it.copy(lastReceivedMessage = event.message.content)
                        }
                    }
                    is ChatEvent.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = event.message,
                                showConnectionError = true
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Sends a message to the chat.
     * 
     * @param messageContent The content of the message to send
     * @return Boolean indicating if the send operation was initiated
     */
    fun sendMessage(messageContent: String): Boolean {
        if (messageContent.isBlank()) return false
        
        val chatId = uiState.value.selectedChatId ?: return false
        
        viewModelScope.launch {
            // The repository will handle adding the message to the chat
            // and updating the UI through flows
            sendMessageUseCase(chatId, messageContent)
        }
        
        return true
    }

    /**
     * Checks if the application is connected to the chat.
     * 
     * @return Boolean indicating connection status
     */
    fun isConnected() = isConnectedUseCase()

    /**
     * Disconnects from the chat service.
     */
    fun disconnect() {
        disconnectChatUseCase()
        _uiState.update { 
            it.copy(
                isConnected = false,
                errorMessage = ""
            )
        }
    }

    /**
     * Clears any error messages in the UI state.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = "") }
    }
    
    /**
     * Clears all chat history.
     */
    fun clearAllChats() {
        clearAllChatsUseCase()
    }
    
    /**
     * Selects a chat.
     *
     * @param chatId ID of the chat to select
     */
    fun selectChat(chatId: String) {
        selectChatUseCase(chatId)
    }

    /**
     * Creates a new chat.
     *
     * @param name Name for the new chat
     * @return The created chat
     */
    fun createChat(name: String): Chat {
        return createChatUseCase(name)
    }

    /**
     * Sets the online status for testing offline functionality.
     *
     * @param isOnline Whether the device should be considered online
     */
    fun setOnlineStatus(isOnline: Boolean) {
        setNetworkStatusUseCase(isOnline)
    }

    /**
     * Dismisses the network error message.
     */
    fun dismissNetworkError() {
        _uiState.update { it.copy(showNetworkError = false) }
    }

    /**
     * Dismisses the connection error message.
     */
    fun dismissConnectionError() {
        _uiState.update { it.copy(showConnectionError = false) }
    }
    
    /**
     * Retries sending all queued messages.
     */
    fun retryQueuedMessages() {
        viewModelScope.launch {
            // First check if we're online and connected
            if (uiState.value.isOnline) {
                // Update UI state to show retry attempt
                _uiState.update { 
                    it.copy(isRetryingMessages = true)
                }
                
                try {
                    // Call the suspend use case to retry messages
                    retryQueuedMessagesUseCase()
                } catch (e: Exception) {
                    // Log error but don't crash the app
                    println("Error retrying messages: ${e.message}")
                } finally {
                    // Always reset retry state
                    _uiState.update { 
                        it.copy(isRetryingMessages = false)
                    }
                }
            }
        }
    }

    /**
     * Simulates receiving a typing status update from another user.
     *
     * @param isTyping Whether the user is typing
     * @param userName Name of the user who is typing
     */
    fun updateTypingStatus(isTyping: Boolean, userName: String = "Someone") {
        _uiState.update { 
            it.copy(
                isTyping = isTyping,
                typingUser = if (isTyping) userName else ""
            )
        }
    }
    
    /**
     * Retry sending a failed message.
     *
     * @param messageId ID of the message to retry
     */
    fun retryMessage(messageId: String) {
        viewModelScope.launch {
            val chatId = uiState.value.selectedChatId ?: return@launch
            
            // Get the message from current UI state
            val messageToRetry = uiState.value.chats
                .find { it.id == chatId }?.messages
                ?.find { it.id == messageId }
            
            if (messageToRetry != null) {
                // Update the UI to show the message is being retried
                val updatedMessage = messageToRetry.copy(isSending = true, isError = false)
                
                // Call the sendMessage directly with the original content
                // The WebSocketDataSource will handle the queue management
                sendMessageUseCase(chatId, messageToRetry.content)
            }
        }
    }
    
    /**
     * Marks all messages as read.
     */
    fun markMessagesAsRead() {
        _uiState.update { 
            it.copy(unreadCount = 0)
        }
    }
    
    /**
     * Simulates starting to type.
     * In a real implementation, this would send a typing indicator to the server.
     */
    fun startTyping() {
        // In a real app, this would send a typing event to the WebSocket
        // For demonstration purposes, we're just simulating it locally
    }
    
    /**
     * Simulates stopping typing.
     * In a real implementation, this would send a stopped typing indicator to the server.
     */
    fun stopTyping() {
        // In a real app, this would send a stopped typing event to the WebSocket
        // For demonstration purposes, we're just simulating it locally
    }

    /**
     * Observes all available chats.
     */
    private fun observeChats() {
        viewModelScope.launch {
            getAllChatsUseCase().collect { chats ->
                _uiState.update { it.copy(chats = chats) }
            }
        }
    }

    /**
     * Observes the currently selected chat.
     */
    private fun observeSelectedChat() {
        viewModelScope.launch {
            getSelectedChatIdUseCase().collect { chatId ->
                _uiState.update { it.copy(selectedChatId = chatId) }
                
                // Load messages for the selected chat
                chatId?.let { id ->
                    observeMessages(id)
                }
            }
        }
    }

    /**
     * Observes messages for a specific chat.
     */
    private fun observeMessages(chatId: String) {
        viewModelScope.launch {
            getMessageHistoryUseCase(chatId).collect { messages ->
                _uiState.update { it.copy(
                    messages = messages,
                    sentMessages = messages.filter { msg -> msg.isSent }
                ) }
            }
        }
    }

    /**
     * Observes network status changes.
     */
    private fun observeNetworkStatus() {
        viewModelScope.launch {
            getNetworkStatusUseCase().collect { isOnline ->
                val wasOffline = !uiState.value.isOnline
                
                _uiState.update { it.copy(
                    isOnline = isOnline,
                    isNetworkError = wasOffline && isOnline,
                    showNetworkError = (!isOnline || (wasOffline && isOnline))
                )}
                
                // If we're back online and have queued messages, retry sending them
                // Add a delay to ensure connection is fully established
                if (isOnline && wasOffline) {
                    // Show a message that we're trying to reconnect
                    _uiState.update { it.copy(
                        showNetworkError = true,
                        isNetworkError = true
                    )}
                    
                    viewModelScope.launch {
                        // Wait a bit longer to ensure the WebSocket has time to reconnect
                        kotlinx.coroutines.delay(3000)
                        
                        // Check again if we're still online before retrying
                        if (uiState.value.isOnline) {
                            // Try to reconnect first
                            if (!isConnectedUseCase() && uiState.value.chats.isNotEmpty()) {
                                // If we have an API key stored, reconnect automatically
                                val apiKey = "demo" // This should come from secure storage in a real app
                                connectToChat(apiKey)
                                
                                // Wait again for connection to establish
                                kotlinx.coroutines.delay(1000)
                            }
                            
                            // Now retry any queued messages
                            if (uiState.value.queuedMessages.isNotEmpty()) {
                                retryQueuedMessages()
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Observes messages that failed to send and are queued for retry.
     */
    private fun observeQueuedMessages() {
        viewModelScope.launch {
            getQueuedMessagesUseCase().collect { queuedMessages ->
                _uiState.update { it.copy(queuedMessages = queuedMessages) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}

/**
 * Data class representing the UI state for chat operations.
 */
data class ChatUiState(
    val isConnected: Boolean = false,
    val lastReceivedMessage: String = "",
    val sentMessages: List<Message> = emptyList(),
    val messages: List<Message> = emptyList(),
    val errorMessage: String = "",
    val isTyping: Boolean = false,
    val typingUser: String = "",
    val unreadCount: Int = 0,
    val chats: List<Chat> = emptyList(),
    val selectedChatId: String? = null,
    val isOnline: Boolean = true,
    val showNetworkError: Boolean = false,
    val showConnectionError: Boolean = false,
    val queuedMessages: List<Message> = emptyList(),
    val isNetworkError: Boolean = false,
    val isRetryingMessages: Boolean = false
)
