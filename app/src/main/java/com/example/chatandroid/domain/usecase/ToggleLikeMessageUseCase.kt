package com.example.chatandroid.domain.usecase

import com.example.chatandroid.data.model.Message
import com.example.chatandroid.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for liking or unliking a message.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class ToggleLikeMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to toggle the like status of a message.
     * 
     * @param message The message to update
     * @return Updated message with toggled like status
     */
    operator fun invoke(message: Message): Message {
        return message.copy(isLiked = !message.isLiked)
    }
}
