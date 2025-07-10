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
                    
                    // Retry sending queued messages
                    retrySendingQueuedMessages()
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
        // Check if this is a message that was already displayed in the UI
        val existingQueuedMessage = _messageQueue.value.find { it.content == message.content }
        
        // If this is a retry of a message that's already in the queue, remove it from the queue
        if (existingQueuedMessage != null) {
            _messageQueue.update { currentQueue ->
                currentQueue.filter { it.id != existingQueuedMessage.id }
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
            // Check if the message is already in the queue (by content or ID)
            val isAlreadyQueued = currentQueue.any { 
                it.id == message.id || it.content == message.content 
            }
            
            if (isAlreadyQueued) {
                // Don't add duplicate message, just update the existing one to mark as error
                currentQueue.map {
                    if (it.id == message.id || it.content == message.content)
                        it.copy(isSending = false, isError = true)
                    else
                        it
                }
            } else {
                // Add the new message to the queue
                currentQueue + message.copy(isSending = false, isError = true)
            }
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
        
        if (currentQueue.isNotEmpty() && isConnected && _isOnline.value) {
            val updatedQueue = mutableListOf<Message>()
            
            currentQueue.forEach { message ->
                try {
                    val event = PieSocketEvent("message").apply {
                        data = message.content
                    }
                    chatChannel?.publish(event)
                    
                    // Keep track of successfully sent messages
                    successfullySentMessages.add(message.copy(isSending = false, isError = false))
                } catch (e: Exception) {
                    // Keep in queue if sending fails
                    updatedQueue.add(message)
                }
            }
            
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
        
        // If back online, try to send queued messages after a short delay
        // to ensure connection is properly established
        if (isOnline && wasOffline) {
            kotlinx.coroutines.GlobalScope.launch {
                kotlinx.coroutines.delay(1000)
                if (_isOnline.value && isConnected) {
                    retrySendingQueuedMessages()
                }
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
