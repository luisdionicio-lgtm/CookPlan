package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cookplan.data.local.RecipeEntity
import com.tecsup.cookplan.data.repository.RecipeRepository
import kotlinx.coroutines.flow.*

data class RecipeListUiState(
    val isLoading: Boolean = false,
    val recipes: List<RecipeEntity> = emptyList(),
    val searchQuery: String = "",
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null
)

class RecipeListViewModel(private val repository: RecipeRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)

    // Combina la lista de Room con el texto buscado y la categoría seleccionada.
    // Filtramos en memoria (la colección local es pequeña) y derivamos las
    // categorías disponibles desde las propias recetas (RF-06).
    val uiState: StateFlow<RecipeListUiState> = combine(
        repository.allRecipes,
        _searchQuery,
        _selectedCategory
    ) { all, query, category ->
        val categories = all.mapNotNull { it.category?.takeIf { c -> c.isNotBlank() } }
            .distinct()
            .sorted()
        val filtered = all.filter { recipe ->
            (query.isBlank() || recipe.name.contains(query, ignoreCase = true)) &&
                (category == null || recipe.category == category)
        }
        RecipeListUiState(
            recipes = filtered,
            searchQuery = query,
            categories = categories,
            selectedCategory = category
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecipeListUiState(isLoading = true)
    )

    fun onSearchQueryChange(newQuery: String) { _searchQuery.value = newQuery }

    fun onCategorySelected(category: String?) { _selectedCategory.value = category }
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
