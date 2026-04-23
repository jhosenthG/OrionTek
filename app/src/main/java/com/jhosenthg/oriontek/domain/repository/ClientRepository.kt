package com.jhosenthg.oriontek.domain.repository

import com.jhosenthg.oriontek.domain.entities.Address
import com.jhosenthg.oriontek.domain.entities.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    suspend fun getClients(): Flow<Result<List<Client>>>
    suspend fun getClientById(clientId: String): Flow<Result<Client>>
    suspend fun addAddressToClient(clientId: String, address: Address): Result<Unit>
}