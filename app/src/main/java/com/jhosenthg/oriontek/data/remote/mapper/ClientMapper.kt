package com.jhosenthg.oriontek.data.remote.mapper

import com.jhosenthg.oriontek.data.remote.dto.AddressDto
import com.jhosenthg.oriontek.data.remote.dto.ClientDto
import com.jhosenthg.oriontek.data.remote.dto.ContactDto
import com.jhosenthg.oriontek.domain.entities.Address
import com.jhosenthg.oriontek.domain.entities.AddressType
import com.jhosenthg.oriontek.domain.entities.Client
import com.jhosenthg.oriontek.domain.entities.ClientStatus
import com.jhosenthg.oriontek.domain.entities.Contact

fun AddressDto.toDomain(): Address {
    return Address(
        id = this.id ?: "${this.street}-${this.zip}".ifBlank { this.city.ifBlank { "address" } },
        label = this.label ?: this.city.ifBlank { this.street.ifBlank { "Address" } },
        type = try {
            AddressType.valueOf((this.type ?: "OFFICE").uppercase())
        } catch (_: Exception) {
            AddressType.OFFICE
        },
        street = this.street,
        suite = this.suite,
        city = this.city,
        state = this.state ?: "N/A",
        zipCode = this.zip
    )
}


fun ContactDto.toDomain(): Contact {
    return Contact(
        name = this.name ?: this.email.substringBefore('@').ifBlank { "Contacto principal" },
        email = this.email,
        phone = this.phone
    )
}


fun ClientDto.toDomain(): Client {
    return Client(
        id = this.id,
        name = this.name,
        industry = this.industry,
        status = when (this.status.uppercase()) {
            "ACTIVE" -> ClientStatus.ACTIVE
            "PENDING" -> ClientStatus.PENDING
            else -> ClientStatus.INACTIVE
        },
        primaryContact = this.contact.toDomain(),
        addresses = this.addresses.map { it.toDomain() },
        annualRevenue = this.revenue,
        recentOrderData = this.orders ?: emptyList()
    )
}