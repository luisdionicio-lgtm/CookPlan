package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val recipeName: String = "",
    val recipeDescription: String = ""
)

class RecipeDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(id: Long) {
        // Mock loading
        _uiState.value = RecipeDetailUiState(recipeName = "Receta $id", recipeDescription = "Descripción de la receta $id")
    }
}
