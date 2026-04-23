package com.jhosenthg.oriontek.di

import com.jhosenthg.oriontek.data.repository.ClientRepositoryImpl
import com.jhosenthg.oriontek.domain.repository.ClientRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindClientRepository(
        clientRepositoryImpl: ClientRepositoryImpl
    ): ClientRepository
}
