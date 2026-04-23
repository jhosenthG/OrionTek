package com.jhosenthg.oriontek.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    @SerialName("id") val id: String,
    @SerialName("label") val label: String,
    @SerialName("address_type") val type: String,
    @SerialName("street_name") val street: String,
    @SerialName("suite_info") val suite: String?,
    @SerialName("city_name") val city: String,
    @SerialName("state_code") val state: String,
    @SerialName("zip_code") val zip: String
)