package com.example.chatandroid.domain.usecase

import com.example.chatandroid.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for clearing all chats.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class ClearAllChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to clear all chats.
     */
    operator fun invoke() {
        chatRepository.clearAllChats()
    }
}
