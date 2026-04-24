package com.jhosenthg.oriontek.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Client : NavKey

@Serializable
data class ClientDetail(val clientId: String) : NavKey