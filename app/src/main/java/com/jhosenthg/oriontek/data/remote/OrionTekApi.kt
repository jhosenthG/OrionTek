package com.jhosenthg.oriontek.data.remote

import com.jhosenthg.oriontek.data.remote.dto.ClientDto
import com.jhosenthg.oriontek.data.remote.dto.ClientsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface OrionTekApi {
	@GET("db.json")
	suspend fun getClients(): ClientsResponseDto

	@GET("clients/{id}.json")
	suspend fun getClientById(
		@Path("id") clientId: String
	): ClientDto
}