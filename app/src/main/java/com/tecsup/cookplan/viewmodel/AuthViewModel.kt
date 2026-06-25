package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.tecsup.cookplan.data.firebase.AuthRepository
import com.tecsup.cookplan.data.repository.CookPlanSyncRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

class AuthViewModel(
    private val repository: AuthRepository,
    private val syncRepository: CookPlanSyncRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(isLoggedIn = repository.isLoggedIn()))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        val correo = email.trim()
        if (correo.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Completa correo y contraseña.")
            return
        }

        _uiState.value = AuthUiState(isLoading = true, isLoggedIn = repository.isLoggedIn())
        repository.login(correo, password) { result ->
            result.fold(
                onSuccess = { enterAppAndSyncInBackground(onSuccess) },
                onFailure = {
                    _uiState.value = AuthUiState(
                        error = mapError(it),
                        isLoggedIn = repository.isLoggedIn()
                    )
                }
            )
        }
    }

    fun register(email: String, password: String, confirm: String, onSuccess: () -> Unit) {
        val correo = email.trim()
        if (correo.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Completa todos los campos.")
            return
        }
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(error = "La contraseña debe tener al menos 6 caracteres.")
            return
        }
        if (password != confirm) {
            _uiState.value = _uiState.value.copy(error = "Las contraseñas no coinciden.")
            return
        }

        _uiState.value = AuthUiState(isLoading = true, isLoggedIn = repository.isLoggedIn())
        repository.register(correo, password) { result ->
            result.fold(
                onSuccess = {
                    viewModelScope.launch {
                        runCatching { syncRepository.createUserProfile(correo) }
                        repository.logout()
                        _uiState.value = AuthUiState(isLoggedIn = false)
                        onSuccess()
                    }
                },
                onFailure = {
                    _uiState.value = AuthUiState(
                        error = mapError(it),
                        isLoggedIn = repository.isLoggedIn()
                    )
                }
            )
        }
    }

    val userEmail: String? get() = repository.currentUser?.email

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }

    fun clearError() {
        if (_uiState.value.error != null) {
            _uiState.value = _uiState.value.copy(error = null)
        }
    }

    private fun enterAppAndSyncInBackground(onSuccess: () -> Unit) {
        _uiState.value = AuthUiState(isLoggedIn = true)
        onSuccess()
        viewModelScope.launch {
            runCatching { syncRepository.syncUserData() }
        }
    }

    private fun mapError(e: Throwable): String = when (e) {
        is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres."
        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos."
        is FirebaseAuthInvalidUserException -> "No existe una cuenta con ese correo."
        is FirebaseAuthUserCollisionException -> "Ese correo ya está registrado."
        else -> e.localizedMessage ?: "Error de autenticación."
    }
}

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val syncRepository: CookPlanSyncRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository, syncRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
