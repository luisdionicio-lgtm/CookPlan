package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RecipeFormUiState(
    val name: String = "",
    val ingredients: String = "",
    val instructions: String = "",
    val isSaving: Boolean = false
)

class RecipeFormViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecipeFormUiState())
    val uiState: StateFlow<RecipeFormUiState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun saveRecipe() {
        // Logic to save
    }
}
