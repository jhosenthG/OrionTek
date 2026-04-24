package com.jhosenthg.oriontek.presentation.screens.clientDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhosenthg.oriontek.domain.entities.Address
import com.jhosenthg.oriontek.domain.entities.AddressType
import com.jhosenthg.oriontek.domain.entities.Client
import com.jhosenthg.oriontek.domain.entities.ClientStatus
import com.jhosenthg.oriontek.domain.useCase.GetClientDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ClientDetailsViewmodel @Inject constructor(
	private val getClientDetailsUseCase: GetClientDetailsUseCase
) : ViewModel() {

	data class AddressUi(
		val id: String,
		val label: String,
		val typeLabel: String,
		val line1: String,
		val line2: String
	)

	data class UiState(
		val isLoading: Boolean = false,
		val clientName: String = "",
		val initials: String = "",
		val accountLabel: String = "",
		val primaryContact: String = "",
		val email: String = "",
		val phone: String = "",
		val addresses: List<AddressUi> = emptyList(),
		val searchQuery: String = "",
		val isSearchActive: Boolean = false,
		val errorMessage: String? = null
	)

	private val _uiState = MutableStateFlow(UiState(isLoading = true))
	val uiState: StateFlow<UiState> = _uiState.asStateFlow()

	private var lastClientId: String? = null
	private var loadJob: Job? = null
	private var allAddresses: List<AddressUi> = emptyList()

	fun loadClient(clientId: String) {
		if (clientId.isBlank()) {
			_uiState.update { state ->
				state.copy(
					isLoading = false,
					errorMessage = "No se recibió un cliente válido."
				)
			}
			return
		}

		lastClientId = clientId
		loadJob?.cancel()
		loadJob = viewModelScope.launch {
			_uiState.update { state -> state.copy(isLoading = true, errorMessage = null) }

			getClientDetailsUseCase(clientId).collect { result ->
				result.onSuccess { client ->
					val mappedState = mapClientToUiState(client)
					allAddresses = mappedState.addresses
					_uiState.value = mappedState
				}.onFailure { throwable ->
					allAddresses = emptyList()
					_uiState.update { state ->
						state.copy(
							isLoading = false,
							errorMessage = throwable.message ?: "No se pudieron cargar los detalles del cliente."
						)
					}
				}
			}
		}
	}

	fun retry() {
		lastClientId?.let(::loadClient)
	}

	fun onSearchClick() {
		val state = _uiState.value
		if (state.isSearchActive) {
			_uiState.update {
				it.copy(
					isSearchActive = false,
					searchQuery = "",
					addresses = allAddresses
				)
			}
		} else {
			_uiState.update { it.copy(isSearchActive = true) }
		}
	}

	fun onSearchQueryChange(query: String) {
		_uiState.update { it.copy(searchQuery = query) }
		applyAddressFilter(query)
	}

	private fun mapClientToUiState(client: Client): UiState {
		return UiState(
			isLoading = false,
			clientName = client.name,
			initials = buildInitials(client.name),
			accountLabel = accountLabelFor(client.status),
			primaryContact = client.primaryContact.name,
			email = client.primaryContact.email,
			phone = client.primaryContact.phone,
			addresses = client.addresses.map(::mapAddress),
			searchQuery = "",
			isSearchActive = false,
			errorMessage = null
		)
	}

	private fun applyAddressFilter(query: String) {
		val normalizedQuery = query.trim().lowercase()
		val filtered = if (normalizedQuery.isBlank()) {
			allAddresses
		} else {
			allAddresses.filter { address ->
				address.label.lowercase().contains(normalizedQuery) ||
					address.typeLabel.lowercase().contains(normalizedQuery) ||
					address.line1.lowercase().contains(normalizedQuery) ||
					address.line2.lowercase().contains(normalizedQuery)
			}
		}

		_uiState.update { it.copy(addresses = filtered) }
	}

	private fun mapAddress(address: Address): AddressUi {
		val firstLine = listOfNotNull(address.street, address.suite)
			.joinToString(separator = ", ")

		return AddressUi(
			id = address.id,
			label = address.label,
			typeLabel = formatAddressType(address.type),
			line1 = firstLine,
			line2 = "${address.city}, ${address.state} ${address.zipCode}"
		)
	}

	private fun formatAddressType(type: AddressType): String {
		return when (type) {
			AddressType.OFFICE -> "Oficina"
			AddressType.BILLING -> "Facturacion"
			AddressType.OPERATIONS -> "Operaciones"
			AddressType.WAREHOUSE -> "Almacen"
		}
	}

	private fun buildInitials(name: String): String {
		return name
			.trim()
			.split(" ")
			.filter { it.isNotBlank() }
			.take(2)
			.joinToString(separator = "") { part -> part.first().uppercase() }
			.ifBlank { "--" }
	}

	private fun accountLabelFor(status: ClientStatus): String {
		return when (status) {
			ClientStatus.ACTIVE -> "Cuenta empresarial"
			ClientStatus.PENDING -> "Cuenta pendiente"
			ClientStatus.INACTIVE -> "Cuenta inactiva"
		}
	}

}