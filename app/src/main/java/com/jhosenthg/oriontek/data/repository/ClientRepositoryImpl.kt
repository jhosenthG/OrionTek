package com.jhosenthg.oriontek.data.repository

import com.jhosenthg.oriontek.data.remote.OrionTekApi
import com.jhosenthg.oriontek.data.remote.mapper.toDomain
import com.jhosenthg.oriontek.domain.entities.Client
import com.jhosenthg.oriontek.domain.repository.ClientRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class ClientRepositoryImpl @Inject constructor(
	private val api: OrionTekApi
) : ClientRepository {

	override suspend fun getClients(): Flow<Result<List<Client>>> = flow {
		emit(
			runCatching {
				api.getClients().clients.map { clientDto -> clientDto.toDomain() }
			}
		)
	}

	override suspend fun getClientById(clientId: String): Flow<Result<Client>> = flow {
		emit(
			runCatching {
				api.getClientById(clientId).toDomain()
			}
		)
	}
}
