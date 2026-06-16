package com.tecsup.cookplan.ui.planner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.cookplan.CookPlanApplication
import com.tecsup.cookplan.viewmodel.PlannerViewModel
import com.tecsup.cookplan.viewmodel.PlannerViewModelFactory
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val DIAS = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val COMIDAS = listOf("Desayuno", "Almuerzo", "Cena")

@Composable
fun PlannerScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as CookPlanApplication
    val viewModel: PlannerViewModel = viewModel(
        factory = PlannerViewModelFactory(app.mealPlanRepository, app.recipeRepository)
    )
    val uiState by viewModel.uiState.collectAsState()

    var dialogTarget by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Semana actual (lunes a domingo) con java.time (disponible en API 26+).
    val monday = remember { LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong()) }
    val sunday = monday.plusDays(6)
    val mes = sunday.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"))
    val rango = "Semana del ${monday.dayOfMonth} al ${sunday.dayOfMonth} de $mes"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Planificador semanal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(rango, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        DIAS.forEachIndexed { index, dia ->
            val fecha = monday.plusDays(index.toLong())
            Text(
                "$dia ${fecha.dayOfMonth}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                COMIDAS.forEach { comida ->
                    val entity = uiState.plan[dia]?.get(comida)
                    MealCell(comida, entity?.recipeName) { dialogTarget = dia to comida }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    dialogTarget?.let { (dia, comida) ->
        val current = uiState.plan[dia]?.get(comida)
        AlertDialog(
            onDismissRequest = { dialogTarget = null },
            title = { Text("$dia · $comida") },
            text = {
                if (uiState.recipes.isEmpty()) {
                    Text("No tienes recetas todavía. Crea o importa alguna primero.")
                } else {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 320.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        uiState.recipes.forEach { recipe ->
                            Text(
                                recipe.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.assign(dia, comida, recipe)
                                        dialogTarget = null
                                    }
                                    .padding(vertical = 12.dp)
                            )
                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = {
                if (current != null) {
                    TextButton(onClick = {
                        viewModel.remove(dia, comida)
                        dialogTarget = null
                    }) {
                        Text("Quitar", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogTarget = null }) { Text("Cerrar") }
            }
        )
    }
}

@Composable
private fun RowScope.MealCell(label: String, recipeName: String?, onClick: () -> Unit) {
    val filled = recipeName != null
    Surface(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(78.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (filled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        border = if (filled) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (filled) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (filled) {
                Text(
                    recipeName!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text("+ Añadir", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
