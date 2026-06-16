package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cookplan.data.local.RecipeEntity
import com.tecsup.cookplan.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecipeFormUiState(
    val id: Long = 0,
    val name: String = "",
    val ingredients: String = "",
    val instructions: String = "",
    val isSaving: Boolean = false
)

class RecipeFormViewModel(private val repository: RecipeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RecipeFormUiState())
    val uiState: StateFlow<RecipeFormUiState> = _uiState.asStateFlow()

    // Receta original al editar. Conserva campos que el formulario no muestra
    // (imageUrl, isExternal) para no perderlos al actualizar.
    private var loaded: RecipeEntity? = null

    fun onNameChange(newName: String) { _uiState.value = _uiState.value.copy(name = newName) }
    fun onIngredientsChange(newIng: String) { _uiState.value = _uiState.value.copy(ingredients = newIng) }
    fun onInstructionsChange(newIns: String) { _uiState.value = _uiState.value.copy(instructions = newIns) }

    fun loadRecipe(id: Long) {
        viewModelScope.launch {
            val recipe = repository.getRecipeById(id) ?: return@launch
            loaded = recipe
            _uiState.value = _uiState.value.copy(
                id = recipe.id,
                name = recipe.name,
                ingredients = recipe.ingredients,
                instructions = recipe.instructions
            )
        }
    }

    fun saveRecipe(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val current = _uiState.value
            // Si estamos editando, partimos de la receta original (preserva imageUrl/isExternal).
            val recipe = loaded?.copy(
                name = current.name,
                ingredients = current.ingredients,
                instructions = current.instructions
            ) ?: RecipeEntity(
                name = current.name,
                ingredients = current.ingredients,
                instructions = current.instructions
            )
            if (recipe.id == 0L) repository.insertRecipe(recipe)
            else repository.updateRecipe(recipe)

            _uiState.value = _uiState.value.copy(isSaving = false)
            onSuccess()
        }
    }
}

class RecipeFormViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeFormViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
