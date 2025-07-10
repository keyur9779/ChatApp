package com.example.chatandroid.domain.usecase

import com.example.chatandroid.data.model.Chat
import com.example.chatandroid.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for creating a new chat.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class CreateChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to create a new chat.
     * 
     * @param name Name of the chat
     * @return The created chat
     */
    operator fun invoke(name: String): Chat {
        return chatRepository.createChat(name)
    }
}
