package com.jhosenthg.oriontek.presentation.screens.clients

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jhosenthg.oriontek.domain.entities.Address
import com.jhosenthg.oriontek.domain.entities.AddressType
import com.jhosenthg.oriontek.domain.entities.Client
import com.jhosenthg.oriontek.domain.entities.ClientStatus
import com.jhosenthg.oriontek.domain.entities.Contact
import com.jhosenthg.oriontek.presentation.screens.clients.components.ClientScreenContent
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
		imageUrl = "",
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
		imageUrl = "",
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
