package com.jhosenthg.oriontek.presentation.screens.clientDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jhosenthg.oriontek.presentation.theme.OrionTekTheme
import com.jhosenthg.oriontek.presentation.theme.Slate200

@Composable
fun ClientDetailsScreen(
	clientId: String,
	modifier: Modifier = Modifier,
	viewModel: ClientDetailsViewmodel = hiltViewModel(),
	onBackClick: () -> Unit = {},
	onSearchClick: () -> Unit = {},
	onEditClick: () -> Unit = {},
	onMessageClick: () -> Unit = {},
	onViewMapClick: () -> Unit = {},
	onAddressMenuClick: (String) -> Unit = {}
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(clientId) {
		viewModel.loadClient(clientId)
	}

	ClientDetailsScreenContent(
		uiState = uiState,
		modifier = modifier,
		onBackClick = onBackClick,
		onSearchClick = onSearchClick,
		onEditClick = onEditClick,
		onMessageClick = onMessageClick,
		onViewMapClick = onViewMapClick,
		onAddressMenuClick = onAddressMenuClick,
		onRetry = viewModel::retry
	)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ClientDetailsScreenContent(
	uiState: ClientDetailsViewmodel.UiState,
	modifier: Modifier = Modifier,
	onBackClick: () -> Unit,
	onSearchClick: () -> Unit,
	onEditClick: () -> Unit,
	onMessageClick: () -> Unit,
	onViewMapClick: () -> Unit,
	onAddressMenuClick: (String) -> Unit,
	onRetry: () -> Unit
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
				navigationIcon = {
					IconButton(onClick = onBackClick) {
						Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver")
					}
				},
				actions = {
					IconButton(onClick = onSearchClick) {
						Icon(Icons.Outlined.Search, contentDescription = "Buscar")
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface,
					titleContentColor = MaterialTheme.colorScheme.onSurface,
					navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
					actionIconContentColor = MaterialTheme.colorScheme.onSurface
				)
			)
		}
	) { innerPadding ->
		when {
			uiState.isLoading -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator()
				}
			}

			uiState.errorMessage != null -> {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding)
						.padding(24.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					Text(
						text = uiState.errorMessage,
						style = MaterialTheme.typography.bodyLarge,
						textAlign = TextAlign.Center
					)
					Spacer(modifier = Modifier.height(8.dp))
					TextButton(onClick = onRetry) {
						Text(text = "Reintentar")
					}
				}
			}

			else -> {
				LazyColumn(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentPadding = androidx.compose.foundation.layout.PaddingValues(
						start = 16.dp,
						end = 16.dp,
						top = 16.dp,
						bottom = 96.dp
					),
					verticalArrangement = Arrangement.spacedBy(16.dp)
				) {
					item {
						ProfileHeaderCard(
							uiState = uiState,
							onEditClick = onEditClick,
							onMessageClick = onMessageClick
						)
					}

					item {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = "Addresses",
								style = MaterialTheme.typography.titleLarge,
								fontWeight = FontWeight.SemiBold
							)
							TextButton(onClick = onViewMapClick) {
								Icon(
									imageVector = Icons.Outlined.Map,
									contentDescription = null,
									modifier = Modifier.size(18.dp)
								)
								Spacer(modifier = Modifier.width(4.dp))
								Text(text = "View Map")
							}
						}
					}

					items(uiState.addresses, key = { it.id }) { address ->
						AddressCard(
							address = address,
							onMoreClick = { onAddressMenuClick(address.id) }
						)
					}

					item {
						Text(
							text = "Client Activity",
							style = MaterialTheme.typography.titleLarge,
							fontWeight = FontWeight.SemiBold
						)
					}

					item {
						ActivityCard(
							bars = uiState.activityBars,
							deltaLabel = uiState.activityDeltaLabel
						)
					}
				}
			}
		}
	}
}

@Composable
private fun ProfileHeaderCard(
	uiState: ClientDetailsViewmodel.UiState,
	onEditClick: () -> Unit,
	onMessageClick: () -> Unit
) {
	ElevatedCard(
		shape = RoundedCornerShape(12.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Box(
					modifier = Modifier
						.size(52.dp)
						.clip(RoundedCornerShape(10.dp))
						.background(MaterialTheme.colorScheme.primaryContainer),
					contentAlignment = Alignment.Center
				) {
					Text(
						text = uiState.initials,
						color = MaterialTheme.colorScheme.onPrimaryContainer,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold
					)
				}
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = uiState.clientName,
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.SemiBold
					)
					Row(verticalAlignment = Alignment.CenterVertically) {
						Icon(
							imageVector = Icons.Outlined.Verified,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.onSurfaceVariant,
							modifier = Modifier.size(18.dp)
						)
						Spacer(modifier = Modifier.width(4.dp))
						Text(
							text = uiState.accountLabel,
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
			}

			Spacer(modifier = Modifier.height(12.dp))
			Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				OutlinedButton(
					onClick = onEditClick,
					modifier = Modifier.weight(1f)
				) {
					Text(text = "Edit")
				}
				Button(
					onClick = onMessageClick,
					modifier = Modifier.weight(1f)
				) {
					Text(text = "Message")
				}
			}

			Spacer(modifier = Modifier.height(16.dp))
			HorizontalDivider(color = Slate200)
			Spacer(modifier = Modifier.height(12.dp))

			ContactInfo(title = "PRIMARY CONTACT", value = uiState.primaryContact)
			Spacer(modifier = Modifier.height(10.dp))
			ContactInfo(title = "EMAIL ADDRESS", value = uiState.email)
			Spacer(modifier = Modifier.height(10.dp))
			ContactInfo(title = "PHONE", value = uiState.phone)
		}
	}
}

@Composable
private fun ContactInfo(title: String, value: String) {
	Column {
		Text(
			text = title,
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.outline
		)
		Text(
			text = value,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium
		)
	}
}

@Composable
private fun AddressCard(
	address: ClientDetailsViewmodel.AddressUi,
	onMoreClick: () -> Unit
) {
	Surface(
		shape = RoundedCornerShape(12.dp),
		color = MaterialTheme.colorScheme.surface,
		tonalElevation = 1.dp,
		modifier = Modifier.fillMaxWidth()
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(14.dp),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Row(
				modifier = Modifier.weight(1f),
				horizontalArrangement = Arrangement.spacedBy(10.dp)
			) {
				Icon(
					imageVector = Icons.Outlined.LocationOn,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.secondary
				)
				Column {
					Row(
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							text = address.label,
							style = MaterialTheme.typography.titleMedium,
							fontWeight = FontWeight.SemiBold
						)
						TypeChip(text = address.typeLabel)
					}
					Text(
						text = address.line1,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
					Text(
						text = address.line2,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}

			IconButton(onClick = onMoreClick) {
				Icon(Icons.Outlined.MoreVert, contentDescription = "Mas opciones")
			}
		}
	}
}

@Composable
private fun TypeChip(text: String) {
	Surface(
		shape = RoundedCornerShape(999.dp),
		color = MaterialTheme.colorScheme.secondaryContainer
	) {
		Text(
			text = text,
			modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onSecondaryContainer,
			fontWeight = FontWeight.Bold
		)
	}
}

@Composable
private fun ActivityCard(
	bars: List<Float>,
	deltaLabel: String
) {
	ElevatedCard(
		shape = RoundedCornerShape(12.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(14.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = "Recent Orders",
					style = MaterialTheme.typography.titleSmall,
					fontWeight = FontWeight.SemiBold
				)
				Text(
					text = deltaLabel,
					style = MaterialTheme.typography.labelMedium,
					color = MaterialTheme.colorScheme.tertiary
				)
			}
			Spacer(modifier = Modifier.height(12.dp))
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.height(88.dp)
					.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
					.padding(horizontal = 10.dp, vertical = 8.dp),
				horizontalArrangement = Arrangement.spacedBy(4.dp),
				verticalAlignment = Alignment.Bottom
			) {
				val activeStartIndex = (bars.size - 2).coerceAtLeast(0)
				bars.forEachIndexed { index, value ->
					val isActive = index >= activeStartIndex
					val barColor = if (isActive) {
						MaterialTheme.colorScheme.primary
					} else {
						MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
					}

					Box(
						modifier = Modifier
							.weight(1f)
							.fillMaxWidth()
							.height((64f * value.coerceIn(0.2f, 1f)).dp)
							.clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
							.background(barColor)
					)
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun ClientDetailsScreenPreview() {
	OrionTekTheme {
		ClientDetailsScreenContent(
			uiState = ClientDetailsViewmodel.UiState(
				isLoading = false,
				clientName = "Global Logistics Corp",
				initials = "GL",
				accountLabel = "Enterprise Account",
				primaryContact = "Sarah Jenkins",
				email = "s.jenkins@globallogistics.com",
				phone = "+1 (555) 012-3456",
				addresses = listOf(
					ClientDetailsViewmodel.AddressUi(
						id = "1",
						label = "Main Headquarters",
						typeLabel = "Office",
						line1 = "4500 Skyline Boulevard, Suite 200",
						line2 = "San Francisco, CA 94105"
					),
					ClientDetailsViewmodel.AddressUi(
						id = "2",
						label = "Accounting Department",
						typeLabel = "Billing",
						line1 = "120 Corporate Way, 5th Floor",
						line2 = "Wilmington, DE 19801"
					)
				),
				activityBars = listOf(0.4f, 0.6f, 0.45f, 0.7f, 0.85f, 0.65f, 0.5f),
				activityDeltaLabel = "+12.5% este mes"
			),
			onBackClick = {},
			onSearchClick = {},
			onEditClick = {},
			onMessageClick = {},
			onViewMapClick = {},
			onAddressMenuClick = {},
			onRetry = {}
		)
	}
}
