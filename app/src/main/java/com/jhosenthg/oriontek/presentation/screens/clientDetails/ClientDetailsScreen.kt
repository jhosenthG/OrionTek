package com.jhosenthg.oriontek.presentation.screens.clientDetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ClientDetailsScreen(
	clientId: String,
	modifier: Modifier = Modifier,
	viewModel: ClientDetailsViewmodel = hiltViewModel(),
	onBackClick: () -> Unit = {},
	onEditClick: () -> Unit = {},
	onMessageClick: () -> Unit = {},
	onViewMapClick: () -> Unit = {},
	onAddressMenuClick: (String) -> Unit = {}
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(clientId) {
		viewModel.loadClient(clientId)
	}

	ClientDetailsContent(
		uiState = uiState,
		modifier = modifier,
		onBackClick = onBackClick,
		onSearchClick = viewModel::onSearchClick,
		onSearchQueryChange = viewModel::onSearchQueryChange,
		onEditClick = onEditClick,
		onMessageClick = onMessageClick,
		onViewMapClick = onViewMapClick,
		onAddressMenuClick = onAddressMenuClick,
		onRetry = viewModel::retry
	)
}

