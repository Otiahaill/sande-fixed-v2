package com.example.sande.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class AuthRepository {
    private val _session = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = _session.asStateFlow()

    data class Session(val uid: String, val email: String?, val idToken: String?)

    suspend fun signIn(email: String, password: String): Session {
        withContext(Dispatchers.IO) { Thread.sleep(600) }
        val session = Session(uid = "dev-uid-001", email = email, idToken = "mock-id-token")
        _session.update { session }
        return session
    }

    suspend fun signOut() {
        withContext(Dispatchers.IO) { firebaseSignOut() }
        _session.update { null }
    }

    private suspend fun firebaseSignOut() = withContext(Dispatchers.IO) { Thread.sleep(200) }

    val currentUser: String? get() = _session.value?.uid
    val isSignedIn: Boolean get() = _session.value != null
}
