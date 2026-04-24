package com.jhosenthg.oriontek.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jhosenthg.oriontek.presentation.screens.clientDetails.ClientDetailsScreen
import com.jhosenthg.oriontek.presentation.screens.clients.ClientScreen

@Composable
fun NavStack(modifier: Modifier = Modifier) {
	val backStack = rememberNavBackStack(Client)

	NavDisplay(
		modifier = modifier,
		backStack = backStack,
		entryDecorators = listOf(
			rememberSaveableStateHolderNavEntryDecorator(),
			rememberViewModelStoreNavEntryDecorator()
		),
		onBack = {
			if (backStack.size > 1) {
				backStack.removeAt(backStack.lastIndex)
			}
		},
		entryProvider = entryProvider(
			fallback = { key -> NavEntry(key) {} }
		) {
			entry<Client> {
				ClientScreen(
					onClientClick = { client ->
						backStack.add(ClientDetail(client.id))
					}
				)
			}

			entry<ClientDetail> { detail ->
				ClientDetailsScreen(
					clientId = detail.clientId,
					onBackClick = {
						if (backStack.size > 1) {
							backStack.removeAt(backStack.lastIndex)
						}
					}
				)
			}
		}
	)
}