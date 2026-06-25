package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cookplan.data.local.RecipeEntity
import com.tecsup.cookplan.data.local.RecipeImageOption
import com.tecsup.cookplan.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecipeFormUiState(
    val id: Long = 0,
    val name: String = "",
    val category: String = "",
    val ingredients: String = "",
    val instructions: String = "",
    val timeMinutes: String = "",
    val servings: String = "",
    val imageUrl: String = "",
    val nameError: Boolean = false,
    val categoryError: Boolean = false,
    val ingredientsError: Boolean = false,
    val instructionsError: Boolean = false,
    val isSaving: Boolean = false
)

class RecipeFormViewModel(private val repository: RecipeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RecipeFormUiState())
    val uiState: StateFlow<RecipeFormUiState> = _uiState.asStateFlow()

    // Receta original al editar. Conserva campos que el formulario no muestra
    // (isExternal) para no perderlos al actualizar.
    private var loaded: RecipeEntity? = null

    fun onNameChange(v: String) { _uiState.value = _uiState.value.copy(name = v, nameError = false) }
    fun onCategoryChange(v: String) { _uiState.value = _uiState.value.copy(category = v, categoryError = false) }
    fun onIngredientsChange(v: String) { _uiState.value = _uiState.value.copy(ingredients = v, ingredientsError = false) }
    fun onInstructionsChange(v: String) { _uiState.value = _uiState.value.copy(instructions = v, instructionsError = false) }
    fun onImageUrlChange(v: String) { _uiState.value = _uiState.value.copy(imageUrl = v) }
    fun onImageSelected(option: RecipeImageOption) {
        val current = _uiState.value
        _uiState.value = current.copy(
            imageUrl = option.key,
            name = current.name.ifBlank { option.title },
            category = current.category.ifBlank { option.category },
            nameError = false,
            categoryError = false
        )
    }

    // Solo dígitos (o vacío) en los campos numéricos.
    fun onTimeChange(v: String) { if (v.all { it.isDigit() }) _uiState.value = _uiState.value.copy(timeMinutes = v) }
    fun onServingsChange(v: String) { if (v.all { it.isDigit() }) _uiState.value = _uiState.value.copy(servings = v) }

    fun loadRecipe(id: Long) {
        viewModelScope.launch {
            val recipe = repository.getRecipeById(id) ?: return@launch
            loaded = recipe
            _uiState.value = _uiState.value.copy(
                id = recipe.id,
                name = recipe.name,
                category = recipe.category ?: "",
                ingredients = recipe.ingredients,
                instructions = recipe.instructions,
                timeMinutes = recipe.timeMinutes?.toString() ?: "",
                servings = recipe.servings?.toString() ?: "",
                imageUrl = recipe.imageUrl ?: ""
            )
        }
    }

    fun saveRecipe(onSuccess: () -> Unit) {
        val current = _uiState.value
        // Validación de campos obligatorios (RF-01).
        val nameError = current.name.isBlank()
        val categoryError = current.category.isBlank()
        val ingredientsError = current.ingredients.isBlank()
        val instructionsError = current.instructions.isBlank()
        if (nameError || categoryError || ingredientsError || instructionsError) {
            _uiState.value = current.copy(
                nameError = nameError,
                categoryError = categoryError,
                ingredientsError = ingredientsError,
                instructionsError = instructionsError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(isSaving = true)
            val recipe = loaded?.copy(
                name = current.name,
                category = current.category,
                ingredients = current.ingredients,
                instructions = current.instructions,
                timeMinutes = current.timeMinutes.toIntOrNull(),
                servings = current.servings.toIntOrNull(),
                imageUrl = current.imageUrl.ifBlank { null }
            ) ?: RecipeEntity(
                name = current.name,
                category = current.category,
                ingredients = current.ingredients,
                instructions = current.instructions,
                timeMinutes = current.timeMinutes.toIntOrNull(),
                servings = current.servings.toIntOrNull(),
                imageUrl = current.imageUrl.ifBlank { null }
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
