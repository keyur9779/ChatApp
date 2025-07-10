package com.example.chatandroid.data.datasource

import com.example.chatandroid.data.model.Chat
import com.example.chatandroid.data.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for managing chat conversations.
 * Provides methods to create, update, and access chats.
 */
@Singleton
class ChatDataSource @Inject constructor() {
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()
    
    // Currently selected chat
    private val _selectedChatId = MutableStateFlow<String?>(null)
    val selectedChatId: StateFlow<String?> = _selectedChatId.asStateFlow()
    
    init {
        // Create a default chat with the chatbot
        createChat("Chatbot")
    }
    
    /**
     * Creates a new chat conversation.
     *
     * @param name Name of the chat or contact
     * @return The created chat
     */
    fun createChat(name: String): Chat {
        val newChat = Chat(name = name)
        _chats.update { currentChats ->
            currentChats + newChat
        }
        
        // Select the new chat if it's the first one
        if (_chats.value.size == 1) {
            _selectedChatId.value = newChat.id
        }
        
        return newChat
    }
    
    /**
     * Adds a message to a chat conversation.
     *
     * @param chatId ID of the chat to add the message to
     * @param message Message to add
     */
    fun addMessageToChat(chatId: String, message: Message) {
        _chats.update { currentChats ->
            currentChats.map { chat ->
                if (chat.id == chatId) {
                    // Calculate unread count (increment if not selected chat)
                    val unreadCount = if (chatId != _selectedChatId.value && !message.isSent) {
                        chat.unreadCount + 1
                    } else {
                        chat.unreadCount
                    }
                    
                    chat.copy(
                        messages = chat.messages + message,
                        unreadCount = unreadCount,
                        lastUpdated = System.currentTimeMillis()
                    )
                } else {
                    chat
                }
            }
        }
    }
    
    /**
     * Updates a message in a chat conversation.
     *
     * @param chatId ID of the chat containing the message
     * @param messageId ID of the message to update
     * @param updatedMessage Updated message
     */
    fun updateMessage(chatId: String, messageId: String, updatedMessage: Message) {
        _chats.update { currentChats ->
            currentChats.map { chat ->
                if (chat.id == chatId) {
                    chat.copy(
                        messages = chat.messages.map { message ->
                            if (message.id == messageId) updatedMessage else message
                        }
                    )
                } else {
                    chat
                }
            }
        }
    }
    
    /**
     * Marks all messages in a chat as read.
     *
     * @param chatId ID of the chat to mark as read
     */
    fun markChatAsRead(chatId: String) {
        _chats.update { currentChats ->
            currentChats.map { chat ->
                if (chat.id == chatId) {
                    chat.copy(
                        messages = chat.messages.map { 
                            if (!it.isRead) it.copy(isRead = true) else it 
                        },
                        unreadCount = 0
                    )
                } else {
                    chat
                }
            }
        }
    }
    
    /**
     * Sets the currently selected chat.
     *
     * @param chatId ID of the selected chat
     */
    fun selectChat(chatId: String) {
        _selectedChatId.value = chatId
        markChatAsRead(chatId)
    }
    
    /**
     * Gets a chat by its ID.
     *
     * @param chatId ID of the chat to retrieve
     * @return The chat, or null if not found
     */
    fun getChat(chatId: String): Chat? {
        return _chats.value.find { it.id == chatId }
    }
    
    /**
     * Gets a flow of messages for a specific chat.
     *
     * @param chatId ID of the chat to get messages for
     * @return Flow of messages
     */
    fun getChatMessagesFlow(chatId: String): Flow<List<Message>> {
        return _chats.map { chatsList ->
            chatsList.find { it.id == chatId }?.messages ?: emptyList()
        }
    }
    
    /**
     * Gets the currently selected chat.
     *
     * @return The selected chat, or null if none is selected
     */
    fun getSelectedChat(): Chat? {
        return _selectedChatId.value?.let { chatId ->
            _chats.value.find { it.id == chatId }
        }
    }
    
    /**
     * Clears all chats.
     */
    fun clearAllChats() {
        _chats.value = emptyList()
        createChat("Chatbot") // Recreate default chat
    }
}
