package com.example.chatandroid.data.datasource

import com.example.chatandroid.data.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for managing message history.
 * Provides methods to save, retrieve, and clear message history.
 */
@Singleton
class MessageHistoryDataSource @Inject constructor() {
    
    private val _messageHistory = MutableStateFlow<List<Message>>(emptyList())
    
    /**
     * Gets the flow of message history.
     *
     * @return Flow emitting the list of messages
     */
    fun getMessageHistory(): Flow<List<Message>> = _messageHistory.asStateFlow()
    
    /**
     * Saves a message to the history.
     *
     * @param message The message to save
     */
    fun saveMessage(message: Message) {
        val currentList = _messageHistory.value
        _messageHistory.value = currentList + message
    }
    
    /**
     * Clears the message history.
     */
    fun clearHistory() {
        _messageHistory.value = emptyList()
    }
}
