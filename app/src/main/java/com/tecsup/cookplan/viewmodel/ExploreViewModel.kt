package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ExploreUiState {
    object Loading : ExploreUiState()
    data class Success(val externalRecipes: List<String>) : ExploreUiState()
    data class Error(val message: String) : ExploreUiState()
}

class ExploreViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Success(emptyList()))
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    fun searchOnline(query: String) {
        // Logic for Retrofit later
    }
}
