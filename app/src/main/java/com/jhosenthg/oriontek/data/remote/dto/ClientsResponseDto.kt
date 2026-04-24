package com.jhosenthg.oriontek.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientsResponseDto(
    @SerialName("clients") val clients: List<ClientDto> = emptyList()
)

