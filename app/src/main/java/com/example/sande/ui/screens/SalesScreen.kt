package com.example.sande.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sande.di.SandeApplication
import com.example.sande.ui.EmptyStateCard
import com.example.sande.ui.formatKes
import com.example.sande.ui.theme.SandeShape
import com.example.sande.ui.viewModelFactoryOf
import com.example.sande.ui.viewmodel.SalesViewModel

@Composable
fun SalesScreen(modifier: Modifier = Modifier) {
    val app = LocalContext.current.applicationContext as SandeApplication
    val vm: SalesViewModel = viewModel(
        factory = viewModelFactoryOf { SalesViewModel(app.movementRepository, app.productRepository) }
    )
    val products by vm.products.collectAsState()
    val cart by vm.cart.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Sales", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Tap a product to add it to the cart",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyColumn(
            modifier = Modifier.weight(1f).padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (products.isEmpty()) {
                item {
                    EmptyStateCard("No products", "Add products in the Catalogue first")
                }
            }
            items(products, key = { "p-" + it.id }) { p ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { vm.addToCart(p) },
                    shape = SandeShape.card,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(p.name, style = MaterialTheme.typography.bodyLarge)
                        Text("KES ${formatKes(p.priceCents)}", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            if (cart.isNotEmpty()) {
                item {
                    Text(
                        "Cart",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(cart, key = { "c-" + it.productId }) { line ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(line.productName, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${line.quantity} × ${formatKes(line.unitPriceCents)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(formatKes(line.quantity * line.unitPriceCents))
                        IconButton(onClick = { vm.removeFromCart(line.productId) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total KES ${formatKes(vm.subtotalCents(cart))}", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { vm.recordSale() }, enabled = cart.isNotEmpty()) { Text("Record sale") }
        }
    }
}
