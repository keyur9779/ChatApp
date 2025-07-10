package com.example.chatandroid.domain.usecase

import com.example.chatandroid.domain.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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
     * 
     * Uses a small delay before retrying to ensure connection stability
     * and prevent duplicate message sends.
     */
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        // Add a small delay to ensure connection is stable before retrying
        delay(500)
        
        // Check if we're connected before retrying
        if (chatRepository.isConnected()) {
            // Retry sending queued messages
            chatRepository.retryQueuedMessages()
            
            // Add a small delay after retrying to allow system to process
            delay(300)
        }
    }
}
