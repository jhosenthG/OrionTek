package com.jhosenthg.oriontek.data.remote

import com.jhosenthg.oriontek.data.remote.dto.ClientDto
import retrofit2.http.GET
import retrofit2.http.Path

interface OrionTekApi {
	@GET("clients")
	suspend fun getClients(): List<ClientDto>

	@GET("clients/{clientId}")
	suspend fun getClientById(
		@Path("clientId") clientId: String
	): ClientDto
}