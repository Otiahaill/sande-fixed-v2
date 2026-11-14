package com.example.sande.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sande.di.SandeApplication
import com.example.sande.data.entities.Movement
import com.example.sande.data.repository.MovementRepository
import com.example.sande.ui.EmptyStateCard
import com.example.sande.ui.theme.SandeShape
import com.example.sande.ui.viewModelFactoryOf
import com.example.sande.ui.viewmodel.AuditViewerViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuditViewerScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val app = context.applicationContext as SandeApplication
    val vm: AuditViewerViewModel = viewModel(
        factory = viewModelFactoryOf { AuditViewerViewModel(app.movementRepository) }
    )
    val movements by vm.movements.collectAsState()

    SandeTheme {
        Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text(text = "Audit Viewer") },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = modifier.fillMaxSize().padding(innerPadding)
            ) {
                Text(
                    text = "Audit Log",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (movements.isEmpty()) {
                    EmptyStateCard(
                        title = "No audit records",
                        subtitle = "Inventory movements will appear here",
                        icon = Icons.Default.History
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 8.dp)
                    ) {
                        items(movements, key = { it.id }) { movement ->
                            AuditRow(
                                movement = movement
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Single audit row
 */
@Composable
private fun AuditRow(movement: com.example.sande.data.entities.Movement) {
    val typeColor = when (movement.type) {
        com.example.sande.data.entities.MovementType.SALE -> MaterialTheme.colorScheme.primary
        com.example.sande.data.entities.MovementType.RECEIVE -> MaterialTheme.colorScheme.secondary
        com.example.sande.data.entities.MovementType.ADJUST -> MaterialTheme.colorScheme.tertiary
        com.example.sande.data.entities.MovementType.VOID -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = SandeShape.card,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                tint = typeColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movement.type.capitalize(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Product: ${movement.productId} · Qty: ${movement.quantity}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = formatSaleDate(movement.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = formatKes(movement.unitPriceCents ?: 0L * movement.quantity),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Format timestamp to readable date/time
 */
private fun formatSaleDate(timestamp: Long): String {
    val instant = java.time.Instant.ofEpochMilli(timestamp)
    val date = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
    return "${date.dayOfMonth} ${date.month.name.take(3)} ${date.hour}:${date.minute.toString().padStart(2, '0')}"
}

/**
 * Format KES
 */
private fun formatKes(cents: Long): String {
    val sign = if (cents < 0) "-" else ""
    val abs = kotlin.math.abs(cents)
    return "$sign${abs / 100}.${(abs % 100).toString().padStart(2, '0')}"
}

/**
 * Simple Spacer wrapper
 */
@Composable
private fun Spacer(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Spacer(modifier = modifier)
}

/**
 * Simple Box wrapper
 */
@Composable
private fun Box(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit = {}
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content
    )
}

/**
 * Simple Column wrapper
 */
@Composable
private fun Column(
    modifier: Modifier = Modifier,
    verticalArrangement: androidx.compose.foundation.layout.Arrangement.Vertical = androidx.compose.foundation.layout.Arrangement.Top,
    horizontalAlignment: androidx.compose.foundation.layout.Alignment.Horizontal = androidx.compose.foundation.layout.Alignment.Start,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

/**
 * Simple Row wrapper
 */
@Composable
private fun Row(
    modifier: Modifier = Modifier,
    verticalAlignment: androidx.compose.foundation.layout.Alignment.Vertical = androidx.compose.foundation.layout.Alignment.Top,
    horizontalArrangement: androidx.compose.foundation.layout.Arrangement.Horizontal = androidx.compose.foundation.layout.Arrangement.Start,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}
