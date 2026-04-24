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

class GetClientDetailsUseCaseTest {

    private val repository: ClientRepository = mockk()
    private val useCase = GetClientDetailsUseCase(repository)

    @Test
    fun invoke_delegatesToRepositoryWithId_andEmitsSuccess() = runTest {
        val expectedClient = Client(
            id = "c-22",
            name = "Orion",
            industry = "Finance",
            status = ClientStatus.PENDING,
            primaryContact = Contact(name = "Ana", email = "ana@example.com", phone = "321"),
            addresses = emptyList()
        )
        coEvery { repository.getClientById("c-22") } returns flowOf(Result.success(expectedClient))

        useCase("c-22").test {
            val result = awaitItem()

            assertTrue(result.isSuccess)
            assertEquals(expectedClient, result.getOrThrow())

            awaitComplete()
        }

        coVerify(exactly = 1) { repository.getClientById("c-22") }
    }

    @Test
    fun invoke_propagatesFailure() = runTest {
        val failure = NoSuchElementException("missing")
        coEvery { repository.getClientById("404") } returns flowOf(Result.failure(failure))

        useCase("404").test {
            val result = awaitItem()

            assertTrue(result.isFailure)
            assertEquals("missing", result.exceptionOrNull()?.message)

            awaitComplete()
        }

        coVerify(exactly = 1) { repository.getClientById("404") }
    }
}

