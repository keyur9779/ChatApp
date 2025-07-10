package com.example.chatandroid.domain.usecase

import com.example.chatandroid.data.model.Message
import com.example.chatandroid.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for sending messages to the chat.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to send a message.
     * 
     * @param chatId ID of the chat to send the message to
     * @param content The message content to send
     * @return The sent message with updated state
     */
    operator fun invoke(chatId: String, content: String): Message {
        return chatRepository.sendMessage(chatId, content)
    }
}
