package com.jhosenthg.oriontek.data.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ContactDto(
    @SerialName("full_name")
    @JsonNames("name")
    val name: String? = null,
    @SerialName("email_address")
    @JsonNames("email")
    val email: String = "",
    @SerialName("phone_number")
    @JsonNames("phone")
    val phone: String = ""
)