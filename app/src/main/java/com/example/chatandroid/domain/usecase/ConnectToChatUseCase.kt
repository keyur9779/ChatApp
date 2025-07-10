package com.example.chatandroid.domain.usecase

import com.example.chatandroid.domain.repository.ChatEvent
import com.example.chatandroid.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for connecting to the chat service.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class ConnectToChatUseCase @Inject constructor(
    val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to connect to the chat service.
     * 
     * @param apiKey API key for authentication
     * @return Flow of ChatEvent objects
     */
    operator fun invoke(apiKey: String): Flow<ChatEvent> {
        return chatRepository.connectToChat(apiKey)
    }
}
