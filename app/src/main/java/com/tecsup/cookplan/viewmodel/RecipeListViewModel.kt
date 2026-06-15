package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cookplan.data.local.RecipeEntity
import com.tecsup.cookplan.data.repository.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RecipeListUiState(
    val isLoading: Boolean = false,
    val recipes: List<RecipeEntity> = emptyList(),
    val searchQuery: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeListViewModel(private val repository: RecipeRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    
    val uiState: StateFlow<RecipeListUiState> = _searchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) repository.allRecipes
            else repository.searchRecipes(query)
        }
        .map { recipes ->
            RecipeListUiState(recipes = recipes, searchQuery = _searchQuery.value)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecipeListUiState(isLoading = true)
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}

class RecipeListViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
