package com.example.chatandroid.data.repository

import com.example.chatandroid.data.datasource.ChatDataSource
import com.example.chatandroid.data.datasource.WebSocketDataSource
import com.example.chatandroid.data.datasource.WebSocketEvent
import com.example.chatandroid.data.model.Chat
import com.example.chatandroid.data.model.Message
import com.example.chatandroid.domain.repository.ChatEvent
import com.example.chatandroid.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ChatRepository that uses WebSocketDataSource
 * to handle WebSocket communication.
 */
@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val webSocketDataSource: WebSocketDataSource,
    private val chatDataSource: ChatDataSource
) : ChatRepository {

    /**
     * Connects to the chat WebSocket and maps WebSocketEvents to ChatEvents.
     *
     * @param apiKey The API key for authentication
     * @return Flow of ChatEvent objects
     */
    override fun connectToChat(apiKey: String): Flow<ChatEvent> {
        return webSocketDataSource.connectToPieSocket(apiKey)
            .map { event ->
                when (event) {
                    is WebSocketEvent.Connected -> ChatEvent.Connected
                    is WebSocketEvent.MessageReceived -> {
                        val message = Message(
                            content = event.message,
                            isSent = false
                        )
                        // Save received message to current chat
                        val selectedChatId = chatDataSource.selectedChatId.value
                        if (selectedChatId != null) {
                            chatDataSource.addMessageToChat(selectedChatId, message)
                        }
                        ChatEvent.MessageReceived(message)
                    }
                    is WebSocketEvent.Error -> ChatEvent.Error(event.message)
                }
            }
    }

    /**
     * Sends a message to the WebSocket channel.
     *
     * @param chatId ID of the chat to send the message to
     * @param content The message content to send
     * @return The sent message with updated state
     */
    override fun sendMessage(chatId: String, content: String): Message {
        val message = Message(
            content = content,
            isSent = true,
            isSending = true
        )
        
        // Add message to chat first to show in UI
        chatDataSource.addMessageToChat(chatId, message)
        
        // Try to send through WebSocket
        val updatedMessage = webSocketDataSource.sendMessage(message)
        
        // Update message status in chat
        chatDataSource.updateMessage(chatId, message.id, updatedMessage)
        
        return updatedMessage
    }

    /**
     * Disconnects from the WebSocket.
     */
    override fun disconnect() {
        webSocketDataSource.disconnect()
    }
    
    /**
     * Checks if connected to WebSocket service.
     *
     * @return Boolean indicating connection status
     */
    override fun isConnected(): Boolean {
        return webSocketDataSource.isConnected()
    }
    
    /**
     * Gets the message history for a specific chat.
     *
     * @param chatId ID of the chat to get messages for
     * @return Flow of message history
     */
    override fun getMessageHistory(chatId: String): Flow<List<Message>> {
        return chatDataSource.getChatMessagesFlow(chatId)
    }
    
    /**
     * Gets all available chats.
     *
     * @return Flow of all chats
     */
    override fun getAllChats(): Flow<List<Chat>> {
        return chatDataSource.chats
    }
    
    /**
     * Gets the currently selected chat ID.
     *
     * @return Flow of the selected chat ID
     */
    override fun getSelectedChatId(): Flow<String?> {
        return chatDataSource.selectedChatId
    }
    
    /**
     * Selects a chat.
     *
     * @param chatId ID of the chat to select
     */
    override fun selectChat(chatId: String) {
        chatDataSource.selectChat(chatId)
    }
    
    /**
     * Creates a new chat.
     *
     * @param name Name of the chat
     * @return The created chat
     */
    override fun createChat(name: String): Chat {
        return chatDataSource.createChat(name)
    }
    
    /**
     * Clears all chat history.
     */
    override fun clearAllChats() {
        chatDataSource.clearAllChats()
    }
    
    /**
     * Sets the network connection status.
     *
     * @param isOnline Whether the device is online
     */
    override fun setNetworkStatus(isOnline: Boolean) {
        webSocketDataSource.setNetworkStatus(isOnline)
    }
    
    /**
     * Gets the current network status.
     *
     * @return Flow of online status
     */
    override fun getNetworkStatus(): Flow<Boolean> {
        return webSocketDataSource.isOnline
    }
    
    /**
     * Gets queued messages that failed to send.
     *
     * @return Flow of queued messages
     */
    override fun getQueuedMessages(): Flow<List<Message>> {
        return webSocketDataSource.messageQueue
    }
    
    /**
     * Retries sending queued messages.
     */
    override fun retryQueuedMessages() {
        webSocketDataSource.retrySendingQueuedMessages()
    }
}
