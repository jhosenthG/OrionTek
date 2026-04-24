package com.jhosenthg.oriontek.presentation.screens.clients.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jhosenthg.oriontek.domain.entities.Client
import com.jhosenthg.oriontek.domain.entities.ClientStatus

@Composable
fun ClientCard(
	client: Client,
	onClick: () -> Unit
) {
	val addressLabel = if (client.addresses.size == 1) {
		"1 direccion registrada"
	} else {
		"${client.addresses.size} direcciones registradas"
	}

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(14.dp),
				verticalAlignment = Alignment.Top
			) {
				ClientIconBadge(imageUrl = client.imageUrl, status = client.status)

				Column(
					modifier = Modifier.weight(1f),
					verticalArrangement = Arrangement.spacedBy(4.dp)
				) {
					Text(
						text = client.name,
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.SemiBold,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
						fontSize = 16.sp
					)
					Text(
						text = client.industry,
						style = MaterialTheme.typography.titleMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						maxLines = 1,
						fontSize = 12.sp,
						overflow = TextOverflow.Ellipsis
					)
				}

				StatusBadge(status = client.status)
			}

			HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					imageVector = Icons.Outlined.LocationOn,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.size(22.dp)
				)
				Text(
					text = addressLabel,
					style = MaterialTheme.typography.headlineSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.weight(1f),
					fontSize = 14.sp
				)
				Icon(
					imageVector = Icons.Outlined.ChevronRight,
					contentDescription = "Ver detalle del cliente",
					tint = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.size(26.dp)
				)
			}
		}
	}
}

@Composable
private fun ClientIconBadge(imageUrl: String?, status: ClientStatus) {
	val backgroundColor = when (status) {
		ClientStatus.ACTIVE -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f)
		ClientStatus.PENDING -> MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
		ClientStatus.INACTIVE -> MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
	}
	val contentColor = when (status) {
		ClientStatus.ACTIVE -> MaterialTheme.colorScheme.secondary
		ClientStatus.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
		ClientStatus.INACTIVE -> MaterialTheme.colorScheme.outline
	}

	Box(
		modifier = Modifier
			.size(50.dp)
			.clip(RoundedCornerShape(14.dp))
			.background(backgroundColor),
		contentAlignment = Alignment.Center
	) {
		if (!imageUrl.isNullOrBlank()) {
			AsyncImage(
				model = imageUrl,
				contentDescription = "Logo del cliente",
				modifier = Modifier.fillMaxSize(),
				contentScale = androidx.compose.ui.layout.ContentScale.Crop
			)
		} else {
			Icon(
				imageVector = Icons.Outlined.Business,
				contentDescription = null,
				tint = contentColor,
				modifier = Modifier.size(44.dp)
			)
		}
	}
}

@Composable
private fun StatusBadge(status: ClientStatus) {
	val (containerColor, contentColor, label) = when (status) {
		ClientStatus.ACTIVE -> Triple(
			MaterialTheme.colorScheme.tertiary.copy(alpha = 0.22f),
			MaterialTheme.colorScheme.secondary,
			"ACTIVO"
		)
		ClientStatus.PENDING -> Triple(
			MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
			MaterialTheme.colorScheme.onSurfaceVariant,
			"PENDIENTE"
		)
		ClientStatus.INACTIVE -> Triple(
			MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
			MaterialTheme.colorScheme.outline,
			"INACTIVO"
		)
	}

	Box(
		modifier = Modifier
			.clip(RoundedCornerShape(8.dp))
			.background(containerColor)
			.padding(horizontal = 10.dp, vertical = 4.dp)
	) {
		Text(
			text = label,
			style = MaterialTheme.typography.labelMedium,
			color = contentColor,
			fontWeight = FontWeight.Bold
		)
	}
}

