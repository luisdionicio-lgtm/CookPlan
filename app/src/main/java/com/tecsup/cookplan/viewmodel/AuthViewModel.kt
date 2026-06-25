package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.tecsup.cookplan.data.firebase.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        val correo = email.trim()
        if (correo.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Completa correo y contraseña.")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)
        repository.login(correo, password) { result ->
            result.fold(
                onSuccess = { _uiState.value = AuthUiState(); onSuccess() },
                onFailure = { _uiState.value = AuthUiState(error = mapError(it)) }
            )
        }
    }

    fun register(email: String, password: String, confirm: String, onSuccess: () -> Unit) {
        val correo = email.trim()
        if (correo.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Completa todos los campos.")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState(error = "La contraseña debe tener al menos 6 caracteres.")
            return
        }
        if (password != confirm) {
            _uiState.value = AuthUiState(error = "Las contraseñas no coinciden.")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)
        repository.register(correo, password) { result ->
            result.fold(
                onSuccess = { _uiState.value = AuthUiState(); onSuccess() },
                onFailure = { _uiState.value = AuthUiState(error = mapError(it)) }
            )
        }
    }

    val userEmail: String? get() = repository.currentUser?.email

    fun logout() = repository.logout()

    fun clearError() {
        if (_uiState.value.error != null) _uiState.value = _uiState.value.copy(error = null)
    }

    private fun mapError(e: Throwable): String = when (e) {
        is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres."
        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos."
        is FirebaseAuthInvalidUserException -> "No existe una cuenta con ese correo."
        is FirebaseAuthUserCollisionException -> "Ese correo ya está registrado."
        else -> e.localizedMessage ?: "Error de autenticación."
    }
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
