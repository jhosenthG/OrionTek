package com.jhosenthg.oriontek.presentation.screens.clients.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jhosenthg.oriontek.domain.entities.Client
import com.jhosenthg.oriontek.presentation.screens.clients.ClientViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ClientScreenContent(
	uiState: ClientViewModel.UiState,
	modifier: Modifier = Modifier,
	onSearchQueryChange: (String) -> Unit,
	onFilterSelected: (ClientViewModel.ClientFilter) -> Unit,
	onRefresh: () -> Unit,
	onClientClick: (Client) -> Unit
) {
	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = "OrionTek",
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold
					)
				},
				actions = {
					IconButton(onClick = onRefresh) {
						Icon(
							imageVector = Icons.Outlined.Refresh,
							contentDescription = "Actualizar clientes"
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface,
					titleContentColor = MaterialTheme.colorScheme.onSurface,
					actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
				)
			)
		}
	) { innerPadding ->
		when {
			uiState.isLoading && uiState.clients.isEmpty() -> {
				LoadingContent(modifier = Modifier.padding(innerPadding))
			}

			uiState.errorMessage != null && uiState.clients.isEmpty() -> {
				ErrorContent(
					message = uiState.errorMessage,
					modifier = Modifier.padding(innerPadding),
					onRetry = onRefresh
				)
			}

			else -> {
				LazyVerticalGrid(
					columns = GridCells.Adaptive(minSize = 280.dp),
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentPadding = PaddingValues(16.dp),
					verticalArrangement = Arrangement.spacedBy(16.dp),
					horizontalArrangement = Arrangement.spacedBy(16.dp)
				) {
					item(span = { GridItemSpan(maxLineSpan) }) {
						SearchAndFilterSection(
							searchQuery = uiState.searchQuery,
							selectedFilter = uiState.selectedFilter,
							onSearchQueryChange = onSearchQueryChange,
							onFilterSelected = onFilterSelected
						)
					}

					item(span = { GridItemSpan(maxLineSpan) }) {
						HeaderSection(clientCount = uiState.clients.size)
					}

					if (uiState.clients.isEmpty()) {
						item(span = { GridItemSpan(maxLineSpan) }) {
							EmptyContent(
								isFiltering = uiState.searchQuery.isNotBlank() ||
									uiState.selectedFilter != ClientViewModel.ClientFilter.ALL,
								modifier = Modifier.padding(top = 8.dp)
							)
						}
					} else {
						items(
							items = uiState.clients,
							key = { client -> client.id }
						) { client ->
							ClientCard(
								client = client,
								onClick = { onClientClick(client) }
							)
						}
					}
				}
			}
		}
	}
}

