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
		val activityBars: List<Float> = emptyList(),
		val activityDeltaLabel: String = "",
		val errorMessage: String? = null
	)

	private val _uiState = MutableStateFlow(UiState(isLoading = true))
	val uiState: StateFlow<UiState> = _uiState.asStateFlow()

	private var lastClientId: String? = null
	private var loadJob: Job? = null

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
					_uiState.value = mapClientToUiState(client)
				}.onFailure { throwable ->
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

	private fun mapClientToUiState(client: Client): UiState {
		val bars = toBars(client.recentOrderData)
		return UiState(
			isLoading = false,
			clientName = client.name,
			initials = buildInitials(client.name),
			accountLabel = accountLabelFor(client.status),
			primaryContact = client.primaryContact.name,
			email = client.primaryContact.email,
			phone = client.primaryContact.phone,
			addresses = client.addresses.map(::mapAddress),
			activityBars = bars,
			activityDeltaLabel = calculateDeltaLabel(client.recentOrderData),
			errorMessage = null
		)
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
		return type.name.lowercase().replaceFirstChar { it.uppercase() }
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
			ClientStatus.ACTIVE -> "Enterprise Account"
			ClientStatus.PENDING -> "Pending Account"
			ClientStatus.INACTIVE -> "Inactive Account"
		}
	}

	private fun toBars(values: List<Int>): List<Float> {
		if (values.isEmpty()) {
			return listOf(0.4f, 0.6f, 0.45f, 0.7f, 0.85f, 0.65f, 0.5f)
		}
		val maxValue = values.maxOrNull()?.coerceAtLeast(1) ?: 1
		return values.map { value -> value.toFloat() / maxValue.toFloat() }
	}

	private fun calculateDeltaLabel(values: List<Int>): String {
		if (values.size < 2 || values.first() == 0) return "Sin variacion"

		val first = values.first().toDouble()
		val last = values.last().toDouble()
		val percentage = ((last - first) / first) * 100
		val sign = if (percentage >= 0) "+" else ""
		return "$sign${"%.1f".format(percentage)}% este mes"
	}
}