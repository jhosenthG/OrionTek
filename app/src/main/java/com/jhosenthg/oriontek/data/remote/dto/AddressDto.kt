package com.jhosenthg.oriontek.data.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AddressDto(
  @SerialName("id") val id: String? = null,
  @SerialName("label") val label: String? = null,
  @SerialName("address_type")
  @JsonNames("type")
  val type: String? = null,
  @SerialName("street_name")
  @JsonNames("street")
  val street: String = "",
  @SerialName("suite_info")
  @JsonNames("suite")
  val suite: String? = null,
  @SerialName("city_name")
  @JsonNames("city")
  val city: String = "",
  @SerialName("state_code")
  @JsonNames("state", "country")
  val state: String? = null,
  @SerialName("zip_code")
  @JsonNames("postalCode")
  val zip: String = ""
)