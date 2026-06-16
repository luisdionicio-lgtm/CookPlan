package com.tecsup.cookplan.ui.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.cookplan.CookPlanApplication
import com.tecsup.cookplan.viewmodel.RecipeFormViewModel
import com.tecsup.cookplan.viewmodel.RecipeFormViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeFormScreen(
    recipeId: Long? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as CookPlanApplication).recipeRepository
    val viewModel: RecipeFormViewModel = viewModel(factory = RecipeFormViewModelFactory(repository))

    val uiState by viewModel.uiState.collectAsState()

    // Si viene con id, cargamos la receta para editarla (RF-04).
    LaunchedEffect(recipeId) {
        if (recipeId != null) viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recipeId == null) "Nueva Receta" else "Editar Receta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancelar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.saveRecipe(onSuccess = onBack)
            }) {
                Icon(Icons.Default.Check, contentDescription = "Guardar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Nombre de la receta") },
                isError = uiState.nameError,
                supportingText = if (uiState.nameError) {
                    { Text("El nombre es obligatorio") }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.category,
                onValueChange = { viewModel.onCategoryChange(it) },
                label = { Text("Categoría (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.timeMinutes,
                    onValueChange = { viewModel.onTimeChange(it) },
                    label = { Text("Tiempo (min)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = uiState.servings,
                    onValueChange = { viewModel.onServingsChange(it) },
                    label = { Text("Porciones") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.ingredients,
                onValueChange = { viewModel.onIngredientsChange(it) },
                label = { Text("Ingredientes") },
                isError = uiState.ingredientsError,
                supportingText = if (uiState.ingredientsError) {
                    { Text("Los ingredientes son obligatorios") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.instructions,
                onValueChange = { viewModel.onInstructionsChange(it) },
                label = { Text("Instrucciones") },
                isError = uiState.instructionsError,
                supportingText = if (uiState.instructionsError) {
                    { Text("Las instrucciones son obligatorias") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
