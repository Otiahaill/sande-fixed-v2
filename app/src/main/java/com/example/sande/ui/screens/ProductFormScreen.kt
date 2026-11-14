package com.example.sande.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sande.di.SandeApplication
import com.example.sande.ui.theme.SandeShape
import com.example.sande.ui.viewModelFactoryOf
import com.example.sande.ui.viewmodel.ProductFormViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProductFormScreen(
    productToEdit: com.example.sande.data.entities.Product? = null,
    onProductSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    val app = context.applicationContext as com.example.sande.di.SandeApplication
    val vm: ProductFormViewModel = viewModel(
        factory = viewModelFactoryOf {
            ProductFormViewModel(app.productRepository, productToEdit)
        }
    )

    val name by vm.name
    val sku by vm.sku
    val category by vm.category
    val priceCentsStr by vm.priceCentsStr

    SandeTheme {
        Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text(text = if (productToEdit == null) "Add Product" else "Edit Product") },
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
                    text = if (productToEdit == null) "Add New Product" else "Edit Product",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

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
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { vm.name = it },
                            label = { Text(text = "Product Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = sku,
                            onValueChange = { vm.sku = it },
                            label = { Text(text = "SKU (optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = category,
                            onValueChange = { vm.category = it },
                            label = { Text(text = "Category (optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = priceCentsStr,
                            onValueChange = { vm.priceCentsStr = it },
                            label = { Text(text = "Price (KES)") },
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        vm.saveProduct()
                        onProductSaved()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = vm.isFormValid
                ) {
                    Text(text = if (productToEdit == null) "Add Product" else "Save Changes")
                }
            }
        }
    }
}

/**
 * Simple Spacer wrapper
 */
@Composable
private fun Spacer(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Spacer(modifier = modifier)
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
