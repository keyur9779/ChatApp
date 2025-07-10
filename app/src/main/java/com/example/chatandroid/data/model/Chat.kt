package com.example.chatandroid.data.model

/**
 * Data class representing a chat conversation.
 *
 * @property id Unique identifier for the chat
 * @property name Name of the chat or contact
 * @property messages List of messages in the chat
 * @property unreadCount Number of unread messages
 * @property lastUpdated Timestamp of the last message
 */
data class Chat(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val messages: List<Message> = emptyList(),
    val unreadCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    /**
     * Returns the most recent message in the chat, or null if there are no messages.
     */
    val lastMessage: Message? 
        get() = messages.maxByOrNull { it.timestamp }

    /**
     * Returns the timestamp of the last message, or null if there are no messages.
     */
    val lastMessageTimestamp: Long?
        get() = lastMessage?.timestamp

    /**
     * Returns a preview of the last message, limited to 50 characters.
     */
    val lastMessagePreview: String
        get() = lastMessage?.let {
            if (it.content.length > 50) it.content.take(50) + "..." else it.content
        } ?: ""
}
