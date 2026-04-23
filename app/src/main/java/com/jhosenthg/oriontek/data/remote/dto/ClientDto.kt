package com.jhosenthg.oriontek.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("industry_type") val industry: String,
    @SerialName("account_status") val status: String,
    @SerialName("contact_info") val contact: ContactDto,
    @SerialName("addresses") val addresses: List<AddressDto>,
    @SerialName("revenue") val revenue: Double?,
    @SerialName("orders_history") val orders: List<Int>?
)