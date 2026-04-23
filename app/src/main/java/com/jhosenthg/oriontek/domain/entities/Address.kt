package com.jhosenthg.oriontek.domain.entities

data class Address(
    val id: String,
    val label: String,
    val type: AddressType,
    val street: String,
    val suite: String?,
    val city: String,
    val state: String,
    val zipCode: String
)

enum class AddressType {
    OFFICE,
    BILLING,
    OPERATIONS,
    WAREHOUSE
}
