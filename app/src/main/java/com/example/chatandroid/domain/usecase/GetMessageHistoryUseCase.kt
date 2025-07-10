package com.example.chatandroid.domain.usecase

import com.example.chatandroid.data.model.Message
import com.example.chatandroid.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving message history for a specific chat.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class GetMessageHistoryUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to get message history for a specific chat.
     * 
     * @param chatId ID of the chat to get history for
     * @return Flow of message history list
     */
    operator fun invoke(chatId: String): Flow<List<Message>> {
        return chatRepository.getMessageHistory(chatId)
    }
}
