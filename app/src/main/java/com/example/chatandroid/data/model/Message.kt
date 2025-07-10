package com.example.chatandroid.data.model

/**
 * Data class representing a chat message.
 *
 * @property content The text content of the message
 * @property isSent Whether the message was sent by the user (true) or received (false)
 * @property timestamp The time when the message was sent/received
 * @property isLiked Whether the message has been liked by the user
 * @property id Unique identifier for the message
 * @property isSending Whether the message is currently being sent (for offline functionality)
 * @property isError Whether there was an error sending the message
 * @property isRead Whether the message has been read by the user
 */
data class Message(
    val content: String,
    val isSent: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isLiked: Boolean = false,
    val id: String = java.util.UUID.randomUUID().toString(),
    val isSending: Boolean = false,
    val isError: Boolean = false,
    val isRead: Boolean = false
)
