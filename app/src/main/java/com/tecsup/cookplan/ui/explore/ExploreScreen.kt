package com.tecsup.cookplan.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.tecsup.cookplan.CookPlanApplication
import com.tecsup.cookplan.viewmodel.ExploreUiState
import com.tecsup.cookplan.viewmodel.ExploreViewModel
import com.tecsup.cookplan.viewmodel.ExploreViewModelFactory

@Composable
fun ExploreScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as CookPlanApplication).recipeRepository
    val viewModel: ExploreViewModel = viewModel(factory = ExploreViewModelFactory(repository))
    
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var importMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(importMessage) {
        importMessage?.let {
            snackbarHostState.showSnackbar(it)
            importMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Explorar Recetas Online", style = MaterialTheme.typography.headlineMedium)
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar en TheMealDB (ej. Chicken)...") },
                trailingIcon = {
                    IconButton(onClick = { viewModel.searchOnline(query) }) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is ExploreUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ExploreUiState.Success -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.externalRecipes) { meal ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                ListItem(
                                    headlineContent = { Text(meal.name) },
                                    supportingContent = { Text("Click para importar") },
                                    leadingContent = {
                                        if (!meal.thumbUrl.isNullOrBlank()) {
                                            AsyncImage(
                                                model = meal.thumbUrl,
                                                contentDescription = meal.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                            )
                                        }
                                    },
                                    trailingContent = {
                                        IconButton(onClick = {
                                            viewModel.importRecipe(meal) {
                                                importMessage = "¡${meal.name} importada!"
                                            }
                                        }) {
                                            Icon(Icons.Default.Download, contentDescription = "Importar")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                is ExploreUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    Text("Ingresa una palabra clave para buscar recetas.")
                }
            }
        }
    }
}
