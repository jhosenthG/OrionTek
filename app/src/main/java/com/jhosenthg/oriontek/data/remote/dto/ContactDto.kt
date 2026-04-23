package com.jhosenthg.oriontek.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactDto(
    @SerialName("full_name") val name: String,
    @SerialName("email_address") val email: String,
    @SerialName("phone_number") val phone: String
)