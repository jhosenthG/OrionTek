package com.jhosenthg.oriontek.presentation.screens.clientDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jhosenthg.oriontek.presentation.theme.OrionTekTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsContent(
    uiState: ClientDetailsViewmodel.UiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
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
            uiState.isLoading -> LoadingContent(innerPadding)
            uiState.errorMessage != null -> ErrorContent(
                message = uiState.errorMessage,
                innerPadding = innerPadding,
                onRetry = onRetry
            )

            else -> ClientDetailsBody(
                uiState = uiState,
                innerPadding = innerPadding,
                onSearchClick = onSearchClick,
                onSearchQueryChange = onSearchQueryChange,
                onEditClick = onEditClick,
                onMessageClick = onMessageClick,
                onViewMapClick = onViewMapClick,
                onAddressMenuClick = onAddressMenuClick
            )
        }
    }
}

@Composable
private fun LoadingContent(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    innerPadding: PaddingValues,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onRetry) {
            Text(text = "Reintentar")
        }
    }
}

@Composable
private fun ClientDetailsBody(
    uiState: ClientDetailsViewmodel.UiState,
    innerPadding: PaddingValues,
    onSearchClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onEditClick: () -> Unit,
    onMessageClick: () -> Unit,
    onViewMapClick: () -> Unit,
    onAddressMenuClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(
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
            AddressesSectionHeader(
                onSearchClick = onSearchClick,
                onViewMapClick = onViewMapClick
            )
        }

        if (uiState.isSearchActive) {
            item {
                AddressSearchField(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange
                )
            }
        }

        items(uiState.addresses, key = { it.id }) { address ->
            AddressCard(
                address = address,
                onMoreClick = { onAddressMenuClick(address.id) }
            )
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    uiState: ClientDetailsViewmodel.UiState,
    onEditClick: () -> Unit,
    onMessageClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(text = "Editar")
                }
                Button(
                    onClick = onMessageClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(text = "Mensaje")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
            Spacer(modifier = Modifier.height(12.dp))

            ContactInfo(title = "CONTACTO PRINCIPAL", value = uiState.primaryContact)
            Spacer(modifier = Modifier.height(10.dp))
            ContactInfo(title = "CORREO ELECTRÓNICO", value = uiState.email)
            Spacer(modifier = Modifier.height(10.dp))
            ContactInfo(title = "TELÉFONO", value = uiState.phone)
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
private fun AddressesSectionHeader(
    onSearchClick: () -> Unit,
    onViewMapClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Direcciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Buscar direcciones")
            }
        }

        TextButton(onClick = onViewMapClick) {
            Icon(
                imageVector = Icons.Outlined.Map,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Ver mapa")
        }
    }
}

@Composable
private fun AddressSearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text("Buscar dirección") }
    )
}

@Composable
private fun AddressCard(
    address: ClientDetailsViewmodel.AddressUi,
    onMoreClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                Icon(Icons.Outlined.MoreVert, contentDescription = "Más opciones")
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

@Preview(showBackground = true)
@Composable
private fun ClientDetailsContentPreview() {
    OrionTekTheme {
        ClientDetailsContent(
            uiState = ClientDetailsViewmodel.UiState(
                isLoading = false,
                clientName = "Global Logistics Corp",
                initials = "GL",
                accountLabel = "Cuenta empresarial",
                primaryContact = "Sarah Jenkins",
                email = "s.jenkins@globallogistics.com",
                phone = "+1 (555) 012-3456",
                addresses = listOf(
                    ClientDetailsViewmodel.AddressUi(
                        id = "1",
                        label = "Sede principal",
                        typeLabel = "Oficina",
                        line1 = "4500 Skyline Boulevard, Suite 200",
                        line2 = "San Francisco, CA 94105"
                    ),
                    ClientDetailsViewmodel.AddressUi(
                        id = "2",
                        label = "Departamento contable",
                        typeLabel = "Facturación",
                        line1 = "120 Corporate Way, 5th Floor",
                        line2 = "Wilmington, DE 19801"
                    )
                )
            ),
            onBackClick = {},
            onSearchClick = {},
            onSearchQueryChange = {},
            onEditClick = {},
            onMessageClick = {},
            onViewMapClick = {},
            onAddressMenuClick = {},
            onRetry = {}
        )
    }
}

