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

    fun onNameChange(newName: String) { _uiState.value = _uiState.value.copy(name = newName) }
    fun onIngredientsChange(newIng: String) { _uiState.value = _uiState.value.copy(ingredients = newIng) }
    fun onInstructionsChange(newIns: String) { _uiState.value = _uiState.value.copy(instructions = newIns) }

    fun saveRecipe(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val recipe = RecipeEntity(
                id = _uiState.value.id,
                name = _uiState.value.name,
                ingredients = _uiState.value.ingredients,
                instructions = _uiState.value.instructions
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
