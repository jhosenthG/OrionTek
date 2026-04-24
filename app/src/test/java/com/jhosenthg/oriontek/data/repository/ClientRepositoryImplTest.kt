package com.jhosenthg.oriontek.data.repository

import app.cash.turbine.test
import com.jhosenthg.oriontek.data.remote.OrionTekApi
import com.jhosenthg.oriontek.data.remote.dto.AddressDto
import com.jhosenthg.oriontek.data.remote.dto.ClientDto
import com.jhosenthg.oriontek.data.remote.dto.ClientsResponseDto
import com.jhosenthg.oriontek.data.remote.dto.ContactDto
import com.jhosenthg.oriontek.domain.entities.ClientStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClientRepositoryImplTest {

    private val api: OrionTekApi = mockk()
    private val repository = ClientRepositoryImpl(api)

    @Test
    fun getClients_emitsSuccessWithMappedData() = runTest {
        coEvery { api.getClients() } returns ClientsResponseDto(clients = listOf(sampleClientDto(status = "ACTIVE")))

        repository.getClients().test {
            val result = awaitItem()

            assertTrue(result.isSuccess)
            val clients = result.getOrThrow()
            assertEquals(1, clients.size)
            assertEquals("c-1", clients.first().id)
            assertEquals(ClientStatus.ACTIVE, clients.first().status)

            awaitComplete()
        }

        coVerify(exactly = 1) { api.getClients() }
    }

    @Test
    fun getClients_emitsFailure_whenApiThrows() = runTest {
        coEvery { api.getClients() } throws IllegalStateException("network error")

        repository.getClients().test {
            val result = awaitItem()

            assertTrue(result.isFailure)
            assertEquals("network error", result.exceptionOrNull()?.message)

            awaitComplete()
        }

        coVerify(exactly = 1) { api.getClients() }
    }

    @Test
    fun getClientById_emitsMappedClient_andUsesRequestedId() = runTest {
        coEvery { api.getClientById("c-9") } returns sampleClientDto(id = "c-9", status = "PENDING")

        repository.getClientById("c-9").test {
            val result = awaitItem()

            assertTrue(result.isSuccess)
            val client = result.getOrThrow()
            assertEquals("c-9", client.id)
            assertEquals(ClientStatus.PENDING, client.status)

            awaitComplete()
        }

        coVerify(exactly = 1) { api.getClientById("c-9") }
    }

    @Test
    fun getClientById_emitsFailure_whenApiThrows() = runTest {
        coEvery { api.getClientById("missing") } throws NoSuchElementException("not found")

        repository.getClientById("missing").test {
            val result = awaitItem()

            assertTrue(result.isFailure)
            assertEquals("not found", result.exceptionOrNull()?.message)

            awaitComplete()
        }

        coVerify(exactly = 1) { api.getClientById("missing") }
    }

    private fun sampleClientDto(id: String = "c-1", status: String = "ACTIVE"): ClientDto {
        return ClientDto(
            id = id,
            name = "Acme",
            imageUrl = null,
            industry = "Tech",
            status = status,
            contact = ContactDto(name = "John", email = "john@example.com", phone = "123"),
            addresses = listOf(
                AddressDto(
                    id = "a-1",
                    label = "HQ",
                    type = "OFFICE",
                    street = "Main",
                    suite = null,
                    city = "Lima",
                    state = "LI",
                    zip = "0001"
                )
            ),
            revenue = 1000.0,
            orders = listOf(1, 2)
        )
    }
}

