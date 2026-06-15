package com.tecsup.cookplan.ui.recipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.cookplan.CookPlanApplication
import com.tecsup.cookplan.viewmodel.RecipeListViewModel
import com.tecsup.cookplan.viewmodel.RecipeListViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    onAddClick: () -> Unit,
    onRecipeClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as CookPlanApplication).recipeRepository
    val viewModel: RecipeListViewModel = viewModel(factory = RecipeListViewModelFactory(repository))

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Receta")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            TextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar recetas...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.recipes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay recetas aún. ¡Crea una!")
                }
            } else {
                LazyColumn {
                    items(uiState.recipes) { recipe ->
                        ListItem(
                            headlineContent = { Text(recipe.name) },
                            supportingContent = { Text(recipe.ingredients.take(50) + "...") },
                            modifier = Modifier.clickable { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}
