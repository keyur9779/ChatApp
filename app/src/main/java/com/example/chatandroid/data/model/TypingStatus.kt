package com.example.chatandroid.data.model

/**
 * Data class representing the typing status of a user.
 *
 * @property isTyping Whether the user is currently typing
 * @property userName Name of the user who is typing
 */
data class TypingStatus(
    val isTyping: Boolean,
    val userName: String = "Someone"
)
