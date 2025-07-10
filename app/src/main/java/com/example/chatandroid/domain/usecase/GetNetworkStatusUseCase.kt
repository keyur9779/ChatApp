package com.example.chatandroid.domain.usecase

import com.example.chatandroid.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting the network status.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class GetNetworkStatusUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to get the network status.
     * 
     * @return Flow of online status
     */
    operator fun invoke(): Flow<Boolean> {
        return chatRepository.getNetworkStatus()
    }
}
