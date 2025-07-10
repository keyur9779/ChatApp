package com.example.chatandroid.domain.usecase

import com.example.chatandroid.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for clearing message history.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class ClearMessageHistoryUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to clear message history.
     */
    operator fun invoke() {
        chatRepository.clearAllChats()
    }
}
