package com.jhosenthg.oriontek.domain.useCase

import app.cash.turbine.test
import com.jhosenthg.oriontek.domain.entities.Client
import com.jhosenthg.oriontek.domain.entities.ClientStatus
import com.jhosenthg.oriontek.domain.entities.Contact
import com.jhosenthg.oriontek.domain.repository.ClientRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetClientsUseCaseTest {

    private val repository: ClientRepository = mockk()
    private val useCase = GetClientsUseCase(repository)

    @Test
    fun invoke_delegatesToRepository_andEmitsSuccess() = runTest {
        val expectedClients = listOf(
            Client(
                id = "c-1",
                name = "Acme",
                industry = "Tech",
                status = ClientStatus.ACTIVE,
                primaryContact = Contact(name = "John", email = "john@example.com", phone = "123"),
                addresses = emptyList()
            )
        )
        coEvery { repository.getClients() } returns flowOf(Result.success(expectedClients))

        useCase().test {
            val result = awaitItem()

            assertTrue(result.isSuccess)
            assertEquals(expectedClients, result.getOrThrow())

            awaitComplete()
        }

        coVerify(exactly = 1) { repository.getClients() }
    }

    @Test
    fun invoke_propagatesFailure() = runTest {
        val failure = IllegalArgumentException("invalid query")
        coEvery { repository.getClients() } returns flowOf(Result.failure(failure))

        useCase().test {
            val result = awaitItem()

            assertTrue(result.isFailure)
            assertEquals("invalid query", result.exceptionOrNull()?.message)

            awaitComplete()
        }

        coVerify(exactly = 1) { repository.getClients() }
    }
}

