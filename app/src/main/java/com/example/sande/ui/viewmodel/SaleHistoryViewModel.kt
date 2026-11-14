package com.example.sande.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sande.data.repository.SaleHeaderRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.viewModelScope

class SaleHistoryViewModel(
    private val saleHeaderRepository: SaleHeaderRepository
) : ViewModel() {

    val sales: StateFlow<List<com.example.sande.data.entities.SaleHeader>> =
        saleHeaderRepository.getAll().stateIn(
            viewModelScope,
            kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )
}
