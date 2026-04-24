package com.jhosenthg.oriontek.presentation.screens.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhosenthg.oriontek.domain.entities.Client
import com.jhosenthg.oriontek.domain.entities.ClientStatus
import com.jhosenthg.oriontek.domain.useCase.GetClientsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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
		ALL("All Clients"),
		ACTIVE("Active Contracts"),
		RECENT("Recent Updates")
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
}
