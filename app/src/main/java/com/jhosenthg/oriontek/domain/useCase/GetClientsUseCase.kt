package com.jhosenthg.oriontek.domain.useCase

import com.jhosenthg.oriontek.domain.entities.Client
import com.jhosenthg.oriontek.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetClientsUseCase @Inject constructor(
    private val repository: ClientRepository
) {
    operator fun invoke(): Flow<Result<List<Client>>> {
        return repository.getClients()
    }
}