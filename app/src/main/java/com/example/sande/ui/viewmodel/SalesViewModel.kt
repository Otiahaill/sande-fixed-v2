package com.example.sande.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sande.data.entities.Product
import com.example.sande.data.repository.MovementRepository
import com.example.sande.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class SalesViewModel(
    private val movementRepository: MovementRepository,
    productRepository: ProductRepository
) : ViewModel() {

    data class CartItem(
        val productId: String,
        val productName: String,
        val quantity: Long,
        val unitPriceCents: Long
    )

    val products: StateFlow<List<Product>> =
        productRepository.getAllActive.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
        )

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    fun addToCart(product: Product) {
        _cart.update { items ->
            val existing = items.find { it.productId == product.id }
            if (existing == null) {
                items + CartItem(product.id, product.name, 1L, product.priceCents)
            } else {
                items.map { if (it.productId == product.id) it.copy(quantity = it.quantity + 1) else it }
            }
        }
    }

    fun removeFromCart(productId: String) {
        _cart.update { items -> items.filterNot { it.productId == productId } }
    }

    fun subtotalCents(items: List<CartItem>): Long = items.sumOf { it.quantity * it.unitPriceCents }

    /**
     * PLACEHOLDER: writes one SALE movement per line. NOT atomic and no sale header yet.
     * The blueprint (M6) requires one Room transaction for sale + lines + movements + outbox row.
     */
    fun recordSale() {
        val items = _cart.value
        if (items.isEmpty()) return
        val saleId = UUID.randomUUID().toString()
        viewModelScope.launch {
            items.forEach { line ->
                movementRepository.recordSale(
                    productId = line.productId,
                    quantity = line.quantity,
                    unitPriceCents = line.unitPriceCents,
                    referenceId = saleId,
                    deviceId = "local"
                )
            }
            _cart.value = emptyList()
        }
    }
}
