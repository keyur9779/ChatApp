package com.example.chatandroid.domain.usecase

import com.example.chatandroid.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for retrying queued messages that failed to send due to network issues.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class RetryQueuedMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to retry sending all queued messages.
     */
    operator fun invoke() {
        chatRepository.retryQueuedMessages()
    }
}
