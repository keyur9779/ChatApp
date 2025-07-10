package com.example.chatandroid.domain.usecase

import com.example.chatandroid.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving the selected chat ID.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class GetSelectedChatIdUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to get the selected chat ID.
     * 
     * @return Flow of the selected chat ID
     */
    operator fun invoke(): Flow<String?> {
        return chatRepository.getSelectedChatId()
    }
}
