package com.example.sande.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sande.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: String? get() = authRepository.currentUser
    val isSignedIn: Boolean get() = authRepository.isSignedIn

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }
}
