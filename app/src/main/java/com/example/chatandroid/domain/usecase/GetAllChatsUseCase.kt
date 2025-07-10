package com.example.chatandroid.domain.usecase

import com.example.chatandroid.data.model.Chat
import com.example.chatandroid.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all chats.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class GetAllChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to get all chats.
     * 
     * @return Flow of all chats
     */
    operator fun invoke(): Flow<List<Chat>> {
        return chatRepository.getAllChats()
    }
}
