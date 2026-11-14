package com.example.sande.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sande.data.repository.MovementRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.viewModelScope

class AuditViewerViewModel(
    private val movementRepository: MovementRepository
) : ViewModel() {

    val movements: StateFlow<List<com.example.sande.data.entities.Movement>> =
        movementRepository.getAll().stateIn(
            viewModelScope,
            kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )
}
