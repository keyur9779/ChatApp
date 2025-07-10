package com.example.chatandroid.domain.repository

import com.example.chatandroid.data.model.Chat
import com.example.chatandroid.data.model.Message
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for chat operations.
 * Defines methods for connecting, sending, and receiving messages.
 */
interface ChatRepository {
    
    /**
     * Connects to the WebSocket chat service.
     *
     * @param apiKey API key for authentication
     * @return Flow of received messages and connection events
     */
    fun connectToChat(apiKey: String): Flow<ChatEvent>
    
    /**
     * Sends a message through the WebSocket.
     *
     * @param chatId ID of the chat to send the message to
     * @param message The message content to send
     * @return The sent message with updated state
     */
    fun sendMessage(chatId: String, content: String): Message
    
    /**
     * Disconnects from the WebSocket chat service.
     */
    fun disconnect()
    
    /**
     * Checks if connected to WebSocket service.
     *
     * @return Boolean indicating connection status
     */
    fun isConnected(): Boolean
    
    /**
     * Gets the message history.
     *
     * @return Flow of message history
     */
    fun getMessageHistory(chatId: String): Flow<List<Message>>
    
    /**
     * Gets all chats.
     *
     * @return Flow of all chats
     */
    fun getAllChats(): Flow<List<Chat>>
    
    /**
     * Gets the currently selected chat.
     *
     * @return Flow of the selected chat ID
     */
    fun getSelectedChatId(): Flow<String?>
    
    /**
     * Selects a chat.
     *
     * @param chatId ID of the chat to select
     */
    fun selectChat(chatId: String)
    
    /**
     * Creates a new chat.
     *
     * @param name Name of the chat
     * @return The created chat
     */
    fun createChat(name: String): Chat
    
    /**
     * Clears all chat history.
     */
    fun clearAllChats()
    
    /**
     * Simulates network connectivity change.
     *
     * @param isOnline Whether the device is online
     */
    fun setNetworkStatus(isOnline: Boolean)
    
    /**
     * Gets the current network status.
     *
     * @return Flow of online status
     */
    fun getNetworkStatus(): Flow<Boolean>
    
    /**
     * Gets queued messages that failed to send.
     *
     * @return Flow of queued messages
     */
    fun getQueuedMessages(): Flow<List<Message>>
    
    /**
     * Retries sending queued messages.
     */
    fun retryQueuedMessages()
}

/**
 * Sealed class representing different chat events.
 */
sealed class ChatEvent {
    object Connected : ChatEvent()
    data class MessageReceived(val message: Message) : ChatEvent()
    data class Error(val message: String) : ChatEvent()
}
