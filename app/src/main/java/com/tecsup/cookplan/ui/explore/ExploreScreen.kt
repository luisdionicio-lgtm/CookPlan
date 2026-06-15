package com.tecsup.cookplan.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.cookplan.viewmodel.ExploreUiState
import com.tecsup.cookplan.viewmodel.ExploreViewModel

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Explorar Recetas Online", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar en TheMealDB...") },
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
                if (state.externalRecipes.isEmpty()) {
                    Text("Ingresa una palabra clave para buscar recetas.")
                } else {
                    // Lista de resultados
                }
            }
            is ExploreUiState.Error -> {
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
