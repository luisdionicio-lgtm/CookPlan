package com.tecsup.cookplan.ui.planner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.cookplan.CookPlanApplication
import com.tecsup.cookplan.viewmodel.PlannerViewModel
import com.tecsup.cookplan.viewmodel.PlannerViewModelFactory

@Composable
fun PlannerScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as CookPlanApplication).mealPlanRepository
    val viewModel: PlannerViewModel = viewModel(factory = PlannerViewModelFactory(repository))

    val uiState by viewModel.uiState.collectAsState()
    val days = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Planificador Semanal", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(days) { day ->
                FilterChip(
                    selected = uiState.selectedDay == day,
                    onClick = { viewModel.selectDay(day) },
                    label = { Text(day) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PlannerCard("Desayuno", uiState.breakfast) { viewModel.removeMeal("Desayuno") }
        Spacer(modifier = Modifier.height(8.dp))
        PlannerCard("Almuerzo", uiState.lunch) { viewModel.removeMeal("Almuerzo") }
        Spacer(modifier = Modifier.height(8.dp))
        PlannerCard("Cena", uiState.dinner) { viewModel.removeMeal("Cena") }
    }
}

@Composable
fun PlannerCard(type: String, recipeName: String, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(type, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Text(recipeName.ifEmpty { "Sin asignar" }, style = MaterialTheme.typography.bodyLarge)
            }
            if (recipeName.isNotEmpty()) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Close, contentDescription = "Quitar")
                }
            }
        }
    }
}
