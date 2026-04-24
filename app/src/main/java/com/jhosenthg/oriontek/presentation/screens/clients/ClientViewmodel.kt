package com.jhosenthg.oriontek.presentation.screens.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhosenthg.oriontek.domain.entities.Client
import com.jhosenthg.oriontek.domain.entities.ClientStatus
import com.jhosenthg.oriontek.domain.useCase.GetClientsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.PrecisionManufacturing
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ClientViewModel @Inject constructor(
	private val getClientsUseCase: GetClientsUseCase
) : ViewModel() {

	data class UiState(
		val isLoading: Boolean = false,
		val searchQuery: String = "",
		val selectedFilter: ClientFilter = ClientFilter.ALL,
		val clients: List<Client> = emptyList(),
		val errorMessage: String? = null
	)

	enum class ClientFilter(val label: String) {
		ALL("Todos los clientes"),
		ACTIVE("Contratos activos"),
		RECENT("Actualizaciones recientes")
	}

	private val _uiState = MutableStateFlow(UiState(isLoading = true))
	val uiState: StateFlow<UiState> = _uiState.asStateFlow()

	private var allClients: List<Client> = emptyList()

	init {
		loadClients()
	}

	fun onSearchQueryChange(query: String) {
		_uiState.update { currentState ->
			currentState.copy(searchQuery = query)
		}
		applyFilters()
	}

	fun onFilterSelected(filter: ClientFilter) {
		_uiState.update { currentState ->
			currentState.copy(selectedFilter = filter)
		}
		applyFilters()
	}

	fun refresh() {
		loadClients()
	}

	private fun loadClients() {
		viewModelScope.launch {
			_uiState.update { currentState ->
				currentState.copy(isLoading = true, errorMessage = null)
			}

			getClientsUseCase().collect { result ->
				result.onSuccess { clients ->
					allClients = clients
					_uiState.update { currentState ->
						currentState.copy(isLoading = false, errorMessage = null)
					}
					applyFilters()
				}.onFailure { throwable ->
					allClients = emptyList()
					_uiState.update { currentState ->
						currentState.copy(
							isLoading = false,
							clients = emptyList(),
							errorMessage = throwable.message ?: "No se pudieron cargar los clientes."
						)
					}
				}
			}
		}
	}

	private fun applyFilters() {
		val currentState = _uiState.value
		val filteredClients = allClients.filter { client ->
			matchesFilter(client, currentState.selectedFilter) &&
				matchesSearch(client, currentState.searchQuery)
		}

		_uiState.update { state ->
			state.copy(clients = filteredClients)
		}
	}

	private fun matchesFilter(client: Client, filter: ClientFilter): Boolean {
		return when (filter) {
			ClientFilter.ALL -> true
			ClientFilter.ACTIVE -> client.status == ClientStatus.ACTIVE
			ClientFilter.RECENT -> client.recentOrderData.isNotEmpty()
		}
	}

	private fun matchesSearch(client: Client, query: String): Boolean {
		if (query.isBlank()) return true

		val normalizedQuery = query.trim().lowercase()
		return client.name.lowercase().contains(normalizedQuery) ||
			client.id.lowercase().contains(normalizedQuery) ||
			client.primaryContact.name.lowercase().contains(normalizedQuery)
	}

	fun getClientIcon(client: Client): ImageVector {
		val industry = client.industry.lowercase()
		return when {
			"health" in industry || "medical" in industry -> Icons.Outlined.HealthAndSafety
			"finance" in industry || "investment" in industry -> Icons.Outlined.AccountBalance
			"logistic" in industry || "supply" in industry -> Icons.Outlined.LocalShipping
			"aerospace" in industry || "venture" in industry -> Icons.Outlined.RocketLaunch
			"industry" in industry || "manufact" in industry -> Icons.Outlined.PrecisionManufacturing
			else -> Icons.Outlined.Business
		}
	}

	fun formatAddressCount(count: Int): String {
		return if (count == 1) "1 direccion registrada" else "$count direcciones registradas"
	}
}
