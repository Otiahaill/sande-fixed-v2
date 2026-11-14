package com.example.sande.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sande.di.SandeApplication
import com.example.sande.ui.EmptyStateCard
import com.example.sande.ui.theme.SandeShape
import com.example.sande.ui.viewModelFactoryOf
import com.example.sande.ui.viewmodel.StockViewModel

@Composable
fun StockScreen(modifier: Modifier = Modifier) {
    val app = LocalContext.current.applicationContext as SandeApplication
    val vm: StockViewModel = viewModel(
        factory = viewModelFactoryOf { StockViewModel(app.movementRepository, app.productRepository) }
    )
    val rows by vm.rows.collectAsState()
    var receiving by remember { mutableStateOf<StockViewModel.StockRow?>(null) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Stock", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Tap a product to receive stock",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (rows.isEmpty()) {
            EmptyStateCard(
                title = "No products",
                subtitle = "Add products in the Catalogue first",
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rows, key = { it.product.id }) { row ->
                    val status = when {
                        row.balance <= 0 -> "Out of stock"
                        row.balance <= 5 -> "Low stock"
                        else -> "In stock"
                    }
                    val statusColor = when {
                        row.balance <= 0 -> MaterialTheme.colorScheme.error
                        row.balance <= 5 -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.primary
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { receiving = row },
                        shape = SandeShape.card,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(row.product.name, style = MaterialTheme.typography.bodyLarge)
                                Text(status, style = MaterialTheme.typography.labelSmall, color = statusColor)
                            }
                            Text("${row.balance} units", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }

    val target = receiving
    if (target != null) {
        var qty by remember(target.product.id) { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { receiving = null },
            title = { Text("Receive ${target.product.name}") },
            text = {
                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it.filter { c -> c.isDigit() } },
                    label = { Text("Quantity") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.receive(target.product.id, qty.toLongOrNull() ?: 0L)
                    receiving = null
                }) { Text("Receive") }
            },
            dismissButton = {
                TextButton(onClick = { receiving = null }) { Text("Cancel") }
            }
        )
    }
}
