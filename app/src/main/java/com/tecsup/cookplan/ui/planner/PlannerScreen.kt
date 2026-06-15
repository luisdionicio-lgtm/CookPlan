package com.tecsup.cookplan.ui.planner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.cookplan.viewmodel.PlannerViewModel

@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel = viewModel()
) {
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

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Desayuno", style = MaterialTheme.typography.titleMedium)
                Text(uiState.breakfast.ifEmpty { "No asignado" })
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                Text("Almuerzo", style = MaterialTheme.typography.titleMedium)
                Text(uiState.lunch.ifEmpty { "No asignado" })

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                Text("Cena", style = MaterialTheme.typography.titleMedium)
                Text(uiState.dinner.ifEmpty { "No asignado" })
            }
        }
    }
}
