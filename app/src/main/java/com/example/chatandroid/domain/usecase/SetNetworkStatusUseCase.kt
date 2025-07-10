package com.example.chatandroid.domain.usecase

import com.example.chatandroid.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for setting the network status.
 * 
 * @property chatRepository Repository that handles chat operations
 */
class SetNetworkStatusUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Executes the use case to set the network status.
     * 
     * @param isOnline Whether the device is online
     */
    operator fun invoke(isOnline: Boolean) {
        chatRepository.setNetworkStatus(isOnline)
    }
}
