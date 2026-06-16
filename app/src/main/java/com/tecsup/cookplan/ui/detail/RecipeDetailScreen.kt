package com.tecsup.cookplan.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.cookplan.CookPlanApplication
import com.tecsup.cookplan.viewmodel.RecipeDetailViewModel
import com.tecsup.cookplan.viewmodel.RecipeDetailViewModelFactory

private val DIAS = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val COMIDAS = listOf("Desayuno", "Almuerzo", "Cena")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as CookPlanApplication
    val viewModel: RecipeDetailViewModel = viewModel(
        factory = RecipeDetailViewModelFactory(app.recipeRepository, app.mealPlanRepository)
    )

    val uiState by viewModel.uiState.collectAsState()

    var showAssignDialog by remember { mutableStateOf(false) }
    var assignMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    LaunchedEffect(assignMessage) {
        assignMessage?.let {
            snackbarHostState.showSnackbar(it)
            assignMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(uiState.recipe?.name ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Editar — se conecta en el Paso 3 */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = {
                        viewModel.deleteRecipe { onBack() }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            )
        }
    ) { padding ->
        val recipe = uiState.recipe
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (recipe != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = recipe.name, style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showAssignDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Asignar al plan")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Ingredientes", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text(text = recipe.ingredients, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Instrucciones", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text(text = recipe.instructions, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    if (showAssignDialog) {
        var selectedDay by remember { mutableStateOf(DIAS.first()) }
        var selectedMeal by remember { mutableStateOf(COMIDAS.first()) }

        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text("Asignar al plan") },
            text = {
                Column {
                    Text("Día", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DIAS.forEach { day ->
                            FilterChip(
                                selected = selectedDay == day,
                                onClick = { selectedDay = day },
                                label = { Text(day) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Comida", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        COMIDAS.forEach { meal ->
                            FilterChip(
                                selected = selectedMeal == meal,
                                onClick = { selectedMeal = meal },
                                label = { Text(meal) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.assignToPlan(selectedDay, selectedMeal) {
                        assignMessage = "Asignado: $selectedMeal del $selectedDay"
                    }
                    showAssignDialog = false
                }) {
                    Text("Asignar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
