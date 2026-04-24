package com.jhosenthg.oriontek.presentation.screens.clients

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PrecisionManufacturing
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jhosenthg.oriontek.domain.entities.Address
import com.jhosenthg.oriontek.domain.entities.AddressType
import com.jhosenthg.oriontek.domain.entities.Client
import com.jhosenthg.oriontek.domain.entities.ClientStatus
import com.jhosenthg.oriontek.domain.entities.Contact
import com.jhosenthg.oriontek.presentation.theme.OrionTekTheme

@Composable
fun ClientScreen(
	modifier: Modifier = Modifier,
	viewModel: ClientViewModel = hiltViewModel(),
	onClientClick: (Client) -> Unit = {}
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	ClientScreenContent(
		uiState = uiState,
		modifier = modifier,
		onSearchQueryChange = viewModel::onSearchQueryChange,
		onFilterSelected = viewModel::onFilterSelected,
		onRefresh = viewModel::refresh,
		onClientClick = onClientClick
	)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ClientScreenContent(
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

@Composable
private fun SearchAndFilterSection(
	searchQuery: String,
	selectedFilter: ClientViewModel.ClientFilter,
	onSearchQueryChange: (String) -> Unit,
	onFilterSelected: (ClientViewModel.ClientFilter) -> Unit
) {
	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		OutlinedTextField(
			value = searchQuery,
			onValueChange = onSearchQueryChange,
			modifier = Modifier.fillMaxWidth(),
			singleLine = true,
			placeholder = {
				Text("Search clients by name or ID...")
			},
			leadingIcon = {
				Icon(
					imageVector = Icons.Outlined.Search,
					contentDescription = null
				)
			},
			shape = RoundedCornerShape(16.dp)
		)

		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			ClientViewModel.ClientFilter.entries.forEach { filter ->
				FilterChip(
					selected = filter == selectedFilter,
					onClick = { onFilterSelected(filter) },
					label = { Text(filter.label) }
				)
			}
		}
	}
}

@Composable
private fun HeaderSection(clientCount: Int) {
	Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
		Text(
			text = "Client Directory",
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.SemiBold
		)
		Text(
			text = if (clientCount == 1) "1 cliente encontrado" else "$clientCount clientes encontrados",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
	}
}

@Composable
private fun ClientCard(
	client: Client,
	onClick: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.Top
			) {
				ClientIconBadge(client = client)

				Column(
					modifier = Modifier.weight(1f),
					verticalArrangement = Arrangement.spacedBy(4.dp)
				) {
					Text(
						text = client.name,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
					Text(
						text = client.industry,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
					Text(
						text = "ID: ${client.id}",
						style = MaterialTheme.typography.labelMedium,
						color = MaterialTheme.colorScheme.primary
					)
				}

				StatusBadge(status = client.status)
			}

			Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
				ClientMetaRow(
					icon = Icons.Outlined.LocationOn,
					text = formatAddressCount(client.addresses.size)
				)
				ClientMetaRow(
					icon = Icons.Outlined.Business,
					text = client.primaryContact.name
				)
			}

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = client.primaryContact.email,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.weight(1f)
				)
				Spacer(modifier = Modifier.size(12.dp))
				Icon(
					imageVector = Icons.Outlined.ChevronRight,
					contentDescription = "Ver detalle del cliente",
					tint = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}
	}
}

@Composable
private fun ClientIconBadge(client: Client) {
	val backgroundColor = when (client.status) {
		ClientStatus.ACTIVE -> MaterialTheme.colorScheme.secondaryContainer
		ClientStatus.PENDING -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)
		ClientStatus.INACTIVE -> MaterialTheme.colorScheme.surfaceVariant
	}
	val contentColor = when (client.status) {
		ClientStatus.ACTIVE -> MaterialTheme.colorScheme.onSecondaryContainer
		ClientStatus.PENDING -> MaterialTheme.colorScheme.tertiary
		ClientStatus.INACTIVE -> MaterialTheme.colorScheme.onSurfaceVariant
	}

	Box(
		modifier = Modifier
			.size(56.dp)
			.clip(RoundedCornerShape(16.dp))
			.background(backgroundColor),
		contentAlignment = Alignment.Center
	) {
		Icon(
			imageVector = iconForClient(client),
			contentDescription = null,
			tint = contentColor,
			modifier = Modifier.size(28.dp)
		)
	}
}

@Composable
private fun StatusBadge(status: ClientStatus) {
	val (containerColor, contentColor, label) = when (status) {
		ClientStatus.ACTIVE -> Triple(Color(0xFFDDFBE3), Color(0xFF15803D), "Active")
		ClientStatus.PENDING -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "Pending")
		ClientStatus.INACTIVE -> Triple(Color(0xFFE2E8F0), Color(0xFF64748B), "Inactive")
	}

	Box(
		modifier = Modifier
			.clip(CircleShape)
			.background(containerColor)
			.padding(horizontal = 10.dp, vertical = 4.dp)
	) {
		Text(
			text = label,
			style = MaterialTheme.typography.labelSmall,
			color = contentColor,
			fontWeight = FontWeight.Bold
		)
	}
}

@Composable
private fun ClientMetaRow(
	icon: ImageVector,
	text: String
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			imageVector = icon,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.size(18.dp)
		)
		Text(
			text = text,
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis
		)
	}
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		CircularProgressIndicator()
	}
}

@Composable
private fun ErrorContent(
	message: String?,
	modifier: Modifier = Modifier,
	onRetry: () -> Unit
) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.padding(24.dp),
		contentAlignment = Alignment.Center
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text(
				text = message ?: "Ocurrió un error inesperado.",
				textAlign = TextAlign.Center,
				style = MaterialTheme.typography.bodyLarge
			)
			TextButton(onClick = onRetry) {
				Text("Reintentar")
			}
		}
	}
}

@Composable
private fun EmptyContent(
	isFiltering: Boolean,
	modifier: Modifier = Modifier
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		shape = RoundedCornerShape(20.dp),
		color = MaterialTheme.colorScheme.surface,
		tonalElevation = 1.dp
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(24.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Icon(
				imageVector = Icons.Outlined.Search,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.size(32.dp)
			)
			Text(
				text = if (isFiltering) {
					"No encontramos clientes con esos criterios."
				} else {
					"No hay clientes disponibles para mostrar."
				},
				style = MaterialTheme.typography.bodyLarge,
				textAlign = TextAlign.Center
			)
		}
	}
}

private fun iconForClient(client: Client): ImageVector {
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

private fun formatAddressCount(count: Int): String {
	return if (count == 1) "1 Registered Address" else "$count Registered Addresses"
}

@Preview(showBackground = true)
@Composable
private fun ClientScreenPreview() {
	OrionTekTheme {
		ClientScreenContent(
			uiState = ClientViewModel.UiState(
				clients = sampleClients
			),
			onSearchQueryChange = {},
			onFilterSelected = {},
			onRefresh = {},
			onClientClick = {}
		)
	}
}

private val sampleClients = listOf(
	Client(
		id = "CL-001",
		name = "Aether Dynamics",
		industry = "Industrial Tech Group",
		status = ClientStatus.ACTIVE,
		primaryContact = Contact(
			name = "Jane Cooper",
			email = "jane@aether.com",
			phone = "+1 555 0101"
		),
		addresses = listOf(
			Address(
				id = "ADDR-1",
				label = "HQ",
				type = AddressType.OFFICE,
				street = "742 Evergreen Ave",
				suite = "Suite 400",
				city = "Austin",
				state = "TX",
				zipCode = "73301"
			)
		),
		recentOrderData = listOf(10, 12, 15)
	),
	Client(
		id = "CL-002",
		name = "Peak Horizon",
		industry = "Aerospace Venture",
		status = ClientStatus.PENDING,
		primaryContact = Contact(
			name = "Wade Warren",
			email = "wade@peakhorizon.com",
			phone = "+1 555 0102"
		),
		addresses = listOf(
			Address(
				id = "ADDR-2",
				label = "Operations",
				type = AddressType.OPERATIONS,
				street = "1800 Orbit Rd",
				suite = null,
				city = "Seattle",
				state = "WA",
				zipCode = "98101"
			)
		)
	)
)

