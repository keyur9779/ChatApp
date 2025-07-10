package com.example.chatandroid.domain.usecase

import com.example.chatandroid.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for checking connection status.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class IsConnectedUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to check connection status.
     * 
     * @return Boolean indicating if the chat is connected
     */
    operator fun invoke(): Boolean {
        return chatRepository.isConnected()
    }
}
