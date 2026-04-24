package com.jhosenthg.oriontek.domain.entities


data class Client(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val industry: String,
    val status: ClientStatus,
    val primaryContact: Contact,
    val addresses: List<Address>,
    val annualRevenue: Double? = null,
    val recentOrderData: List<Int> = emptyList()
)

enum class ClientStatus {
    ACTIVE,
    PENDING,
    INACTIVE
}