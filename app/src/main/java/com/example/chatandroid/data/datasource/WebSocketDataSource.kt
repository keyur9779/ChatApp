package com.example.chatandroid.data.datasource

import com.example.chatandroid.data.model.Message
import com.piesocket.channels.PieSocket
import com.piesocket.channels.misc.PieSocketEvent
import com.piesocket.channels.misc.PieSocketEventListener
import com.piesocket.channels.misc.PieSocketOptions
import com.piesocket.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source class responsible for handling WebSocket operations using PieSocket.
 * Provides methods to connect, send messages, and receive data from WebSocket.
 */
@Singleton
class WebSocketDataSource @Inject constructor() {
    private var piesocket: PieSocket? = null
    private var chatChannel: Channel? = null
    private var isConnected = false
    
    // Queue for messages that failed to send due to being offline
    private val _messageQueue = MutableStateFlow<List<Message>>(emptyList())
    val messageQueue: StateFlow<List<Message>> = _messageQueue.asStateFlow()
    
    // Network state
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    /**
     * Connects to PieSocket WebSocket with the provided API key.
     *
     * @param apiKey The API key for PieSocket authentication
     * @return Flow emitting connection states and messages
     */
    fun connectToPieSocket(apiKey: String): Flow<WebSocketEvent> = callbackFlow {
        try {
            val options = PieSocketOptions().apply {
                this.apiKey = apiKey
                this.clusterId = "free.blr2"
            }

            piesocket = PieSocket(options)
            chatChannel = piesocket?.join("default")

            chatChannel?.listen("system:connected", object : PieSocketEventListener() {
                override fun handleEvent(event: PieSocketEvent) {
                    isConnected = true
                    _isOnline.value = true
                    trySend(WebSocketEvent.Connected)
                    
                    // Instead of retrying messages immediately, just send a Connected event
                    // The ViewModel will detect the connection and trigger retries with proper delays
                }
            })

            chatChannel?.listen("message", object : PieSocketEventListener() {
                override fun handleEvent(event: PieSocketEvent) {
                    event.data?.toString()?.let {
                        trySend(WebSocketEvent.MessageReceived(it))
                    }
                }
            })

            chatChannel?.listen("system:error", object : PieSocketEventListener() {
                override fun handleEvent(event: PieSocketEvent) {
                    val errorMessage = event.data?.toString() ?: "Unknown error"
                    _isOnline.value = false
                    trySend(WebSocketEvent.Error(errorMessage))
                }
            })
            
        } catch (e: Exception) {
            isConnected = false
            _isOnline.value = false
            trySend(WebSocketEvent.Error("Failed to connect: ${e.message}"))
        }
        
        awaitClose {
            disconnect()
        }
    }

    /**
     * Sends a message through the WebSocket connection.
     * If offline, the message is queued for later sending.
     *
     * @param message The message object to send
     * @return The sent or queued message with updated state
     */
    fun sendMessage(message: Message): Message {
        // Check if this is a retry of an existing message (by ID or content)
        val existingQueuedMessage = _messageQueue.value.find { 
            it.id == message.id || it.content == message.content 
        }
        
        // Remove any existing versions of this message from the queue to prevent duplicates
        if (existingQueuedMessage != null) {
            _messageQueue.update { currentQueue ->
                currentQueue.filter { it.id != existingQueuedMessage.id && it.content != message.content }
            }
        }
        
        return if (isConnected && _isOnline.value) {
            try {
                val event = PieSocketEvent("message").apply {
                    data = message.content
                }
                chatChannel?.publish(event)
                message.copy(isSending = false, isError = false)
            } catch (e: Exception) {
                queueMessage(message)
                message.copy(isSending = false, isError = true)
            }
        } else {
            queueMessage(message)
            message.copy(isSending = false, isError = true)
        }
    }
    
    /**
     * Queues a message for later sending when back online.
     * Prevents duplicate messages in the queue.
     *
     * @param message The message to queue
     */
    private fun queueMessage(message: Message) {
        _messageQueue.update { currentQueue ->
            // Remove any existing messages with the same ID or content
            val filteredQueue = currentQueue.filter { 
                it.id != message.id && it.content != message.content 
            }
            
            // Add the new message to the queue with error status
            filteredQueue + message.copy(isSending = false, isError = true)
        }
    }
    
    /**
     * Retries sending all queued messages.
     * 
     * @return List of successfully sent message IDs that need to be updated in the UI
     */
    fun retrySendingQueuedMessages(): List<Message> {
        val currentQueue = _messageQueue.value
        val successfullySentMessages = mutableListOf<Message>()
        
        // If we have queued messages, are connected, and online, retry sending them
        if (currentQueue.isNotEmpty() && isConnected && _isOnline.value) {
            val updatedQueue = mutableListOf<Message>()
            
            // Create a set of unique message content for deduplication
            val uniqueContentMessages = currentQueue
                .distinctBy { it.content } // Only process one message per unique content
            
            // Process each unique message
            for (message in uniqueContentMessages) {
                try {
                    // Create a small delay between messages to avoid overwhelming the server
                    kotlinx.coroutines.runBlocking { kotlinx.coroutines.delay(150) }
                    
                    // Publish the message
                    val event = PieSocketEvent("message").apply {
                        data = message.content
                    }
                    chatChannel?.publish(event)
                    
                    // Add to successfully sent messages list
                    successfullySentMessages.add(message.copy(
                        isSending = false, 
                        isError = false,
                        isSent = true
                    ))
                } catch (e: Exception) {
                    // Keep in queue if sending fails
                    updatedQueue.add(message)
                }
            }
            
            // Update the queue with only messages that failed to send
            _messageQueue.value = updatedQueue
        }
        
        return successfullySentMessages
    }
    
    /**
     * Simulates a network connectivity change.
     *
     * @param isOnline Whether the device is online
     */
    fun setNetworkStatus(isOnline: Boolean) {
        val wasOffline = !_isOnline.value
        _isOnline.value = isOnline
        
        if (!isOnline) {
            // When going offline, mark connection as disconnected
            isConnected = false
        } else if (wasOffline) {
            // Reconnect logic when coming back online
            try {
                // The actual reconnect will be handled by PieSocket's internal logic
                // We're just updating our state to reflect this
                isConnected = true
                
                // Don't automatically retry messages here - leave it to the repository
                // which will be notified of the online status change and handle it properly
            } catch (e: Exception) {
                isConnected = false
            }
        }
    }

    /**
     * Disconnects from the WebSocket.
     */
    fun disconnect() {
        chatChannel?.disconnect()
        isConnected = false
    }

    /**
     * Checks if the WebSocket is currently connected.
     *
     * @return Boolean indicating connection status
     */
    fun isConnected(): Boolean = isConnected
}

/**
 * Sealed class representing different WebSocket events that can occur.
 */
sealed class WebSocketEvent {
    object Connected : WebSocketEvent()
    data class MessageReceived(val message: String) : WebSocketEvent()
    data class Error(val message: String) : WebSocketEvent()
}
