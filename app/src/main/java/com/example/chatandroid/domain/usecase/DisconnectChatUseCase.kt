package com.example.chatandroid.domain.usecase

import com.example.chatandroid.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for disconnecting from the chat.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class DisconnectChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to disconnect from chat.
     */
    operator fun invoke() {
        chatRepository.disconnect()
    }
}
