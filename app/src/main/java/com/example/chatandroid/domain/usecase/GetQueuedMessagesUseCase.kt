package com.example.chatandroid.domain.usecase

import com.example.chatandroid.data.model.Message
import com.example.chatandroid.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving queued messages.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class GetQueuedMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to get queued messages.
     * 
     * @return Flow of queued messages
     */
    operator fun invoke(): Flow<List<Message>> {
        return chatRepository.getQueuedMessages()
    }
}
