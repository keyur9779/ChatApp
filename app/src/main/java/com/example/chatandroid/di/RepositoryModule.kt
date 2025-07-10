package com.example.chatandroid.di

import com.example.chatandroid.data.repository.ChatRepositoryImpl
import com.example.chatandroid.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing repository dependencies.
 * Binds concrete implementations to their interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Binds ChatRepositoryImpl to ChatRepository interface.
     * 
     * @param chatRepositoryImpl The concrete implementation
     * @return The bound ChatRepository interface
     */
    @Binds
    @Singleton
    abstract fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository
}
