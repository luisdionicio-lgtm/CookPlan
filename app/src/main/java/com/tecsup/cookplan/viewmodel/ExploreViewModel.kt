package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cookplan.data.local.RecipeEntity
import com.tecsup.cookplan.data.remote.dto.MealDto
import com.tecsup.cookplan.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ExploreUiState {
    object Idle : ExploreUiState()
    object Loading : ExploreUiState()
    data class Success(val externalRecipes: List<MealDto>) : ExploreUiState()
    data class Error(val message: String) : ExploreUiState()
}

class ExploreViewModel(private val repository: RecipeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Idle)
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    fun searchOnline(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = ExploreUiState.Loading
            try {
                val results = repository.searchOnline(query)
                if (results.isEmpty()) {
                    _uiState.value = ExploreUiState.Error("No se encontraron recetas. Prueba con otra palabra.")
                } else {
                    _uiState.value = ExploreUiState.Success(results)
                }
            } catch (e: Exception) {
                _uiState.value = ExploreUiState.Error("Sin conexión. Verifica tu internet e intenta de nuevo.")
            }
        }
    }

    fun importRecipe(mealDto: MealDto, onComplete: () -> Unit) {
        viewModelScope.launch {
            val entity = RecipeEntity(
                name = mealDto.name,
                category = mealDto.category,
                ingredients = mealDto.toIngredientsString(),
                instructions = mealDto.instructions ?: "",
                isExternal = true,
                imageUrl = mealDto.thumbUrl
            )
            repository.insertRecipe(entity)
            onComplete()
        }
    }
}

class ExploreViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExploreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
