package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cookplan.data.local.MealPlanEntity
import com.tecsup.cookplan.data.local.RecipeEntity
import com.tecsup.cookplan.data.repository.MealPlanRepository
import com.tecsup.cookplan.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val recipe: RecipeEntity? = null,
    val error: String? = null
)

class RecipeDetailViewModel(
    private val repository: RecipeRepository,
    private val mealPlanRepository: MealPlanRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(id: Long) {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState(isLoading = true)
            val result = repository.getRecipeById(id)
            if (result != null) {
                _uiState.value = RecipeDetailUiState(recipe = result)
            } else {
                _uiState.value = RecipeDetailUiState(error = "Receta no encontrada")
            }
        }
    }

    fun deleteRecipe(onComplete: () -> Unit) {
        val currentRecipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            repository.deleteRecipe(currentRecipe)
            onComplete()
        }
    }

    // Asigna la receta actual a un día y tipo de comida del planificador (RF-07).
    fun assignToPlan(day: String, mealType: String, onDone: () -> Unit) {
        val recipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            mealPlanRepository.assignMeal(
                MealPlanEntity(
                    dayOfWeek = day,
                    mealType = mealType,
                    recipeId = recipe.id,
                    recipeName = recipe.name
                )
            )
            onDone()
        }
    }
}

class RecipeDetailViewModelFactory(
    private val repository: RecipeRepository,
    private val mealPlanRepository: MealPlanRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeDetailViewModel(repository, mealPlanRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
