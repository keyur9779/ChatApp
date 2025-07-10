package com.example.chatandroid.domain.usecase

import com.example.chatandroid.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for selecting a chat.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class SelectChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to select a chat.
     * 
     * @param chatId ID of the chat to select
     */
    operator fun invoke(chatId: String) {
        chatRepository.selectChat(chatId)
    }
}
