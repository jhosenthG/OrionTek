package com.jhosenthg.oriontek.presentation.screens.clients.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jhosenthg.oriontek.presentation.screens.clients.ClientViewModel

@Composable
fun SearchAndFilterSection(
	searchQuery: String,
	selectedFilter: ClientViewModel.ClientFilter,
	onSearchQueryChange: (String) -> Unit,
	onFilterSelected: (ClientViewModel.ClientFilter) -> Unit
) {
	val chipScrollState = rememberScrollState()

	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		OutlinedTextField(
			value = searchQuery,
			onValueChange = onSearchQueryChange,
			modifier = Modifier.fillMaxWidth(),
			singleLine = true,
			placeholder = {
				Text("Buscar cliente")
			},
			leadingIcon = {
				Icon(
					imageVector = Icons.Outlined.Search,
					contentDescription = null
				)
			},
			shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.horizontalScroll(chipScrollState),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically
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
fun HeaderSection(clientCount: Int) {
	Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
		Text(
						text = "Directorio de clientes",
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

