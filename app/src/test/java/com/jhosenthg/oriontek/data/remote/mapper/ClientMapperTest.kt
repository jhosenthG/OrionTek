package com.jhosenthg.oriontek.data.remote.mapper

import com.jhosenthg.oriontek.data.remote.dto.AddressDto
import com.jhosenthg.oriontek.data.remote.dto.ClientDto
import com.jhosenthg.oriontek.data.remote.dto.ContactDto
import com.jhosenthg.oriontek.domain.entities.AddressType
import com.jhosenthg.oriontek.domain.entities.ClientStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClientMapperTest {

    @Test
    fun addressDtoToDomain_appliesFallbackValues_whenOptionalsAreMissing() {
        val dto = AddressDto(
            id = null,
            label = null,
            type = "invalid_type",
            street = "",
            suite = null,
            city = "",
            state = null,
            zip = ""
        )

        val result = dto.toDomain()

        assertEquals("-", result.id)
        assertEquals("Address", result.label)
        assertEquals(AddressType.OFFICE, result.type)
        assertEquals("N/A", result.state)
    }

    @Test
    fun contactDtoToDomain_usesEmailPrefix_whenNameIsNull() {
        val dto = ContactDto(name = null, email = "alice@example.com", phone = "+123456")

        val result = dto.toDomain()

        assertEquals("alice", result.name)
        assertEquals("alice@example.com", result.email)
        assertEquals("+123456", result.phone)
    }

    @Test
    fun contactDtoToDomain_usesDefaultName_whenNameAndEmailPrefixAreBlank() {
        val dto = ContactDto(name = null, email = "", phone = "")

        val result = dto.toDomain()

        assertEquals("Contacto principal", result.name)
    }

    @Test
    fun clientDtoToDomain_mapsStatusesAndOrdersFallback() {
        val baseContact = ContactDto(name = "John", email = "john@example.com", phone = "111")
        val baseAddress = AddressDto(
            id = "addr-1",
            label = "Main",
            type = "warehouse",
            street = "Street 1",
            suite = "Suite 2",
            city = "NY",
            state = "NY",
            zip = "10001"
        )

        val unknownStatusDto = ClientDto(
            id = "c-1",
            name = "ACME",
            imageUrl = null,
            industry = "Tech",
            status = "archived",
            contact = baseContact,
            addresses = listOf(baseAddress),
            revenue = 1200.0,
            orders = null
        )

        val pendingStatusDto = unknownStatusDto.copy(status = "PENDING", orders = listOf(1, 2, 3))

        val unknownStatusResult = unknownStatusDto.toDomain()
        val pendingStatusResult = pendingStatusDto.toDomain()

        assertEquals(ClientStatus.INACTIVE, unknownStatusResult.status)
        assertTrue(unknownStatusResult.recentOrderData.isEmpty())

        assertEquals(ClientStatus.PENDING, pendingStatusResult.status)
        assertEquals(listOf(1, 2, 3), pendingStatusResult.recentOrderData)
        assertEquals(AddressType.WAREHOUSE, pendingStatusResult.addresses.first().type)
    }
}

